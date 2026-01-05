package com.spring.jwt.FarmerLabReport;

import com.spring.jwt.EmployeeFarmerSurvey.BaseResponseDTO1;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for managing Farmer Lab Reports.
 *
 * <p>
 * This controller exposes APIs to upload, view, update, delete,
 * and download lab report PDF documents associated with
 * {@code EmployeeFarmerSurvey}.
 * </p>
 *
 * <p>
 * All successful responses are wrapped using {@link BaseResponseDTO1}
 * to maintain a consistent API response structure across the application.
 * </p>
 *
 * Base URL:
 * <pre>
 * /api/v1/lab-report
 * </pre>
 */
@RestController
@RequestMapping("/api/v1/lab_report")
@RequiredArgsConstructor
public class FarmerLabReportController {

    private final FarmerLabReportService labReportService;

    /**
     * Uploads a new lab report PDF for a given survey.
     *
     * @param surveyId the unique identifier of the survey
     * @param file the PDF file to upload
     * @return success response with uploaded lab report details
     */
    @PostMapping("/upload/{surveyId}")
    public ResponseEntity<BaseResponseDTO1<FarmerLabReportUploadDTO>> upload(
            @PathVariable Long surveyId,
            @RequestParam MultipartFile file) {

        FarmerLabReportUploadDTO response =
                labReportService.uploadLabReport(surveyId, file);

        return ResponseEntity.ok(
                new BaseResponseDTO1<>(
                        "200",
                        "Lab report uploaded successfully",
                        response
                )
        );
    }

    /**
     * Retrieves lab report details using survey ID.
     *
     * @param surveyId the unique identifier of the survey
     * @return success response with lab report details
     */
    @GetMapping("/view/{surveyId}")
    public ResponseEntity<BaseResponseDTO1<FarmerLabReportViewDTO>> view(
            @PathVariable Long surveyId) {

        FarmerLabReportViewDTO response =
                labReportService.viewLabReport(surveyId);

        return ResponseEntity.ok(
                new BaseResponseDTO1<>(
                        "200",
                        "Lab report fetched successfully",
                        response
                )
        );
    }

    /**
     * Retrieves lab report details using report ID.
     *
     * @param reportId the unique identifier of the lab report
     * @return success response with lab report details
     */
    @GetMapping("/get/{reportId}")
    public ResponseEntity<BaseResponseDTO1<FarmerLabReportViewDTO>> getById(
            @PathVariable Long reportId) {

        FarmerLabReportViewDTO response =
                labReportService.getLabReportById(reportId);

        return ResponseEntity.ok(
                new BaseResponseDTO1<>(
                        "200",
                        "Lab report fetched successfully",
                        response
                )
        );
    }


    /**
     * Updates an existing lab report PDF using survey ID.
     *
     * @param surveyId the unique identifier of the survey
     * @param file the new PDF file to replace the existing report
     * @return success response with updated lab report details
     */
    @PatchMapping("/update/{surveyId}")
    public ResponseEntity<BaseResponseDTO1<FarmerLabReportUploadDTO>> update(
            @PathVariable Long surveyId,
            @RequestParam MultipartFile file) {

        FarmerLabReportUploadDTO response =
                labReportService.updateLabReportBySurveyId(surveyId, file);

        return ResponseEntity.ok(
                new BaseResponseDTO1<>(
                        "200",
                        "Lab report updated successfully",
                        response
                )
        );
    }



    /**
     * Deletes the lab report associated with the given survey ID.
     *
     * @param surveyId the unique identifier of the survey
     * @return success response confirming deletion
     */
    @DeleteMapping("/delete/{surveyId}")
    public ResponseEntity<BaseResponseDTO1<Void>> delete(
            @PathVariable Long surveyId) {

        labReportService.deleteLabReportBySurveyId(surveyId);

        return ResponseEntity.ok(
                new BaseResponseDTO1<>(
                        "200",
                        "Lab report deleted successfully",
                        null
                )
        );
    }



    /**
     * Downloads the lab report PDF associated with the given survey ID.
     *
     * <p>
     * Returns the PDF as a byte stream with appropriate headers
     * for file download.
     * </p>
     *
     * @param surveyId the unique identifier of the survey
     * @return PDF file as byte array
     */
    @GetMapping("/download/{surveyId}")
    public ResponseEntity<byte[]> download(@PathVariable Long surveyId) {

        byte[] pdf = labReportService.downloadLabReport(surveyId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=lab-report-" + surveyId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
