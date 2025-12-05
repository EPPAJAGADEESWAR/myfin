package com.myfin.customer.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.myfin.customer.entity.ChatMessage;
import com.myfin.customer.entity.Customer;
import com.myfin.customer.repository.ChatMessageRepository;
import com.myfin.customer.repository.CustomerRepository;

@Service
public class ChatService {

	private final ChatMessageRepository chatRepo;
	private final CustomerRepository customerRepo;

	public ChatService(ChatMessageRepository chatRepo, CustomerRepository customerRepo) {
		this.chatRepo = chatRepo;
		this.customerRepo = customerRepo;
	}

	public List<ChatMessage> getConversation(Long customerId) {
		return chatRepo.findByCustomerCustomerIdOrderByCreatedAtAsc(customerId);
	}

	public ChatMessage addCustomerMessage(Long customerId, String text) {
		Customer customer = customerRepo.findById(customerId)
				.orElseThrow(() -> new RuntimeException("Customer not found"));
		ChatMessage msg = new ChatMessage();
		msg.setCustomer(customer);
		msg.setFromRole("CUSTOMER");
		msg.setMessage(text);
		msg.setCreatedAt(LocalDateTime.now());
		return chatRepo.save(msg);
	}

	public ChatMessage addAdminMessage(Long customerId, String text) {
		Customer customer = customerRepo.findById(customerId)
				.orElseThrow(() -> new RuntimeException("Customer not found"));
		ChatMessage msg = new ChatMessage();
		msg.setCustomer(customer);
		msg.setFromRole("ADMIN");
		msg.setMessage(text);
		msg.setCreatedAt(LocalDateTime.now());
		return chatRepo.save(msg);
	}
}
