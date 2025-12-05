package com.myfin.customer.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.myfin.customer.service.LoanService;

@Controller
public class HomeController {

	private final LoanService loanService;

	public HomeController(LoanService loanService) {
		this.loanService = loanService;
	}

	@GetMapping({ "/", "/home" })
	public String home() {
		return "home"; // templates/home.html
	}

	@GetMapping("/customer/login")
	public String customerLoginPage() {
		return "customer-login"; // templates/customer-login.html
	}

	@GetMapping("/customer/register")
	public String customerRegisterPage() {
		return "customer-register"; // templates/customer-register.html (next step)
	}

	@GetMapping("/admin/login")
	public String adminLoginPage() {
		return "admin-login"; // future admin page
	}

	@GetMapping("/customer/accounts")
	public String customerAccountsPage() {
		return "customer-accounts";
	}

	@GetMapping("/customer/deposit")
	public String customerDepositPage() {
		return "customer-deposit";
	}

	@GetMapping("/customer/withdraw")
	public String customerWithdrawPage() {
		return "customer-withdraw";
	}

	@GetMapping("/customer/transfer")
	public String customerTransferPage() {
		return "customer-transfer";
	}

	@GetMapping("/customer/deposits")
	public String customerDepositsPage() {
		return "customer-deposits";
	}

	@GetMapping("/customer/loan")
	public String customerLoanPage() {
		return "customer-loan";
	}

	@GetMapping("/admin/dashboard")
	public String adminDashboardPage() {
		return "admin-dashboard";
	}

	@GetMapping("/support/chat")
	public String supportChatPage() {
		return "support-chat"; // templates/support-chat.html
	}

}
