package com.atguigu.gmall.search.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @Author: LAZY
 * @Date: 2022/09/03/2022/9/3
 */

@Data
@Document(indexName = "person",shards = 1,replicas = 1)
public class Person {

    @Id
    private Integer id;

    @Field(type = FieldType.Keyword, index = false)
    private String name;

    @Field(type = FieldType.Integer)
    private Integer age;

}
