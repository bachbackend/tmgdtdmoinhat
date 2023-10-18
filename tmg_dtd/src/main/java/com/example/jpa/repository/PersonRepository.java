package com.example.jpa.repository;

import com.example.jpa.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findByTotalMoneyLessThan(double totalMoney);

    List<Person> findByIdIn(List<Long> ids);

    boolean existsByEmailOrPhoneNumber(String email, String phoneNumber);

    List<Person> findByNameContaining(String name);


    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

}
