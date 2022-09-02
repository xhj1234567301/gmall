package com.atguigu.gmall.item;

import org.junit.jupiter.api.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * @Author: LAZY
 * @Date: 2022/09/01/2022/9/1
 */
public class SpelTest {

    @Test
    void test01(){
        //1.创建一个表达式解析器
        SpelExpressionParser parser = new SpelExpressionParser();
        //准备一个表达式
        String myExpression = "Hello #{1+1}";
        //得到一个表达式
        Expression expression = parser.parseExpression(myExpression, new TemplateParserContext());
        Object value = expression.getValue();
        System.out.println("value = " + value);
    }
}
