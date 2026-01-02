package com.spring.jwt.mapper;

import com.spring.jwt.dto.FarmerFormRequestDTO;
import com.spring.jwt.entity.FarmerForm;
import org.springframework.stereotype.Component;

@Component
public class FarmerFormMapper {

    public FarmerForm toEntity(FarmerFormRequestDTO dto) {
        FarmerForm form = new FarmerForm();

        form.setFormNumber(dto.getFormNumber());
        form.setFarmerName(dto.getFarmerName());
        form.setMobile(dto.getMobile());
        form.setAddress(dto.getAddress());
        form.setVillage(dto.getVillage());
        form.setTaluka(dto.getTaluka());
        form.setDistrict(dto.getDistrict());
        form.setProblem(dto.getProblem());

        return form;
    }
    public FarmerFormRequestDTO toDTO(FarmerForm form) {
        return new FarmerFormRequestDTO(
                form.getFormNumber(),
                form.getFarmerName(),
                form.getAddress(),
                form.getMobile(),
                form.getTaluka(),
                form.getDistrict(),
                form.getVillage(),
                form.getProblem()
        );

    }


}