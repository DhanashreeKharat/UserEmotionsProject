package com.mentalhealth.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Service
public class MusicSuggestionService {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;
    private String accessToken;
    private long tokenExpiryTime;

    public MusicSuggestionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String suggestMusic(String emotion) {
        if (clientId == null || clientId.isEmpty() || clientSecret == null || clientSecret.isEmpty()) {
            return fallbackMusic(emotion);
        }

        try {
            ensureAccessToken();

            String query = "calm healing";
            if (emotion.equals("happy")) query = "upbeat joyful";
            if (emotion.equals("anger")) query = "relaxing soft";
            if (emotion.equals("fear")) query = "soothing ambient";

            String searchUrl = "https://api.spotify.com/v1/search?q=" + query + "&type=playlist&limit=1";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(searchUrl, HttpMethod.GET, request, String.class);
            JSONObject jsonResponse = new JSONObject(response.getBody());
            
            JSONArray items = jsonResponse.getJSONObject("playlists").getJSONArray("items");
            if (items.length() > 0) {
                return items.getJSONObject(0).getJSONObject("external_urls").getString("spotify");
            }
        } catch (Exception e) {
            System.err.println("Error calling Spotify API: " + e.getMessage());
        }
        
        return fallbackMusic(emotion);
    }

    private void ensureAccessToken() {
        if (accessToken != null && System.currentTimeMillis() < tokenExpiryTime) {
            return;
        }

        String auth = clientId + ":" + clientSecret;
        String base64Auth = Base64.getEncoder().encodeToString(auth.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + base64Auth);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("https://accounts.spotify.com/api/token", request, String.class);

        JSONObject jsonResponse = new JSONObject(response.getBody());
        accessToken = jsonResponse.getString("access_token");
        tokenExpiryTime = System.currentTimeMillis() + (jsonResponse.getInt("expires_in") * 1000L) - 60000;
    }

    private String fallbackMusic(String emotion) {
        if (emotion.equals("sad") || emotion.equals("fear")) return "https://open.spotify.com/playlist/37i9dQZF1DWZqd5JICZI0u"; // Peaceful Piano
        if (emotion.equals("happy")) return "https://open.spotify.com/playlist/37i9dQZF1DX3rxVfibe1L0"; // Mood Booster
        if (emotion.equals("anger")) return "https://open.spotify.com/playlist/37i9dQZF1DX4sWSpwq3LiO"; // Peaceful Meditation
        return "https://open.spotify.com/playlist/37i9dQZF1DX889U0CL85jj"; // Chill Vibes
    }
}
