package com.spring.jwt.controller;

import com.spring.jwt.dto.BaseResponseDto;
import com.spring.jwt.dto.FarmerFormRequestDTO;
import com.spring.jwt.service.FarmerFormService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/farmer-form")
@RequiredArgsConstructor
public class FarmerFormController {

    private final FarmerFormService farmerFormService;


    @PostMapping("/submit")
    public ResponseEntity<BaseResponseDto> submitFarmerForm(
            @Valid @RequestBody FarmerFormRequestDTO dto
    ) {
        return ResponseEntity.ok(farmerFormService.submitFarmerForm(dto));
    }

    @GetMapping("/{farmerFormId}")
    public ResponseEntity<BaseResponseDto> getFarmerForm(
            @PathVariable Long farmerFormId
    ) {
        return ResponseEntity.ok(farmerFormService.getFarmerForm(farmerFormId));
    }

    @GetMapping
    public ResponseEntity<Page<FarmerFormRequestDTO>> getFarmerForms(
            @RequestParam(required = false)String farmerName,
            @RequestParam(required = false)String village,
            @RequestParam(required = false)String taluka,
            @RequestParam(required = false)String district,
            @ParameterObject Pageable pageable
    ){
        return ResponseEntity.ok(farmerFormService.getFarmerForms(
                farmerName,
                village,
                taluka,
                district,
                pageable
        ));
    }


}