package com.myfin.customer.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.myfin.customer.dto.LoanApplyRequest;
import com.myfin.customer.entity.Account;
import com.myfin.customer.entity.Customer;
import com.myfin.customer.entity.Loan;
import com.myfin.customer.repository.AccountRepository;
import com.myfin.customer.repository.LoanRepository;

@Service
public class LoanService {

	private final LoanRepository loanRepository;
	private final AccountRepository accountRepository;

	public LoanService(LoanRepository loanRepository, AccountRepository accountRepository) {
		this.loanRepository = loanRepository;
		this.accountRepository = accountRepository;
	}

	// ------- Apply for loan --------
	public Loan applyForLoan(LoanApplyRequest request) {
		Account account = accountRepository.findById(request.getAccountId())
				.orElseThrow(() -> new RuntimeException("Account not found"));

		Customer customer = account.getCustomer();

		Loan loan = new Loan();
		loan.setAccount(account);
		loan.setCustomer(customer);
		loan.setPrincipal(request.getPrincipal());
		loan.setInterestRate(request.getAnnualInterestRate());
		loan.setTenureMonths(request.getTenureMonths());
		loan.setStatus("APPLIED");
		loan.setCreatedAt(LocalDateTime.now());

		// emiAmount null ga vadham; separate EMI API lo calculate chestham
		return loanRepository.save(loan);
	}

	// ------- EMI calculation --------
	public BigDecimal calculateEmi(Long loanId) {
		Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Loan not found"));

		BigDecimal principal = loan.getPrincipal(); // P
		BigDecimal annualRate = loan.getInterestRate(); // e.g. 12 (%)
		int n = loan.getTenureMonths(); // N (months)

		// r = annualRate / (12 * 100)
		BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12 * 100.0), 10, RoundingMode.HALF_UP);

		// (1 + r)^n
		BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
		BigDecimal pow = onePlusR.pow(n);

		// EMI = P * r * (1+r)^n / ((1+r)^n - 1)
		BigDecimal numerator = principal.multiply(monthlyRate).multiply(pow);
		BigDecimal denominator = pow.subtract(BigDecimal.ONE);

		BigDecimal emi = numerator.divide(denominator, 2, RoundingMode.HALF_UP);

		// Store EMI in DB for future use / reporting
		loan.setEmiAmount(emi);
		loanRepository.save(loan);

		return emi;
	}

	// ------- NEW: Approve loan --------
	// ------- Approve loan with balance rule --------
	public Loan approveLoan(Long loanId) {
		Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Loan not found"));

		Account account = loan.getAccount();
		if (account == null) {
			throw new RuntimeException("Loan not linked to account");
		}

		// Simple rule: balance must be at least 2x principal
		BigDecimal balance = account.getBalance(); // balance field peru getBalance() ani assume
		BigDecimal required = loan.getPrincipal().multiply(BigDecimal.valueOf(2));

		if (balance.compareTo(required) < 0) {
			// balance thakkuva → admin ki clear error message
			throw new RuntimeException("Insufficient balance. Required at least " + required + ", actual " + balance);
		}

		loan.setStatus("APPROVED");
		return loanRepository.save(loan);
	}

	// ------- NEW: Deny / reject loan --------
	public Loan denyLoan(Long loanId) {
		Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Loan not found"));

		loan.setStatus("DENIED");
		return loanRepository.save(loan);
	}
	
	// ------- Get all pending loans (status = APPLIED) --------
	public java.util.List<Loan> getPendingLoans() {
	    return loanRepository.findByStatus("APPLIED");
	}

}
