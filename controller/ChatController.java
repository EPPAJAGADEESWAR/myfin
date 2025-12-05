package com.myfin.customer.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.myfin.customer.entity.ChatMessage;
import com.myfin.customer.entity.Customer;
import com.myfin.customer.repository.CustomerRepository;
import com.myfin.customer.service.ChatService;

@RestController
@RequestMapping("/chats")
public class ChatController {

	private final ChatService chatService;
	private final CustomerRepository customerRepository;

	public ChatController(ChatService chatService, CustomerRepository customerRepository) {
		this.chatService = chatService;
		this.customerRepository = customerRepository;
	}

	// Current logged-in customer id from SecurityContext
	// Current logged-in customer id from SecurityContext
	private Long getCurrentCustomerId() {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    // If not logged in properly, throw error
	    if (auth == null || auth.getPrincipal() == null
	            || "anonymousUser".equals(auth.getPrincipal())) {
	        throw new RuntimeException("Not authenticated");
	    }

	    // auth.getName() = email from JWT / login
	    String email = auth.getName();
	    Customer c = customerRepository.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("Customer not found: " + email));
	    return c.getCustomerId();
	}


	// GET /chats - full conversation of current customer
	@GetMapping
	public ResponseEntity<List<ChatMessage>> getMyChats() {
		Long customerId = getCurrentCustomerId();
		return ResponseEntity.ok(chatService.getConversation(customerId));
	}

	// POST /chats - customer sends new message
	@PostMapping
	public ResponseEntity<ChatMessage> sendMessage(@RequestBody String text) {
		Long customerId = getCurrentCustomerId();
		ChatMessage saved = chatService.addCustomerMessage(customerId, text);
		return ResponseEntity.ok(saved);
	}
}
