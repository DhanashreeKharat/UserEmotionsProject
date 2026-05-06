package com.mentalhealth.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnalyzeRequest {
    @NotBlank(message = "Text input cannot be empty")
    private String text;
}
