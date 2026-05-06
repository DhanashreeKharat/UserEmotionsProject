package com.mentalhealth.service;

import org.json.JSONArray;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class QuoteService {

    private final RestTemplate restTemplate;

    public QuoteService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String fetchQuote() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity("https://zenquotes.io/api/random", String.class);
            JSONArray jsonArray = new JSONArray(response.getBody());
            return jsonArray.getJSONObject(0).getString("q") + " - " + jsonArray.getJSONObject(0).getString("a");
        } catch (Exception e) {
            System.err.println("Error calling Quote API: " + e.getMessage());
            return "You are stronger than you think. - Unknown";
        }
    }
}
