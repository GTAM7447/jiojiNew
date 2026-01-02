package com.spring.jwt.FarmerSelfieEmployeeFarmerSurvey;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FarmerSelfieResponseDTO {

    private Long selfieId;
    private Long surveyId;
    private String imageUrl;
    private LocalDateTime takenAt;
}
