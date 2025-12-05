package com.myfin.customer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.myfin.customer.dto.ChatCustomerSummary;
import com.myfin.customer.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

	List<ChatMessage> findByCustomerCustomerIdOrderByCreatedAtAsc(Long customerId);

	@Query("""
		    select new com.myfin.customer.dto.ChatCustomerSummary(
		        c.customerId, c.name,
		        count(m),
		        max(m.createdAt)
		    )
		    from ChatMessage m
		    join m.customer c
		    where m.fromRole = 'CUSTOMER'
		    group by c.customerId, c.name
		    order by max(m.createdAt) desc
		    """)
		List<ChatCustomerSummary> findChatCustomersSummary();

}
