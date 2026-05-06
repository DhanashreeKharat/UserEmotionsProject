package com.mentalhealth.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class SupportResponseService {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private final RestTemplate restTemplate;
    private final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    public SupportResponseService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generateResponse(String emotion, String intensity) {
        if (openAiApiKey == null || openAiApiKey.isEmpty()) {
            return fallbackResponse(emotion, intensity);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + openAiApiKey);
            headers.set("Content-Type", "application/json");

            String prompt = String.format("User feels %s with %s intensity. Give short supportive advice.", emotion, intensity);

            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-3.5-turbo");
            
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            
            body.put("messages", new Map[]{message});
            body.put("max_tokens", 100);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(OPENAI_URL, HttpMethod.POST, request, String.class);

            JSONObject jsonResponse = new JSONObject(response.getBody());
            JSONArray choices = jsonResponse.getJSONArray("choices");
            return choices.getJSONObject(0).getJSONObject("message").getString("content").trim();

        } catch (Exception e) {
            System.err.println("Error calling OpenAI API: " + e.getMessage());
            return fallbackResponse(emotion, intensity);
        }
    }

    private String fallbackResponse(String emotion, String intensity) {
        if (emotion.equals("sad")) return "I hear that you're feeling sad. It's okay to feel this way. Take things one step at a time.";
        if (emotion.equals("anger")) return "It sounds like you're really angry. Try taking some deep breaths and stepping away for a moment.";
        if (emotion.equals("fear")) return "Feeling scared is completely valid. Remember you are in a safe space right now.";
        if (emotion.equals("happy")) return "That's wonderful to hear! It's great to embrace and enjoy these positive moments.";
        return "Thank you for sharing how you feel. I am here to listen and support you.";
    }
}
