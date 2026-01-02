package com.spring.jwt.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "farmer_selfie",
        indexes = {
                @Index(name = "idx_farmer_selfie_survey_id", columnList = "survey_id")
        }
)
public class FarmerSelfie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long selfieId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private EmployeeFarmerSurvey survey;

    private String imageUrl;

    private LocalDateTime takenAt = LocalDateTime.now();
}