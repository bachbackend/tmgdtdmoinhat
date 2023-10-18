package com.example.jpa.repository;

import com.example.jpa.entity.UpdateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UpdateHistoryRepository extends JpaRepository<UpdateHistory, Long> {
    List<UpdateHistory> findByPersonId(Long id);
}
