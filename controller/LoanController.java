package com.myfin.customer.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myfin.customer.dto.LoanApplyRequest;
import com.myfin.customer.entity.Loan;
import com.myfin.customer.service.LoanService;

@RestController
@RequestMapping("/loans")
public class LoanController {

	private final LoanService loanService;

	public LoanController(LoanService loanService) {
		this.loanService = loanService;
	}

	// POST /loans/apply
	@PostMapping("/apply")
	public ResponseEntity<Loan> applyForLoan(@RequestBody LoanApplyRequest request) {
		Loan loan = loanService.applyForLoan(request);
		return new ResponseEntity<>(loan, HttpStatus.CREATED);
	}

	// GET /loans/{id}/emi
	@GetMapping("/{id}/emi")
	public ResponseEntity<BigDecimal> getEmi(@PathVariable("id") Long loanId) {
		BigDecimal emi = loanService.calculateEmi(loanId);
		return ResponseEntity.ok(emi);
	}

	// POST /loans/{id}/approve
	@PostMapping("/{id}/approve")
	public ResponseEntity<Loan> approveLoan(@PathVariable("id") Long loanId) {
		Loan updated = loanService.approveLoan(loanId);
		return ResponseEntity.ok(updated);
	}

	// POST /loans/{id}/deny
	@PostMapping("/{id}/deny")
	public ResponseEntity<Loan> denyLoan(@PathVariable("id") Long loanId) {
		Loan updated = loanService.denyLoan(loanId);
		return ResponseEntity.ok(updated);
	}
	
	// GET /loans/admin/pending
	@GetMapping("/admin/pending")
	public ResponseEntity<List<Loan>> getPendingLoans() {
	    return ResponseEntity.ok(loanService.getPendingLoans());
	}

}
