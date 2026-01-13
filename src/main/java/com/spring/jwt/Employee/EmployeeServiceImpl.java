package com.spring.jwt.Employee;

import com.spring.jwt.entity.Employee;
import com.spring.jwt.entity.User;
import com.spring.jwt.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeResponseDTO getEmployeeById(Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found with ID: " + employeeId));

        return mapToDto(employee);
    }

    @Override
    public EmployeeResponseDTO getEmployeeByUserId(Long userId) {

        Employee employee = employeeRepository.findByUser_UserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found for User ID: " + userId));

        return mapToDto(employee);
    }

    @Override
    public List<EmployeeResponseDTO> getAllEmployees() {

        List<Employee> employees = employeeRepository.findAll();

        if (employees.isEmpty()) {
            throw new ResourceNotFoundException("No employees found");
        }

        return employees.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }



    private EmployeeResponseDTO mapToDto(Employee employee) {

        User user = employee.getUser();

        EmployeeResponseDTO dto = new EmployeeResponseDTO();

        dto.setEmployeeId(employee.getEmployeeId());
        dto.setEmployeeCode(employee.getEmployeeCode());
        dto.setCompanyName(employee.getCompanyName());
        dto.setAddress(employee.getAddress());
        dto.setPermanentAddress(employee.getPermanentAddress());
        dto.setCity(employee.getCity());
        dto.setDistrict(employee.getDistrict());
        dto.setState(employee.getState());
        dto.setAccountNumber(employee.getAccountNumber());
        dto.setIfscCode(employee.getIfscCode());
        dto.setPfNumber(employee.getPfNumber());
        dto.setInsuranceNumber(employee.getInsuranceNumber());
        dto.setPanNumber(employee.getPanNumber());
        dto.setVehicleNumber(employee.getVehicleNumber());
        dto.setDescription(employee.getDescription());

        dto.setUserId(user.getUserId());
        dto.setUserEmail(user.getEmail());
        dto.setUserMobile(user.getMobileNumber());

        return dto;
    }
}
