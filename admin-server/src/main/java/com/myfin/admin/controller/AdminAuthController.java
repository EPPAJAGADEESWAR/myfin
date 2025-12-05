package com.myfin.admin.controller;

import com.myfin.admin.entity.Admin;
import com.myfin.admin.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

//REST controller for admin authentication (register, login, logout)
@RestController
@RequestMapping("/admins")
public class AdminAuthController {

	private final AdminService adminService;

	public AdminAuthController(AdminService adminService) {
		this.adminService = adminService;
	}

	@PostMapping("/register")
	public ResponseEntity<Admin> register(@RequestBody Admin admin) {
		Admin saved = adminService.register(admin);
		return ResponseEntity.ok(saved);
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
		Optional<Admin> admin = adminService.login(email, password);
		if (admin.isPresent()) {
			return ResponseEntity.ok("Admin login successful");
		} else {
			return ResponseEntity.status(401).body("Invalid admin credentials");
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout() {
		return ResponseEntity.ok("Admin logout successful");
	}

}
