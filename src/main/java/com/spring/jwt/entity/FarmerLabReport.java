package com.spring.jwt.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "farmer_lab_report",
        indexes = {
                @Index(name = "idx_lab_report_survey_id", columnList = "survey_id")
        }
)
public class FarmerLabReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private EmployeeFarmerSurvey survey;

    private String pdfUrl;

    private LocalDateTime uploadedAt = LocalDateTime.now();
}