package com.spring.jwt.FarmerSelfieEmployeeFarmerSurvey;

import org.springframework.web.multipart.MultipartFile;

public interface  FarmerSelfieEmployeeFarmerSurveyService {
    FarmerSelfieResponseUploadDTO uploadSelfie(Long surveyId, MultipartFile file);

    FarmerSelfieResponseDTO getSelfieById(Long selfieId);

    FarmerSelfieResponseDTO getSelfieBySurveyId(Long surveyId);

    FarmerSelfieResponseDTO updateSelfieImage(Long selfieId, MultipartFile file);
}
