package com.myfin.customer.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long transactionId; // technical primary key

	@ManyToOne
	@JoinColumn(name = "account_id", nullable = false)
	private Account account; // main account (deposit/withdraw/from-account)

	@ManyToOne
	@JoinColumn(name = "related_account_id")
	private Account relatedAccount; // to-account for transfers (can be null)

	@Column(nullable = false)
	private String txnType; // DEPOSIT / WITHDRAW / TRANSFER / LOAN_PAYMENT

	@Column(nullable = false)
	private BigDecimal amount;

	@Column(nullable = false)
	private LocalDateTime txnTime;

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Account getRelatedAccount() {
		return relatedAccount;
	}

	public void setRelatedAccount(Account relatedAccount) {
		this.relatedAccount = relatedAccount;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public LocalDateTime getTxnTime() {
		return txnTime;
	}

	public void setTxnTime(LocalDateTime txnTime) {
		this.txnTime = txnTime;
	}
}
