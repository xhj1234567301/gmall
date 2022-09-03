package com.atguigu.gmall.search.repository;

import com.atguigu.gmall.search.bean.Person;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: LAZY
 * @Date: 2022/09/03/2022/9/3
 */
@Repository
public interface PersonRepository extends PagingAndSortingRepository<Person,Integer> {

    List<Person> findAllByAgeGreaterThan(Integer age);


    Person findPersonByName(String name);

    //DSL
    @Query("{\n" +
            "    \"match_all\": {}\n" +
            "  }")
    List<Person> aaa();
}
