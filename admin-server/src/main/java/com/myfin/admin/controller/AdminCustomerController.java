package com.myfin.admin.controller;

import java.util.Arrays;
import java.util.List;

import com.myfin.admin.dto.CustomerDto;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/admin-api/customers")
public class AdminCustomerController {

    private final RestTemplate restTemplate;
    private static final String CUSTOMER_BASE_URL = "http://localhost:8081/admin-api/customers";

    public AdminCustomerController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // GET /admin-api/customers
    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        ResponseEntity<CustomerDto[]> resp =
                restTemplate.getForEntity(CUSTOMER_BASE_URL, CustomerDto[].class);

        List<CustomerDto> list = Arrays.asList(resp.getBody());
        return ResponseEntity.ok(list);
    }

    // PUT /admin-api/customers/{id}/status?value=ACTIVE|INACTIVE
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateCustomerStatus(
            @PathVariable Long id,
            @RequestParam String value) {

        String url = CUSTOMER_BASE_URL + "/" + id + "/status?value=" + value;
        restTemplate.put(url, null);
        return ResponseEntity.ok().build();
    }

    // PUT /admin-api/customers/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCustomerDetails(
            @PathVariable Long id,
            @RequestBody CustomerDto updated) {

        String url = CUSTOMER_BASE_URL + "/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerDto> entity = new HttpEntity<>(updated, headers);

        restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
        return ResponseEntity.ok().build();
    }
}
      
