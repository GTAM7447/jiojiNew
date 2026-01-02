package com.spring.jwt.service;

import com.spring.jwt.dto.BaseResponseDto;
import com.spring.jwt.dto.FarmerFormRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FarmerFormService {
    BaseResponseDto submitFarmerForm(FarmerFormRequestDTO dto);
    BaseResponseDto getFarmerForm(Long farmerFormId);
    Page<FarmerFormRequestDTO> getFarmerForms(
            String farmerName,
            String village,
            String taluka,
            String district,
            Pageable pageable);
}
