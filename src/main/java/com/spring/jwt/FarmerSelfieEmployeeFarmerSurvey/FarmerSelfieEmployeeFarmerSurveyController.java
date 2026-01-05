package com.spring.jwt.FarmerSelfieEmployeeFarmerSurvey;

import com.spring.jwt.EmployeeFarmerSurvey.BaseResponseDTO1;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST Controller responsible for handling Farmer Selfie operations
 * related to Employee Farmer Survey.
 *
 * This controller supports:
 *  - Uploading farmer selfie images
 *  - Fetching selfie details by selfie ID
 *  - Fetching selfie details by survey ID
 *  - Updating (patching) the selfie image only
 *
 * All responses are wrapped using BaseResponseDTO1 for consistency
 * across the application.
 */
@RestController
@RequestMapping("/api/v1/farmer_selfie_Survey")
@RequiredArgsConstructor
public class FarmerSelfieEmployeeFarmerSurveyController {

    /**
     * Service layer dependency handling all business logic
     * related to farmer selfie operations.
     */
    private final FarmerSelfieEmployeeFarmerSurveyService selfieService;

    /**
     * Upload a farmer selfie image for a given survey.
     *
     * Business Rules:
     *  - A valid surveyId must be provided
     *  - Only image files are allowed
     *  - Only one selfie is allowed per survey
     *
     * @param surveyId ID of the Employee Farmer Survey
     * @param selfie    Multipart image file to be uploaded
     * @return         Created FarmerSelfieResponseDTO
     */
    @PostMapping("/upload")
    public ResponseEntity<BaseResponseDTO1<FarmerSelfieResponseUploadDTO>> uploadSelfie(
            @RequestParam Long surveyId,
            @RequestParam("selfie") MultipartFile selfie) {

        FarmerSelfieResponseUploadDTO response =
                selfieService.uploadSelfie(surveyId, selfie);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponseDTO1<>("201", "Farmer selfie uploaded successfully", response));
    }



    /**
     * Fetch farmer selfie details using selfie ID.
     *
     * @param selfieId Unique ID of the farmer selfie
     * @return         FarmerSelfieResponseDTO containing selfie details
     */
    @GetMapping("/{selfieId}")
    public ResponseEntity<BaseResponseDTO1<FarmerSelfieResponseDTO>> getBySelfieId(
            @PathVariable Long selfieId) {

        FarmerSelfieResponseDTO response = selfieService.getSelfieById(selfieId);

        return ResponseEntity.ok(new BaseResponseDTO1<>("200", "Farmer selfie fetched successfully", response));
    }

    /**
     * Fetch farmer selfie details using survey ID.
     *
     * Use case:
     *  - Mobile app wants selfie details while viewing survey
     *
     * @param surveyId ID of the Employee Farmer Survey
     * @return         FarmerSelfieResponseDTO linked to the survey
     */
    @GetMapping("/survey/{surveyId}")
    public ResponseEntity<BaseResponseDTO1<FarmerSelfieResponseDTO>> getBySurveyId(
            @PathVariable Long surveyId) {
        FarmerSelfieResponseDTO response = selfieService.getSelfieBySurveyId(surveyId);
        return ResponseEntity.ok(new BaseResponseDTO1<>("200", "Farmer selfie fetched successfully by survey ID", response));
    }


    /**
     * Update (replace) the farmer selfie image.
     *
     * Notes:
     *  - This is a PATCH operation because only the image is updated
     *  - Existing selfie metadata remains unchanged
     *
     * @param selfieId ID of the selfie to be updated
     * @param image    New image file
     * @return         Updated FarmerSelfieResponseDTO
     */
    @PatchMapping("/{selfieId}")
    public ResponseEntity<BaseResponseDTO1<FarmerSelfieResponseDTO>> updateSelfieImage(
            @PathVariable Long selfieId,
            @RequestParam MultipartFile image) {

        FarmerSelfieResponseDTO response = selfieService.updateSelfieImage(selfieId, image);
        return ResponseEntity.ok(new BaseResponseDTO1<>("200", "Farmer selfie image updated successfully", response));
    }
}
