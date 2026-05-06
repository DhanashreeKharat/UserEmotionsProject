package com.mentalhealth.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "user_emotion_entries")
public class UserEmotionEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    @Column(nullable = false)
    private String emotion;

    @Column(nullable = false)
    private String intensity; // High, Medium, Low
    
    @Column(length = 2000)
    private String userInput;
}
