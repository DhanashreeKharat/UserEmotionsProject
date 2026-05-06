package com.mentalhealth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmotionResult {
    private String emotion;
    private double score;
    private String intensity;
}
