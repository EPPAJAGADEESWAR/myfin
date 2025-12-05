package com.myfin.customer.service;

import com.myfin.customer.dto.CustomerDto;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.myfin.customer.entity.Account;
import com.myfin.customer.entity.Customer;
import com.myfin.customer.repository.AccountRepository;
import com.myfin.customer.repository.CustomerRepository;

@Service
public class CustomerService {

	private final CustomerRepository customerRepository;
	private final AccountRepository accountRepository;

	public CustomerService(CustomerRepository customerRepository, AccountRepository accountRepository) {
		this.customerRepository = customerRepository;
		this.accountRepository = accountRepository;
	}

	public Customer register(Customer customer) {
		customer.setStatus("ACTIVE");
		Customer savedCustomer = customerRepository.save(customer);

		Account account = new Account();
		account.setAccountNumber("ACC" + savedCustomer.getCustomerId());
		account.setCustomer(savedCustomer);
		account.setAccountType("SAVINGS");
		account.setBalance(BigDecimal.ZERO);
		account.setStatus("ACTIVE");

		accountRepository.save(account);

		return savedCustomer;
	}

	public Optional<Customer> login(String email, String password) {
		return customerRepository.findByEmail(email).filter(c -> c.getPassword().equals(password));
	}

	public CustomerDto toDto(Customer customer) {
		CustomerDto dto = new CustomerDto();
		dto.setCustomerId(customer.getCustomerId());
		dto.setFullName(customer.getName());
		dto.setEmail(customer.getEmail());
		dto.setMobile(customer.getMobile());
		dto.setStatus(customer.getStatus());
		return dto;
	}

}
