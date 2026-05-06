package com.mentalhealth.service;

import com.mentalhealth.model.RiskStatus;
import com.mentalhealth.model.UserEmotionEntry;
import com.mentalhealth.repository.EmotionEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RiskDetectionService {

    private final EmotionEntryRepository repository;
    private final String HELPLINE_NUMBER = "+91-9820466726 (AASRA)";

    public RiskDetectionService(EmotionEntryRepository repository) {
        this.repository = repository;
    }

    public RiskStatus checkRisk() {
        List<UserEmotionEntry> lastEntries = repository.findTop5ByOrderByIdDesc();
        
        int riskCount = 0;
        for (UserEmotionEntry entry : lastEntries) {
            String emotion = entry.getEmotion();
            String intensity = entry.getIntensity();
            
            if (intensity.equals("High") && 
               (emotion.equals("sad") || emotion.equals("fear") || emotion.equals("anger"))) {
                riskCount++;
            }
        }

        RiskStatus status = new RiskStatus();
        if (riskCount >= 3) {
            status.setAtRisk(true);
            status.setAlertMessage("We noticed you may be going through a difficult time. Consider reaching out for help.");
            status.setHelpline(HELPLINE_NUMBER);
        } else {
            status.setAtRisk(false);
            status.setAlertMessage("");
            status.setHelpline("");
        }
        
        return status;
    }
}
