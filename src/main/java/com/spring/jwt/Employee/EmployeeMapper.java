package com.spring.jwt.Employee;


import com.spring.jwt.entity.Employee;
import com.spring.jwt.entity.User;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeCreateUpdateDTO dto, User user) {

        Employee emp = new Employee();
        emp.setUser(user);
        emp.setEmployeeCode(dto.getEmployeeCode());
        emp.setCompanyName(dto.getCompanyName());
        emp.setAddress(dto.getAddress());
        emp.setPermanentAddress(dto.getPermanentAddress());
        emp.setCity(dto.getCity());
        emp.setDistrict(dto.getDistrict());
        emp.setState(dto.getState());
        emp.setAccountNumber(dto.getAccountNumber());
        emp.setIfscCode(dto.getIfscCode());
        emp.setPfNumber(dto.getPfNumber());
        emp.setInsuranceNumber(dto.getInsuranceNumber());
        emp.setPanNumber(dto.getPanNumber());
        emp.setVehicleNumber(dto.getVehicleNumber());
        emp.setDescription(dto.getDescription());
        return emp;
    }

    public EmployeeResponseDTO toDto(Employee emp) {

        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setEmployeeId(emp.getEmployeeId());
        dto.setUserId(emp.getUser().getId());
        dto.setUserEmail(emp.getUser().getEmail());
        dto.setUserMobile(emp.getUser().getMobileNumber());
        dto.setEmployeeCode(emp.getEmployeeCode());
        dto.setCompanyName(emp.getCompanyName());
        dto.setAddress(emp.getAddress());
        dto.setPermanentAddress(emp.getPermanentAddress());
        dto.setCity(emp.getCity());
        dto.setDistrict(emp.getDistrict());
        dto.setState(emp.getState());
        dto.setAccountNumber(emp.getAccountNumber());
        dto.setIfscCode(emp.getIfscCode());
        dto.setPfNumber(emp.getPfNumber());
        dto.setInsuranceNumber(emp.getInsuranceNumber());
        dto.setPanNumber(emp.getPanNumber());
        dto.setVehicleNumber(emp.getVehicleNumber());
        dto.setDescription(emp.getDescription());

        return dto;
    }
}
