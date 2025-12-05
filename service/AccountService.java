package com.myfin.customer.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.myfin.customer.dto.AccountDto;
import com.myfin.customer.dto.BalanceZeroNotificationDto;
import com.myfin.customer.dto.TransferRequest;
import com.myfin.customer.entity.Account;
import com.myfin.customer.entity.Customer;
import com.myfin.customer.entity.FixedDeposit;
import com.myfin.customer.entity.RecurringDeposit;
import com.myfin.customer.entity.Transaction;
import com.myfin.customer.repository.AccountRepository;
import com.myfin.customer.repository.CustomerRepository;
import com.myfin.customer.repository.FixedDepositRepository;
import com.myfin.customer.repository.RecurringDepositRepository;
import com.myfin.customer.repository.TransactionRepository;

@Service
public class AccountService {

	private final AccountRepository accountRepository;
	private final TransactionRepository transactionRepository;
	private final RecurringDepositRepository recurringDepositRepository;
	private final FixedDepositRepository fixedDepositRepository;
	private final CustomerRepository customerRepository;
	private final RestTemplate restTemplate;

	public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository,
			RecurringDepositRepository recurringDepositRepository, FixedDepositRepository fixedDepositRepository,
			CustomerRepository customerRepository, RestTemplate restTemplate) {
		this.accountRepository = accountRepository;
		this.transactionRepository = transactionRepository;
		this.recurringDepositRepository = recurringDepositRepository;
		this.fixedDepositRepository = fixedDepositRepository;
		this.customerRepository = customerRepository;
		this.restTemplate = restTemplate;
	}

	// --------- helper methods for ownership ---------

	private Customer getCurrentCustomer() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName(); // email from JWT
		return customerRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Logged-in customer not found"));
	}

	private void validateOwner(Account account) {
		Customer current = getCurrentCustomer();
		if (!account.getCustomer().getCustomerId().equals(current.getCustomerId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not owner of this account");
		}
	}

	// --------- Deposit ----------
	public Account deposit(Long accountId, BigDecimal amount) {
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Amount must be positive");
		}

		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new RuntimeException("Account not found"));

		validateOwner(account);

		if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
			throw new RuntimeException("Account is not active");
		}

		account.setBalance(account.getBalance().add(amount));
		Account updated = accountRepository.save(account);

		Transaction txn = new Transaction();
		txn.setAccount(updated);
		txn.setTxnType("DEPOSIT");
		txn.setAmount(amount);
		txn.setTxnTime(LocalDateTime.now());
		transactionRepository.save(txn);

		return updated;
	}

	// --------- Withdraw ----------
	public Account withdraw(Long accountId, BigDecimal amount) {
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Amount must be positive");
		}

		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new RuntimeException("Account not found"));

		validateOwner(account);

		if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
			throw new RuntimeException("Account is not active");
		}

		if (account.getBalance().compareTo(amount) < 0) {
			throw new RuntimeException("Insufficient balance");
		}

		account.setBalance(account.getBalance().subtract(amount));
		Account updated = accountRepository.save(account);

		if (updated.getBalance().compareTo(BigDecimal.ZERO) == 0) {
			notifyBalanceZero(updated);
		}

		Transaction txn = new Transaction();
		txn.setAccount(updated);
		txn.setTxnType("WITHDRAW");
		txn.setAmount(amount);
		txn.setTxnTime(LocalDateTime.now());
		transactionRepository.save(txn);

		return updated;
	}

	// --------- Account-to-account transfer ----------
	public void transfer(TransferRequest request) {
		if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Amount must be positive");
		}

		// Load accounts
		Account fromAccount = accountRepository.findById(request.getFromAccountId())
				.orElseThrow(() -> new RuntimeException("From account not found"));

		Account toAccount = accountRepository.findById(request.getToAccountId())
				.orElseThrow(() -> new RuntimeException("To account not found"));

		// fromAccount owner must be current customer
		validateOwner(fromAccount);

		// Basic status checks
		if (!"ACTIVE".equalsIgnoreCase(fromAccount.getStatus())) {
			throw new RuntimeException("From account is not active");
		}
		if (!"ACTIVE".equalsIgnoreCase(toAccount.getStatus())) {
			throw new RuntimeException("To account is not active");
		}

		BigDecimal amount = request.getAmount();

		// Sufficient balance check on source
		if (fromAccount.getBalance().compareTo(amount) < 0) {
			throw new RuntimeException("Insufficient balance in from account");
		}

		// Update balances
		fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
		toAccount.setBalance(toAccount.getBalance().add(amount));

		fromAccount = accountRepository.save(fromAccount);
		toAccount = accountRepository.save(toAccount);

		// Create DEBIT transaction on from-account
		Transaction debitTxn = new Transaction();
		debitTxn.setAccount(fromAccount);
		debitTxn.setTxnType("DEBIT");
		debitTxn.setAmount(amount);
		debitTxn.setTxnTime(LocalDateTime.now());
		transactionRepository.save(debitTxn);

		// Create CREDIT transaction on to-account
		Transaction creditTxn = new Transaction();
		creditTxn.setAccount(toAccount);
		creditTxn.setTxnType("CREDIT");
		creditTxn.setAmount(amount);
		creditTxn.setTxnTime(LocalDateTime.now());
		transactionRepository.save(creditTxn);
	}

	// --------- Transfer to Recurring Deposit (RD) ----------
	public RecurringDeposit openRecurringDeposit(Long accountId, BigDecimal amount, BigDecimal annualInterestRate,
			Integer tenureMonths) {

		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Amount must be positive");
		}

		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new RuntimeException("Account not found"));

		validateOwner(account);

		if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
			throw new RuntimeException("Account is not active");
		}

		if (account.getBalance().compareTo(amount) < 0) {
			throw new RuntimeException("Insufficient balance");
		}

		// Deduct from main account
		account.setBalance(account.getBalance().subtract(amount));
		account = accountRepository.save(account);

		// Create RD record
		RecurringDeposit rd = new RecurringDeposit();
		rd.setAccount(account);
		rd.setCustomer(account.getCustomer());
		rd.setAmount(amount);
		rd.setInterestRate(annualInterestRate);
		rd.setTenureMonths(tenureMonths);
		rd.setStatus("ACTIVE");
		rd.setCreatedAt(LocalDateTime.now());

		RecurringDeposit savedRd = recurringDepositRepository.save(rd);

		// Transaction log
		Transaction txn = new Transaction();
		txn.setAccount(account);
		txn.setTxnType("TRANSFER_TO_RD");
		txn.setAmount(amount);
		txn.setTxnTime(LocalDateTime.now());
		transactionRepository.save(txn);

		return savedRd;
	}

	// --------- Transfer to Fixed Deposit (FD) ----------
	public FixedDeposit openFixedDeposit(Long accountId, BigDecimal amount, BigDecimal annualInterestRate,
			Integer tenureMonths) {

		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Amount must be positive");
		}

		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new RuntimeException("Account not found"));

		validateOwner(account);

		if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
			throw new RuntimeException("Account is not active");
		}

		if (account.getBalance().compareTo(amount) < 0) {
			throw new RuntimeException("Insufficient balance");
		}

		// Deduct from main account
		account.setBalance(account.getBalance().subtract(amount));
		account = accountRepository.save(account);

		// Create FD record
		FixedDeposit fd = new FixedDeposit();
		fd.setAccount(account);
		fd.setCustomer(account.getCustomer());
		fd.setAmount(amount);
		fd.setInterestRate(annualInterestRate);
		fd.setTenureMonths(tenureMonths);
		fd.setStatus("ACTIVE");
		fd.setCreatedAt(LocalDateTime.now());

		FixedDeposit savedFd = fixedDepositRepository.save(fd);

		// Transaction log
		Transaction txn = new Transaction();
		txn.setAccount(account);
		txn.setTxnType("TRANSFER_TO_FD");
		txn.setAmount(amount);
		txn.setTxnTime(LocalDateTime.now());
		transactionRepository.save(txn);

		return savedFd;
	}

	public AccountDto toDto(Account account) {
		AccountDto dto = new AccountDto();
		dto.setAccountId(account.getAccountId()); // or getId()
		dto.setAccountNumber(account.getAccountNumber()); // field peru different aithe adjust
		dto.setAccountType(account.getAccountType()); // e.g. getAccountType()
		dto.setBalance(account.getBalance());
		dto.setStatus(account.getStatus());
		if (account.getCustomer() != null) {
			dto.setCustomerId(account.getCustomer().getCustomerId());
		}
		return dto;
	}

	private void notifyBalanceZero(Account account) {
		BalanceZeroNotificationDto dto = new BalanceZeroNotificationDto();
		dto.setAccountId(account.getAccountId());
		dto.setBalance(account.getBalance());
		if (account.getCustomer() != null) {
			dto.setCustomerId(account.getCustomer().getCustomerId());
			dto.setCustomerName(account.getCustomer().getName());
			dto.setEmail(account.getCustomer().getEmail());
		}

		String url = "http://localhost:8083/notifications/balance-zero";

		try {
			// POST body lo JSON DTO pampadam
			restTemplate.postForEntity(url, dto, Void.class);
		} catch (Exception ex) {
			System.out.println("Failed to call notification-service: " + ex.getMessage());
		}
	}

	public Account getPrimaryAccountForCurrentCustomer() {
	    Customer current = getCurrentCustomer();
	    return accountRepository
	            .findFirstByCustomerCustomerId(current.getCustomerId())
	            .orElseThrow(() -> new RuntimeException("No account found for current customer"));
	}


}
