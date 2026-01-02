package com.spring.jwt.FarmerSelfieEmployeeFarmerSurvey;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class FarmerSelfieResponseUploadDTO {
    private Long selfieId;
    private Long surveyId;
    private LocalDateTime takenAt;
}
