package com.mentalhealth.service;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class TranslationService {

    private final RestTemplate restTemplate;

    public TranslationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String translate(String text, String targetLangCode) {
        try {
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
            String url = "https://api.mymemory.translated.net/get?q=" + encodedText + "&langpair=en|" + targetLangCode;
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JSONObject jsonResponse = new JSONObject(response.getBody());
            
            return jsonResponse.getJSONObject("responseData").getString("translatedText");
        } catch (Exception e) {
            System.err.println("Error calling Translation API for " + targetLangCode + ": " + e.getMessage());
            return text; // fallback to original
        }
    }
}
