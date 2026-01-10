package com.spring.jwt.Employee;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;


    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeResponseDTO>
    getEmployeeById(@PathVariable Long employeeId) {

        return ResponseEntity.ok(
                employeeService.getEmployeeById(employeeId));
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<EmployeeResponseDTO>
    getEmployeeByUserId(@PathVariable Long userId) {

        return ResponseEntity.ok(
                employeeService.getEmployeeByUserId(userId));
    }

    @GetMapping("all")
    public ResponseEntity<List<EmployeeResponseDTO>>
    getAllEmployees() {

        return ResponseEntity.ok(
                employeeService.getAllEmployees());
    }
}
