package com.mentalhealth.service;

import com.mentalhealth.model.AnalyzeRequest;
import com.mentalhealth.model.AnalyzeResponse;
import com.mentalhealth.model.EmotionResult;
import com.mentalhealth.model.RiskStatus;
import com.mentalhealth.model.UserEmotionEntry;
import com.mentalhealth.repository.EmotionEntryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrchestrationService {

    private final EmotionDetectionService emotionDetectionService;
    private final SupportResponseService supportResponseService;
    private final MusicSuggestionService musicSuggestionService;
    private final QuoteService quoteService;
    private final TranslationService translationService;
    private final RiskDetectionService riskDetectionService;
    private final EmotionEntryRepository repository;

    public OrchestrationService(EmotionDetectionService emotionDetectionService,
                                SupportResponseService supportResponseService,
                                MusicSuggestionService musicSuggestionService,
                                QuoteService quoteService,
                                TranslationService translationService,
                                RiskDetectionService riskDetectionService,
                                EmotionEntryRepository repository) {
        this.emotionDetectionService = emotionDetectionService;
        this.supportResponseService = supportResponseService;
        this.musicSuggestionService = musicSuggestionService;
        this.quoteService = quoteService;
        this.translationService = translationService;
        this.riskDetectionService = riskDetectionService;
        this.repository = repository;
    }

    public AnalyzeResponse processUserText(AnalyzeRequest request) {
        String text = request.getText();

        // 1. Detect Emotion
        EmotionResult emotionResult = emotionDetectionService.detectEmotion(text);
        String emotion = emotionResult.getEmotion();
        String intensity = emotionResult.getIntensity();

        // 2. Save to Database
        UserEmotionEntry entry = new UserEmotionEntry();
        entry.setDate(LocalDate.now());
        entry.setTime(LocalTime.now());
        entry.setEmotion(emotion);
        entry.setIntensity(intensity);
        entry.setUserInput(text.length() > 2000 ? text.substring(0, 1999) : text);
        repository.save(entry);

        // 3. Generate Support Response
        String responseText = supportResponseService.generateResponse(emotion, intensity);

        // 4. Translate Response
        Map<String, String> translations = new HashMap<>();
        translations.put("hi", translationService.translate(responseText, "hi")); // Hindi
        translations.put("mr", translationService.translate(responseText, "mr")); // Marathi

        // 5. Fetch Music Suggestion
        String musicUrl = musicSuggestionService.suggestMusic(emotion);

        // 6. Fetch Quote
        String quote = quoteService.fetchQuote();

        // 7. Check Risk
        RiskStatus riskStatus = riskDetectionService.checkRisk();

        // 8. Build final response
        AnalyzeResponse finalResponse = new AnalyzeResponse();
        finalResponse.setEmotion(emotion);
        finalResponse.setIntensity(intensity);
        finalResponse.setOriginalResponse(responseText);
        finalResponse.setTranslations(translations);
        finalResponse.setSuggestedMusic(musicUrl);
        finalResponse.setMotivationalQuote(quote);
        finalResponse.setRiskStatus(riskStatus);

        return finalResponse;
    }
}
