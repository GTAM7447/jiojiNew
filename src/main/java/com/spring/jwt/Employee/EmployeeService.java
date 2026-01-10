package com.spring.jwt.Employee;


import java.util.List;

public interface EmployeeService {

    EmployeeResponseDTO getEmployeeById(Long employeeId);

    EmployeeResponseDTO getEmployeeByUserId(Long userId);

    List<EmployeeResponseDTO> getAllEmployees();
}
