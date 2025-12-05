package com.myfin.customer.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myfin.customer.entity.Account;
import com.myfin.customer.repository.AccountRepository;

@RestController
@RequestMapping("/admin-api/accounts")
public class AdminAccountController {

	private final AccountRepository accountRepository;

	public AdminAccountController(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@GetMapping
	public ResponseEntity<List<Account>> getAccounts(@RequestParam(required = false) Long customerId) {
		List<Account> list;
		if (customerId != null) {
			list = accountRepository.findByCustomerCustomerId(customerId);
		} else {
			list = accountRepository.findAll();
		}
		return ResponseEntity.ok(list);
	}

	// PUT /admin-api/accounts/{id}/status?value=ACTIVE|BLOCKED|CLOSED
	@PutMapping("/{id}/status")
	public ResponseEntity<Account> updateStatus(@PathVariable Long id, @RequestParam String value) {
		Account acc = accountRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Account not found: " + id));

		acc.setStatus(value);
		return ResponseEntity.ok(accountRepository.save(acc));
	}
}
