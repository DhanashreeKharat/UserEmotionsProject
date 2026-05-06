package com.mentalhealth.service;

import com.mentalhealth.model.EmotionResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmotionDetectionService {

    @Value("${huggingface.api.key}")
    private String hfApiKey;

    @Value("${huggingface.api.url}")
    private String hfApiUrl;

    private final RestTemplate restTemplate;

    public EmotionDetectionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public EmotionResult detectEmotion(String text) {
        if (hfApiKey == null || hfApiKey.isEmpty()) {
            return fallbackEmotion(text);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + hfApiKey);
            headers.set("Content-Type", "application/json");

            Map<String, String> body = new HashMap<>();
            body.put("inputs", text);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(hfApiUrl, HttpMethod.POST, request, String.class);

            // Parse response: [[{"label":"sadness","score":0.9},...]]
            JSONArray jsonArray = new JSONArray(response.getBody());
            JSONArray emotionsArray = jsonArray.getJSONArray(0);

            // Find max score
            String topEmotion = "neutral";
            double maxScore = 0.0;

            for (int i = 0; i < emotionsArray.length(); i++) {
                JSONObject obj = emotionsArray.getJSONObject(i);
                double score = obj.getDouble("score");
                if (score > maxScore) {
                    maxScore = score;
                    topEmotion = obj.getString("label");
                }
            }
            
            // Map labels to prompt expectations (sadness -> sad, joy -> happy)
            topEmotion = normalizeEmotion(topEmotion);

            return new EmotionResult(topEmotion, maxScore, calculateIntensity(maxScore));

        } catch (Exception e) {
            System.err.println("Error calling HF API: " + e.getMessage());
            return fallbackEmotion(text);
        }
    }

    private String normalizeEmotion(String label) {
        label = label.toLowerCase();
        if (label.contains("sad")) return "sad";
        if (label.contains("joy") || label.contains("happy")) return "happy";
        if (label.contains("anger") || label.contains("angry")) return "anger";
        if (label.contains("fear")) return "fear";
        return "neutral";
    }

    private String calculateIntensity(double score) {
        if (score > 0.75) return "High";
        if (score > 0.40) return "Medium";
        return "Low";
    }

    private EmotionResult fallbackEmotion(String text) {
        // Fallback simple keyword detection if API fails or key is missing
        String lowerText = text.toLowerCase();
        if (lowerText.contains("sad") || lowerText.contains("depressed")) {
            return new EmotionResult("sad", 0.8, calculateIntensity(0.8));
        } else if (lowerText.contains("happy") || lowerText.contains("joy")) {
            return new EmotionResult("happy", 0.8, calculateIntensity(0.8));
        } else if (lowerText.contains("angry") || lowerText.contains("mad")) {
            return new EmotionResult("anger", 0.8, calculateIntensity(0.8));
        } else if (lowerText.contains("fear") || lowerText.contains("scared")) {
            return new EmotionResult("fear", 0.8, calculateIntensity(0.8));
        }
        return new EmotionResult("neutral", 0.5, calculateIntensity(0.5));
    }
}
