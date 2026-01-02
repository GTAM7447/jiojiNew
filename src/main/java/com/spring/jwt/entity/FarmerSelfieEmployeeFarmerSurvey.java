package com.spring.jwt.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "farmer_selfie_employee_farmer_survey",
        indexes = {
                @Index(name = "idx_farmer_selfie_survey_id", columnList = "survey_id")
        }
)
public class FarmerSelfieEmployeeFarmerSurvey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long FarmerSelfieEmployeeFarmerSurveyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private EmployeeFarmerSurvey survey;

    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String imageUrl;

    private LocalDateTime takenAt = LocalDateTime.now();
}