package com.example.jpa.repository;

import com.example.jpa.entity.LoginHistory;
import com.example.jpa.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {

    List<LoginHistory> findByAccountId(Long id);
}
