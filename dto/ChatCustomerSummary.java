package com.myfin.customer.dto;

public class ChatCustomerSummary {
	private Long customerId;
	private String name;
	private long messageCount;
	private java.time.LocalDateTime lastMessageAt;

	public ChatCustomerSummary(Long customerId, String name, long messageCount, java.time.LocalDateTime lastMessageAt) {
		this.customerId = customerId;
		this.name = name;
		this.messageCount = messageCount;
		this.lastMessageAt = lastMessageAt;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public String getName() {
		return name;
	}

	public long getMessageCount() {
		return messageCount;
	}

	public java.time.LocalDateTime getLastMessageAt() {
		return lastMessageAt;
	}
}
