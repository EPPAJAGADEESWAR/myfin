package com.myfin.customer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myfin.customer.entity.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {

	List<Loan> findByStatus(String status); 
}
