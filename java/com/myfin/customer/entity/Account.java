package com.myfin.customer.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long accountId; // technical primary key

	@Column(nullable = false, unique = true)
	private String accountNumber; // business account number

	@ManyToOne
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer; // owner of this account

	@Column(nullable = false)
	private String accountType; // SAVINGS / LOAN / RD / FD

	@Column(nullable = false)
	private BigDecimal balance; // current balance

	@Column(nullable = false)
	private String status; // ACTIVE / CLOSED

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
