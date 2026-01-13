package com.spring.jwt.Employee;


import org.springframework.data.domain.Page;

import java.util.List;

public interface EmployeeService {

    EmployeeResponseDTO getEmployeeById(Long employeeId);

    EmployeeResponseDTO getEmployeeByUserId(Long userId);

    public Page<EmployeeResponseDTO> getAllEmployees(int page, int size);

    // Status-only update
    EmployeeResponseDTO updateAccountLockStatus(Long employeeId, Boolean accountLocked);

    EmployeeResponseDTO patchEmployeeByUserId(Long userId, EmployeeUpdateRequestDTO dto);

    EmployeeResponseDTO updateAccountLockStatusByUserId(Long userId, Boolean accountLocked);
}
