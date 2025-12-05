package com.myfin.admin.controller;

import java.util.Arrays;
import java.util.List;

import com.myfin.admin.dto.ChatMessageDto;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/admin-api/chats")
public class AdminChatController {

    private final RestTemplate restTemplate;
    private static final String CUSTOMER_BASE_URL = "http://localhost:8081/admin-api/chats";

    public AdminChatController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // GET /admin-api/chats?customerId=1
    @GetMapping
    public ResponseEntity<List<ChatMessageDto>> getConversation(@RequestParam Long customerId) {
        String url = CUSTOMER_BASE_URL + "?customerId=" + customerId;

        ResponseEntity<ChatMessageDto[]> resp =
                restTemplate.getForEntity(url, ChatMessageDto[].class);

        List<ChatMessageDto> list = Arrays.asList(resp.getBody());
        return ResponseEntity.ok(list);
    }

    // POST /admin-api/chats/reply?customerId=1
    @PostMapping("/reply")
    public ResponseEntity<ChatMessageDto> reply(
            @RequestParam Long customerId,
            @RequestBody String text) {

        String url = CUSTOMER_BASE_URL + "/reply?customerId=" + customerId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> entity = new HttpEntity<>(text, headers);

        ResponseEntity<ChatMessageDto> resp =
                restTemplate.exchange(url, HttpMethod.POST, entity, ChatMessageDto.class);

        return ResponseEntity.ok(resp.getBody());
    }
}

