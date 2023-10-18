package com.example.jpa.repository;

import com.example.jpa.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    List<TransactionHistory> findByPersonId(Long id);

    List<TransactionHistory> findAllByPersonId(Long id);
}
