package com.truong.onlinebank.repository;

import com.truong.onlinebank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByAccountIdOrderByCreatedAtDesc(int accountId);
}
