package com.atguigu.gmall.gateway.filter;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.gateway.properties.AuthUrlProperties;
import com.atguigu.gmall.model.user.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 响应式编程
 * @Author: LAZY
 * @Date: 2022/09/07/2022/9/7
 */
@Component
public class GlobalAuthFilter implements GlobalFilter {

    AntPathMatcher matcher = new AntPathMatcher();

    @Autowired
    AuthUrlProperties authUrlProperties;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1、获取请求路径
        String path = exchange.getRequest().getURI().getPath();
        String uri = exchange.getRequest().getURI().toString();

        //2、无需登录就能访问的资源；     直接放行。
        for (String url : authUrlProperties.getNoAuthUrl()) {
            boolean match = matcher.match(url, path);
            if (match){
                //放行
                return chain.filter(exchange);
            }
        }

        //3、只要是 /api/inner/的全部拒绝
        for (String url : authUrlProperties.getDenyUrl()) {
            boolean match = matcher.match(url, path);
            if (match){
                //响应json
                Result<String> stringResult = Result.build("", ResultCodeEnum.PERMISSION);
                return responseResult(stringResult,exchange);
            }
        }


        //4、需要登录的请求； 进行权限验证
        for (String url : authUrlProperties.getLoginAuthUrl()) {
            boolean match = matcher.match(url, path);
            if (match){
                //登录校验
                //获取请求中的token
                String token = getTokenValue(exchange);

                //3.2、 校验 token
                UserInfo userInfo = getTokenUserInfo(token);

                //3.3、验证不通过 打去登录
                if (userInfo!=null){
                    //Redis中有此用户。exchange里面的request的头会新增一个userid
                    ServerWebExchange webExchange = userIdOrTempIdTransport(userInfo,exchange);
                }else {
                    //redis中无此用户【假令牌、token没有，没登录】
                    //打去登录
                    return redirectToCustomPage(authUrlProperties.getLoginPage()+ "?originUrl="+ uri, exchange);
                }

            }
        }


        //其他请求
        String tokenValue = getTokenValue(exchange);
        UserInfo info = getTokenUserInfo(tokenValue);
        // 假的请求
        if (!StringUtils.isEmpty(tokenValue) && info == null) {
            //重定向到登录. 可以不带token,要带就得带正确
            return redirectToCustomPage(authUrlProperties.getLoginPage() + "?originUrl=" + uri, exchange);
        }

        //普通请求 直接返回
        exchange = userIdOrTempIdTransport(info, exchange);


        return chain.filter(exchange);
    }

    /**
     * 用户id、临时id透传
     * exchange里面的request的头会新增一个userid
     * @param userInfo
     * @param exchange
     * @return
     */
    private ServerWebExchange userIdOrTempIdTransport(UserInfo userInfo, ServerWebExchange exchange) {
        //请求一旦发来，所有的请求数据是固定的，不能进行任何修改，只能读取
//        ServerHttpResponse response = exchange.getResponse();
//        response.getHeaders().add("usrrId",userInfo.getId()+"");

        //请求一旦发来，所有的请求数据是固定的，不能进行任何修改，只能读取
        ServerHttpRequest.Builder newReqBuilder = exchange.getRequest().mutate();

        // 用户登录
        if(userInfo != null){
            //封装一个新的请求
            newReqBuilder //变异 将request 转为一个新的
                .header(SysRedisConst.USERID_HEADER, userInfo.getId().toString()); //添加请求头
        }
        //用户未登录
        String tempId = getUserTempId(exchange);
        newReqBuilder.header(SysRedisConst.USERTEMPID_HEADER,tempId);
        
        //放行的时候改掉exchange
        ServerWebExchange webExchange = exchange.mutate()
                .request(newReqBuilder.build())
                .response(exchange.getResponse())
                .build();
        return webExchange;

    }

    /**
     * 获取未登录用户的临时id
     * @param exchange
     * @return
     */
    private String getUserTempId(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        //tempId在请求头
        String userTempId = request.getHeaders().getFirst("userTempId");
        //请求头内不存在 在cooke内找
        if (StringUtils.isEmpty(userTempId)) {
            HttpCookie httpCookie = request.getCookies().getFirst("userTempId");
            if (httpCookie != null){
                userTempId = httpCookie.getValue();
            }
        }
        return userTempId;
    }

    /**
     * 重定向指定页面
     * @param location
     * @param exchange
     * @return
     */
    private Mono<Void> redirectToCustomPage(String location, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        //1、重定向【302状态码 + 响应头中 Location: 新位置】
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().add(HttpHeaders.LOCATION,location);

        //2、清除旧的错误的Cookie[token]（同名cookie并max-age=0）解决无限重定向问题
        ResponseCookie tokenCookie = ResponseCookie.from("token", "111")
                .maxAge(0)
                .path("/")
                .domain(".gmall.com")
                .build();
        response.getCookies().set("token",tokenCookie);
        //3、响应结束
        return response.setComplete();
    }

    /**
     * 校验 token
     * @param token
     * @return
     */
    private UserInfo getTokenUserInfo(String token) {
        String userInfoJson = stringRedisTemplate.opsForValue().get(SysRedisConst.LOGIN_USER + token);
        if (!StringUtils.isEmpty(userInfoJson)) {
            UserInfo userInfo = Jsons.toObj(userInfoJson, UserInfo.class);
            return userInfo;
        }
        return null;
    }

    private String getTokenValue(ServerWebExchange exchange) {
        String tokenStr = null;
        HttpCookie token = exchange.getRequest().getCookies().getFirst("token");
        //token在cookie
        if (token != null){
            return token.getValue();
        }
        //token在请求头
        tokenStr = exchange.getRequest().getHeaders().getFirst("token");
        return tokenStr;
    }

    /**
     * 返回响应的json
     * @param stringResult
     * @param exchange
     * @return
     */
    private Mono<Void> responseResult(Result<String> stringResult, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        String jsonStr = Jsons.toStr(stringResult);

        //DataBuffer
        DataBuffer dataBuffer = response.bufferFactory().wrap(jsonStr.getBytes());

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return response.writeWith(Mono.just(dataBuffer));

    }
}
