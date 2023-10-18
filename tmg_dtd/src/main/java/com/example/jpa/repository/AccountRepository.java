package com.example.jpa.repository;

import com.example.jpa.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
//    Account findByUserName(String userName);
    Optional<Account> findByUserName(String userName);

    boolean existsByUserName(String userName);


}
