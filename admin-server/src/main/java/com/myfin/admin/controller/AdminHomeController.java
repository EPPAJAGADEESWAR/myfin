package com.myfin.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminHomeController {

	@GetMapping("/admin/login")
	public String adminLoginPage() {
		return "admin-login"; // src/main/resources/templates/admin-login.html
	}

	@GetMapping("/admin/dashboard")
	public String adminDashboardPage() {
		return "admin-dashboard"; // src/main/resources/templates/admin-dashboard.html
	}

	@GetMapping("/admin/register")
	public String adminRegisterPage() {
		return "admin-register"; // templates/admin-register.html
	}

	@GetMapping("/admin/customers")
	public String adminCustomersPage() {
		return "admin-customers"; // templates/admin-customers.html
	}

	@GetMapping("/admin/accounts")
	public String adminAccountsPage() {
		return "admin-accounts"; // templates/admin-accounts.html
	}
	
	@GetMapping("/admin/chats")
	public String adminChatsPage() {
	    return "admin-chats";
	}


}
