package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.utils.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class TransactionService {
    public static final String API_BASE_URL = "http://localhost:8080/transaction/";
    private RestTemplate restTemplate = new RestTemplate();

    public String sendTEBucks(Transaction transaction) {
        String status = null;
        try {
            ResponseEntity<String> response = restTemplate.exchange(API_BASE_URL,
                    HttpMethod.POST, makeRequestEntity(transaction), String.class);
            status = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return status;
    }

    private HttpEntity<Void> makeRequestEntity(Transaction transaction) {
        HttpHeaders headers = new HttpHeaders();
        return new HttpEntity(transaction, headers);
    }
}
