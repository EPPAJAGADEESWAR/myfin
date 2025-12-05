package com.myfin.customer.controller;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myfin.customer.dto.AccountDto;
import com.myfin.customer.dto.TransferRequest;
import com.myfin.customer.entity.Account;
import com.myfin.customer.entity.FixedDeposit;
import com.myfin.customer.entity.RecurringDeposit;
import com.myfin.customer.service.AccountService;

@RestController
@RequestMapping
public class AccountController {

	private final AccountService accountService;

	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	// ---------- Deposit ----------
	@PostMapping("/accounts/{id}/deposit")
	public ResponseEntity<AccountDto> deposit(@PathVariable("id") Long accountId, @RequestParam BigDecimal amount) {

		Account updated = accountService.deposit(accountId, amount);
		AccountDto dto = accountService.toDto(updated);
		return ResponseEntity.ok(dto);
	}

	// ---------- Withdraw ----------
	@PostMapping("/accounts/{id}/withdraw")
	public ResponseEntity<AccountDto> withdraw(@PathVariable("id") Long accountId, @RequestParam BigDecimal amount) {

		Account updated = accountService.withdraw(accountId, amount);
		AccountDto dto = accountService.toDto(updated);
		return ResponseEntity.ok(dto);
	}

	// ---------- Account-to-account transfer ----------
	@PostMapping("/accounts/transfer")
	public ResponseEntity<Void> transfer(@RequestBody TransferRequest request) {
		accountService.transfer(request);
		return ResponseEntity.ok().build();
	}

	// ---------- Transfer to Recurring Deposit (RD) ----------
	@PostMapping("/accounts/{id}/transfer-to-rd")
	public ResponseEntity<RecurringDeposit> transferToRecurringDeposit(@PathVariable("id") Long accountId,
			@RequestParam BigDecimal amount, @RequestParam("rate") BigDecimal annualInterestRate,
			@RequestParam("months") Integer tenureMonths) {

		RecurringDeposit rd = accountService.openRecurringDeposit(accountId, amount, annualInterestRate, tenureMonths);

		return ResponseEntity.status(HttpStatus.CREATED).body(rd);
	}

	// ---------- Transfer to Fixed Deposit (FD) ----------
	@PostMapping("/accounts/{id}/transfer-to-fd")
	public ResponseEntity<FixedDeposit> transferToFixedDeposit(@PathVariable("id") Long accountId,
			@RequestParam BigDecimal amount, @RequestParam("rate") BigDecimal annualInterestRate,
			@RequestParam("months") Integer tenureMonths) {

		FixedDeposit fd = accountService.openFixedDeposit(accountId, amount, annualInterestRate, tenureMonths);

		return ResponseEntity.status(HttpStatus.CREATED).body(fd);
	}

	// ---------- Check current customer balance ----------
	@GetMapping("/accounts/me/balance")
	public ResponseEntity<BigDecimal> getMyBalance() {
		Account account = accountService.getPrimaryAccountForCurrentCustomer();
		return ResponseEntity.ok(account.getBalance());
	}

}
