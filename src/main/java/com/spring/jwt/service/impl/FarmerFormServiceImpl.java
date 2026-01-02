package com.spring.jwt.service.impl;

import com.spring.jwt.dto.BaseResponseDto;
import com.spring.jwt.dto.FarmerFormRequestDTO;
import com.spring.jwt.entity.FarmerForm;
import com.spring.jwt.exception.UserAlreadyExistException;
import com.spring.jwt.exception.UserNotFoundExceptions;
import com.spring.jwt.mapper.FarmerFormMapper;
import com.spring.jwt.repository.FarmerFormRepository;
import com.spring.jwt.service.FarmerFormService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FarmerFormServiceImpl implements FarmerFormService {
    private final FarmerFormRepository farmerFormRepository;
    private final FarmerFormMapper farmerFormMapper;

    @Override
    @Transactional
    public BaseResponseDto submitFarmerForm(FarmerFormRequestDTO dto){

        if (farmerFormRepository.existsByFormNumber(dto.getFormNumber())){
            throw new UserAlreadyExistException("FarmerForm already exist");
        }
        FarmerForm form = farmerFormMapper.toEntity(dto);
        farmerFormRepository.save(form);
        return new BaseResponseDto(
                "200",
                "Farmer Form Submitted Successfully",
                form.getFarmerFormId()
        );
    }
    @Override
    public BaseResponseDto getFarmerForm(Long farmerFormId){
        FarmerForm form=farmerFormRepository.findById(farmerFormId)
                .orElseThrow(()->new UserNotFoundExceptions("FarmerForm Not Found with id" + farmerFormId));
        return new BaseResponseDto(

                "200",
                "Farmer Form Fetch Successfully",
                farmerFormMapper.toDTO(form)
        );
    }
    @Override
    public Page<FarmerFormRequestDTO> getFarmerForms(
            String farmerName,
            String village,
            String taluka,
            String district,
            Pageable pageable){

        Page<FarmerForm> forms =farmerFormRepository.findAll(pageable);
        return new PageImpl<>(
                forms.stream()
                        .filter(f-> farmerName==null||f.getFarmerName().equalsIgnoreCase(farmerName))
                        .filter(f-> village==null||f.getVillage().equalsIgnoreCase(village))
                        .filter(f-> taluka==null||f.getTaluka().equalsIgnoreCase(taluka))
                        .filter(f-> district==null||f.getDistrict().equalsIgnoreCase(district))
                        .map(farmerFormMapper::toDTO)
                        .toList(),
                pageable,
                forms.getTotalElements()
        );

    }
}
