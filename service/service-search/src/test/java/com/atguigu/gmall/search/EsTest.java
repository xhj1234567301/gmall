package com.atguigu.gmall.search;

import com.atguigu.gmall.search.bean.Person;
import com.atguigu.gmall.search.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @Author: LAZY
 * @Date: 2022/09/03/2022/9/3
 */
@SpringBootTest
public class EsTest {
    @Autowired
    PersonRepository personRepository;

    @Test
    void testAdd(){
        Person person = new Person();
        person.setId(1);
        person.setName("张三");
        person.setAge(21);

        Person person1 = new Person();
        person1.setId(2);
        person1.setName("王五");
        person1.setAge(18);

        Person person2 = new Person();
        person2.setId(3);
        person2.setName("赵丽颖");
        person2.setAge(23);

        personRepository.save(person);
        personRepository.save(person1);
        personRepository.save(person2);

    }

    @Test
    void testMethod(){
        List<Person> personList = personRepository.findAllByAgeGreaterThan(19);
        personList.forEach(System.out::println);
    }

    @Test
    void testMethod1(){
        Person person = personRepository.findPersonByName("李四");
        System.out.println("person = " + person);
    }

    @Test
    void test02(){
        List<Person> aaa = personRepository.aaa();
        aaa.forEach(System.out::println);
    }
}
