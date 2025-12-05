package com.myfin.customer.controller;

import java.util.HashMap;
import com.myfin.customer.dto.CustomerDto;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myfin.customer.entity.Customer;
import com.myfin.customer.security.JwtService;
import com.myfin.customer.service.CustomerService;

@RestController
@RequestMapping("/customers")
public class CustomerAuthController {

	private final CustomerService customerService;
	private final JwtService jwtService;

	public CustomerAuthController(CustomerService customerService, JwtService jwtService) {
		this.customerService = customerService;
		this.jwtService = jwtService;
	}

	@PostMapping("/register")
	public ResponseEntity<CustomerDto> register(@RequestBody Customer customer) {
		Customer saved = customerService.register(customer);
		CustomerDto dto = customerService.toDto(saved);
		return ResponseEntity.ok(dto); // or ResponseEntity.status(HttpStatus.CREATED).body(dto);
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@RequestParam String email, @RequestParam String password) {
		Optional<Customer> customerOpt = customerService.login(email, password);

		if (customerOpt.isPresent()) {
			Customer customer = customerOpt.get();

			// JWT token generate
			String token = jwtService.generateToken(customer.getEmail());

			Map<String, Object> response = new HashMap<>();
			response.put("message", "Login successful");
			response.put("token", token);
			response.put("customerId", customer.getCustomerId());
			response.put("email", customer.getEmail());

			return ResponseEntity.ok(response);
		} else {
			Map<String, Object> error = new HashMap<>();
			error.put("message", "Invalid email or password");
			return ResponseEntity.status(401).body(error);
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout() {
		return ResponseEntity.ok("Logout successful");
	}
}
