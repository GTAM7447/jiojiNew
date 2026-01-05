package com.spring.jwt.FarmerLabReport;

import com.spring.jwt.EmployeeFarmerSurvey.EmployeeFarmerSurveyRepository;
import com.spring.jwt.entity.EmployeeFarmerSurvey;
import com.spring.jwt.entity.FarmerLabReport;
import com.spring.jwt.exception.DocumentAlreadyExistsException;
import com.spring.jwt.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class FarmerLabReportServiceImpl implements FarmerLabReportService {

    private final FarmerLabReportRepository labReportRepository;
    private final EmployeeFarmerSurveyRepository surveyRepository;



    @Override
    @Transactional
    public FarmerLabReportUploadDTO uploadLabReport(Long surveyId, MultipartFile file) {

        validateSurveyId(surveyId);
        validatePdf(file);

        EmployeeFarmerSurvey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Survey not found with ID: " + surveyId));

        if (labReportRepository.existsBySurvey_SurveyId(surveyId)) {
            throw new DocumentAlreadyExistsException(
                    "Lab report already uploaded for survey ID: " + surveyId);
        }

        try {
            long startTime = System.currentTimeMillis();

            FarmerLabReport report = new FarmerLabReport();
            report.setSurvey(survey);
            report.setPdfUrl(encodeBase64(file));
            report.setUploadedAt(LocalDateTime.now());

            FarmerLabReport saved = labReportRepository.save(report);

            log.info("Lab report uploaded successfully for surveyId={} in {} ms",
                    surveyId, System.currentTimeMillis() - startTime);

            return mapToUploadDTO(saved);

        } catch (Exception e) {
            log.error("Failed to upload lab report for survey {}", surveyId, e);
            throw new RuntimeException("Failed to upload lab report", e);
        }
    }



    @Override
    public FarmerLabReportViewDTO viewLabReport(Long surveyId) {

        FarmerLabReport report = labReportRepository.findBySurvey_SurveyId(surveyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Lab report not found for survey ID: " + surveyId));

        return mapToViewDTO(report);
    }



    @Override
    public FarmerLabReportViewDTO getLabReportById(Long reportId) {

        FarmerLabReport report = labReportRepository.findById(reportId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Lab report not found with ID: " + reportId));

        return mapToViewDTO(report);
    }



    @Override
    @Transactional
    public FarmerLabReportUploadDTO updateLabReportBySurveyId(Long surveyId, MultipartFile file) {

        validateSurveyId(surveyId);
        validatePdf(file);

        FarmerLabReport report = labReportRepository.findBySurvey_SurveyId(surveyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Lab report not found for survey ID: " + surveyId));

        try {
            report.setPdfUrl(encodeBase64(file));
            report.setUploadedAt(LocalDateTime.now());

            FarmerLabReport updated = labReportRepository.save(report);

            log.info("Lab report updated successfully for surveyId={}", surveyId);
            return mapToUploadDTO(updated);

        } catch (Exception e) {
            log.error("Failed to update lab report for survey {}", surveyId, e);
            throw new RuntimeException("Failed to update lab report", e);
        }
    }



    @Override
    @Transactional
    public void deleteLabReportBySurveyId(Long surveyId) {

        FarmerLabReport report = labReportRepository.findBySurvey_SurveyId(surveyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Lab report not found for survey ID: " + surveyId));

        labReportRepository.delete(report);
        log.info("Lab report deleted successfully for surveyId={}", surveyId);
    }



    @Override
    public byte[] downloadLabReport(Long surveyId) {

        FarmerLabReport report = labReportRepository.findBySurvey_SurveyId(surveyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Lab report not found for survey ID: " + surveyId));

        return Base64.getDecoder().decode(report.getPdfUrl());
    }


    private void validateSurveyId(Long surveyId) {
        if (surveyId == null || surveyId <= 0) {
            throw new IllegalArgumentException("Invalid survey ID");
        }
    }

    private void validatePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("PDF file is required");
        }
        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }
    }

    private String encodeBase64(MultipartFile file) {
        try {
            return Base64.getEncoder().encodeToString(file.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode PDF", e);
        }
    }

    private FarmerLabReportUploadDTO mapToUploadDTO(FarmerLabReport report) {
        FarmerLabReportUploadDTO dto = new FarmerLabReportUploadDTO();
        dto.setReportId(report.getReportId());
        dto.setSurveyId(report.getSurvey().getSurveyId());
        dto.setPdfUrl(report.getPdfUrl());
        return dto;
    }

    private FarmerLabReportViewDTO mapToViewDTO(FarmerLabReport report) {
        FarmerLabReportViewDTO dto = new FarmerLabReportViewDTO();
        dto.setReportId(report.getReportId());
        dto.setSurveyId(report.getSurvey().getSurveyId());
        dto.setPdfUrl(report.getPdfUrl());
        dto.setUploadedAt(report.getUploadedAt());
        return dto;
    }
}
