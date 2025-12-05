package com.myfin.customer.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myfin.customer.dto.ChatCustomerSummary;
import com.myfin.customer.entity.Customer;
import com.myfin.customer.repository.ChatMessageRepository;
import com.myfin.customer.repository.CustomerRepository;

@RestController
@RequestMapping("/admin-api")
public class AdminCustomerController {

	private final CustomerRepository customerRepository;
	private final ChatMessageRepository chatMessageRepository;

	public AdminCustomerController(CustomerRepository customerRepository, ChatMessageRepository chatMessageRepository) {
		this.customerRepository = customerRepository;
		this.chatMessageRepository = chatMessageRepository;
	}

	// GET http://localhost:8081/admin-api/customers
	// GET http://localhost:8081/admin-api/customers
	@GetMapping("/customers")
	public List<Customer> getAllCustomers() {
		return customerRepository.findAll();
	}

	@PutMapping("/customers/{id}/status")
	public Customer updateCustomerStatus(@PathVariable Long id, @RequestParam String value) {

		Customer customer = customerRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Customer not found: " + id));

		customer.setStatus(value); // value = "ACTIVE" or "INACTIVE"
		return customerRepository.save(customer);
	}

	@PutMapping("/customers/{id}")
	public Customer updateCustomerDetails(@PathVariable Long id, @RequestBody Customer updated) {

		Customer existing = customerRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Customer not found: " + id));

		// Only editable fields
		existing.setName(updated.getName());
		existing.setEmail(updated.getEmail());
		existing.setMobile(updated.getMobile());
		existing.setAddress(updated.getAddress());
		// status, password untouched

		return customerRepository.save(existing);
	}
}
