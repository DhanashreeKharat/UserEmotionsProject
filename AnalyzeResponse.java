package com.mentalhealth.model;

import lombok.Data;
import java.util.Map;

@Data
public class AnalyzeResponse {
    private String emotion;
    private String intensity;
    private String originalResponse; // English by default
    private Map<String, String> translations; // {"hi": "...", "mr": "..."}
    private String suggestedMusic; // Spotify track URL or name
    private String motivationalQuote;
    private RiskStatus riskStatus;
}
