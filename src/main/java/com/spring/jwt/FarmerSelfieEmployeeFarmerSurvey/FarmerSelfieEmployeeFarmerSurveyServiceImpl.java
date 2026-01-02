package com.spring.jwt.FarmerSelfieEmployeeFarmerSurvey;

import com.spring.jwt.EmployeeFarmerSurvey.EmployeeFarmerSurveyRepository;
import com.spring.jwt.Enums.FormStatus;
import com.spring.jwt.entity.EmployeeFarmerSurvey;
import com.spring.jwt.entity.FarmerSelfieEmployeeFarmerSurvey;
import com.spring.jwt.exception.DocumentAlreadyExistsException;
import com.spring.jwt.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Service implementation responsible for managing Farmer Selfie operations
 * associated with Employee Farmer Surveys.
 *
 * Responsibilities:
 *  - Upload farmer selfie for a survey
 *  - Fetch selfie by selfie ID or survey ID
 *  - Update (patch) selfie image only
 *
 * Design Notes:
 *  - Base64 image is stored directly in DB
 *  - One selfie per survey is enforced
 *  - Survey status is activated once selfie is uploaded
 *  - All write operations are transactional
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FarmerSelfieEmployeeFarmerSurveyServiceImpl
        implements FarmerSelfieEmployeeFarmerSurveyService {

    /**
     * Repository for Farmer Selfie persistence operations
     */
    private final FarmerSelfieEmployeeFarmerSurveyRepository selfieRepository;

    /**
     * Repository for Employee Farmer Survey persistence operations
     */
    private final EmployeeFarmerSurveyRepository surveyRepository;


    /**
     * Upload a farmer selfie image for a given survey.
     *
     * Business Rules:
     *  - Survey must exist
     *  - Only one selfie allowed per survey
     *  - Only valid image files are accepted
     *  - Survey status is updated to ACTIVE after successful upload
     *
     * @param surveyId ID of the employee farmer survey
     * @param file     Image file to upload
     * @return         FarmerSelfieResponseDTO
     */
    @Override
    @Transactional
    public FarmerSelfieResponseUploadDTO uploadSelfie(Long surveyId, MultipartFile file) {

        validateSurveyId(surveyId);
        validateImage(file);

        EmployeeFarmerSurvey survey = surveyRepository.findById(surveyId).orElseThrow(() -> new ResourceNotFoundException("Survey not found with ID: " + surveyId));

        if (selfieRepository.existsBySurvey_SurveyId(surveyId)) {
            throw new DocumentAlreadyExistsException(
                    "Selfie already uploaded for survey ID: " + surveyId);
        }

        try {
            long startTime = System.currentTimeMillis();

            FarmerSelfieEmployeeFarmerSurvey selfie = new FarmerSelfieEmployeeFarmerSurvey();
            selfie.setSurvey(survey);
            selfie.setImageUrl(encodeBase64(file));
            selfie.setTakenAt(LocalDateTime.now());

            FarmerSelfieEmployeeFarmerSurvey saved = selfieRepository.save(selfie);
            survey.setFormStatus(FormStatus.ACTIVE);
            surveyRepository.save(survey);
            long totalTime = System.currentTimeMillis() - startTime;
            log.info("Farmer selfie uploaded successfully for surveyId={} in {} ms", surveyId, totalTime);
            return mapToResponseUpload(saved);
        } catch (Exception e) {
            log.error("Failed to upload farmer selfie for survey {}", surveyId, e);
            throw new RuntimeException("Failed to upload farmer selfie", e);
        }
    }


    /**
     * Fetch farmer selfie details using selfie ID.
     *
     * @param selfieId Unique ID of the farmer selfie
     * @return         FarmerSelfieResponseDTO
     */
    @Override
    public FarmerSelfieResponseDTO getSelfieById(Long selfieId) {

        FarmerSelfieEmployeeFarmerSurvey selfie = selfieRepository.findById(selfieId).orElseThrow(() -> new ResourceNotFoundException("Selfie not found with ID: " + selfieId));
        return mapToResponse(selfie);
    }


    /**
     * Fetch farmer selfie details using survey ID.
     *
     * Typical Use Case:
     *  - Display selfie while viewing survey details
     *
     * @param surveyId ID of the employee farmer survey
     * @return         FarmerSelfieResponseDTO
     */
    @Override
    public FarmerSelfieResponseDTO getSelfieBySurveyId(Long surveyId) {

        FarmerSelfieEmployeeFarmerSurvey selfie = selfieRepository.findBySurvey_SurveyId(surveyId).orElseThrow(() -> new ResourceNotFoundException("Selfie not found for survey ID: " + surveyId));
        return mapToResponse(selfie);
    }


    /**
     * Update (replace) the farmer selfie image.
     *
     * Notes:
     *  - Only the image is updated
     *  - Existing selfie metadata remains unchanged
     *
     * @param selfieId ID of the selfie to update
     * @param file     New image file
     * @return         Updated FarmerSelfieResponseDTO
     */
    @Override
    @Transactional
    public FarmerSelfieResponseDTO updateSelfieImage(Long selfieId, MultipartFile file) {

        validateImage(file);

        FarmerSelfieEmployeeFarmerSurvey selfie = selfieRepository.findById(selfieId).orElseThrow(() -> new ResourceNotFoundException("Selfie not found with ID: " + selfieId));
        try {
            selfie.setImageUrl(encodeBase64(file));
            selfie.setTakenAt(LocalDateTime.now());

            return mapToResponse(selfieRepository.save(selfie));

        } catch (Exception e) {
            log.error("Failed to update selfie {}", selfieId, e);
            throw new RuntimeException("Failed to update selfie image", e);
        }
    }

    /**
     * Validate survey ID input.
     */
    private void validateSurveyId(Long surveyId) {
        if (surveyId == null) {
            throw new IllegalArgumentException("Survey ID must not be null");
        }
    }

    /**
     * Validate uploaded image file.
     *
     * Rules:
     *  - File must not be null or empty
     *  - Only image MIME types are allowed
     *  - Maximum file size: 5MB
     */
    private void validateImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }
        if (file.getContentType() == null ||
                !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("Image size must be less than 5MB");
        }
    }

    /**
     * Convert MultipartFile to Base64 encoded string.
     */
    private String encodeBase64(MultipartFile file) {
        try {
            return Base64.getEncoder().encodeToString(file.getBytes());
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to process image file", e);
        }
    }

    /**
     * Map entity to response DTO.
     */
    private FarmerSelfieResponseDTO mapToResponse(
            FarmerSelfieEmployeeFarmerSurvey entity) {

        FarmerSelfieResponseDTO dto = new FarmerSelfieResponseDTO();
        dto.setSelfieId(entity.getFarmerSelfieEmployeeFarmerSurveyId());
        dto.setSurveyId(entity.getSurvey().getSurveyId());
        dto.setImageUrl(entity.getImageUrl());
        dto.setTakenAt(entity.getTakenAt());
        return dto;
    }
    private FarmerSelfieResponseUploadDTO mapToResponseUpload(
            FarmerSelfieEmployeeFarmerSurvey entity) {

        FarmerSelfieResponseUploadDTO dto = new FarmerSelfieResponseUploadDTO();
        dto.setSelfieId(entity.getFarmerSelfieEmployeeFarmerSurveyId());
        dto.setSurveyId(entity.getSurvey().getSurveyId());
        dto.setTakenAt(entity.getTakenAt());
        return dto;
    }
}
