package com.mentalhealth.model;

import lombok.Data;

@Data
public class RiskStatus {
    private boolean isAtRisk;
    private String alertMessage;
    private String helpline;
}
