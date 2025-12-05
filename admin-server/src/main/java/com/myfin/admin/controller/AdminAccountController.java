package com.myfin.admin.controller;

import java.util.Arrays;
import java.util.List;

import com.myfin.admin.dto.AccountDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/admin-api/accounts")
public class AdminAccountController {

	private final RestTemplate restTemplate;
	private static final String CUSTOMER_BASE_URL = "http://localhost:8081";

	public AdminAccountController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@GetMapping
	public ResponseEntity<List<AccountDto>> getAccounts(@RequestParam(required = false) Long customerId) {

		String url = CUSTOMER_BASE_URL + "/admin-api/accounts";
		if (customerId != null) {
			url += "?customerId=" + customerId;
		}

		ResponseEntity<AccountDto[]> resp = restTemplate.getForEntity(url, AccountDto[].class);

		List<AccountDto> list = Arrays.asList(resp.getBody());
		return ResponseEntity.ok(list);
	}

	@PutMapping("/{id}/status")
	public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam String value) {

		String url = CUSTOMER_BASE_URL + "/admin-api/accounts/" + id + "/status?value=" + value;
		restTemplate.put(url, null);
		return ResponseEntity.ok().build();
	}
}
