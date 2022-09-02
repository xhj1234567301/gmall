package com.atguigu.starter.cache.aspect;

import com.atguigu.starter.cache.annotation.GmallCaches;
import com.atguigu.starter.cache.constant.SysRedisConst;
import com.atguigu.starter.cache.service.CacheOpsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @Author: LAZY
 * @Date: 2022/09/01/2022/9/1
 */
@Aspect //说明这是一个切面
@Component
public class CacheAspect {

    @Autowired
    CacheOpsService cacheOpsService;

    //创建一个表达式解析器 线程安全的
    ExpressionParser parser = new SpelExpressionParser();
    ParserContext context = new TemplateParserContext();

    @Around("@annotation(com.atguigu.starter.cache.annotation.GmallCaches)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        Object result = null;
        String cacheKey = determinCacheKey(joinPoint);

        //查缓存
        Type returnType =  getMethodGenericReturnType(joinPoint);
        Object cacheData = cacheOpsService.getCacheData(cacheKey,returnType);

        //2.缓存
        if (cacheData == null){
            //3.准备回源
            //4.查布隆 有些场景不需要查布隆 三级分类
            String bloomName = determinBloomName(joinPoint);
            if (!StringUtils.isEmpty(bloomName)){
                //指定开启了布隆
                Object bloomValue = determinBloomValue(joinPoint);
                boolean contains = cacheOpsService.bloomContains(bloomName,bloomValue);
                if (!contains){
                    return null;
                }
            }
            //5、布隆说有，准备回源。有击穿风险
            boolean lock = false;
            String lockName="";
            try {
                //不同场景用自己的锁
                lockName = determinLockName(joinPoint);
                //加锁
                lock = cacheOpsService.tryLock(lockName);
                if(lock){
                    //获取到锁 回源
                    result = joinPoint.proceed(joinPoint.getArgs());
                    long ttl = determinTtl(joinPoint);
                    // 调用成功 重新保存到缓存
                    cacheOpsService.saveDate(cacheKey,result,ttl);
                    return result;
                }else {
                    //等待1s 查缓存
                    Thread.sleep(1000L);
                    return cacheOpsService.getCacheData(cacheKey,returnType);
                }
            }finally {
                //解锁
                if (lock) cacheOpsService.unlock(lockName);
            }
        }

        return cacheData;
    }

    private long determinTtl(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        GmallCaches annotation = method.getAnnotation(GmallCaches.class);
        long ttl = annotation.ttl();
        return ttl;
    }

    private String determinLockName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        GmallCaches annotation = method.getAnnotation(GmallCaches.class);
        //拿到表达式
        String lockName = annotation.lockName();
        if (StringUtils.isEmpty(lockName)){
            //未指定锁
            return SysRedisConst.LOCK_PREFIX+method.getName();
        }
        //计算
        String lockNameValue = evaluationExpression(lockName, joinPoint, String.class);
        return lockNameValue;
    }

    private Object determinBloomValue(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        GmallCaches annotation = method.getAnnotation(GmallCaches.class);
        //拿到布隆值表达式
        String bloomValue = annotation.bloomValue();
        Object expression = evaluationExpression(bloomValue, joinPoint, Object.class);
        return expression;
    }

    private String determinBloomName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        GmallCaches annotation = method.getAnnotation(GmallCaches.class);
        String res = annotation.bloomName();
        return res;
    }

    private Type getMethodGenericReturnType(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Type genericReturnType = method.getGenericReturnType();
        return genericReturnType;
    }


    /**
     * 查询键值
     * @param joinPoint
     * @return
     */
    private String determinCacheKey(ProceedingJoinPoint joinPoint) {
        //1.拿到目标方法上的@GmallCaches注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //2.拿到注解
        GmallCaches cacheAnnotation = method.getDeclaredAnnotation(GmallCaches.class);
        String expression = cacheAnnotation.cacheKey();
        //3.根据表达式计算缓存键
        String cacheKey =evaluationExpression(expression,joinPoint,String.class);
        return cacheKey;

    }

    private<T> T evaluationExpression(String expression,
                                      ProceedingJoinPoint joinPoint,
                                      Class<T> clz) {
        //1.得到表达式
        Expression exp = parser.parseExpression(expression, context);
        //2.sku:info:#{#params[0]}
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        //3.取出所有参数 绑定到上下文
        Object[] args = joinPoint.getArgs();
        evaluationContext.setVariable("params",args);
        //4.得到表达式的值
        T expValue = exp.getValue(evaluationContext, clz);
        return expValue;
    }


}
