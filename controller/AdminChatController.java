package com.myfin.customer.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.myfin.customer.entity.ChatMessage;
import com.myfin.customer.service.ChatService;

@RestController
@RequestMapping("/admin-api/chats")
public class AdminChatController {

    private final ChatService chatService;

    public AdminChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // GET /admin-api/chats?customerId=1  -> conversation
    @GetMapping
    public ResponseEntity<List<ChatMessage>> getConversation(@RequestParam Long customerId) {
        return ResponseEntity.ok(chatService.getConversation(customerId));
    }

    // POST /admin-api/chats/reply?customerId=1  (body = plain text)
    @PostMapping("/reply")
    public ResponseEntity<ChatMessage> reply(@RequestParam Long customerId,
                                             @RequestBody String text) {
        ChatMessage saved = chatService.addAdminMessage(customerId, text);
        return ResponseEntity.ok(saved);
    }
}
