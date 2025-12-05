package com.myfin.customer.repository;

import com.myfin.customer.entity.Transaction;
import com.myfin.customer.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	List<Transaction> findByAccount(Account account);
}
