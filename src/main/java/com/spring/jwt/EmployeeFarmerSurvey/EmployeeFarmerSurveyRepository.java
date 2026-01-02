package com.spring.jwt.EmployeeFarmerSurvey;

import com.spring.jwt.entity.EmployeeFarmerSurvey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeFarmerSurveyRepository extends JpaRepository<EmployeeFarmerSurvey, Long> {

    Optional<EmployeeFarmerSurvey> findByFormNumber(String formNumber);

    boolean existsByFormNumber(String formNumber);

    Page<EmployeeFarmerSurvey> findByUserId(Long userId, Pageable pageable);


    Optional<EmployeeFarmerSurvey> findTopByFormNumberStartingWithOrderByFormNumberDesc(String prefix);

    boolean existsByFarmerMobile(String farmerMobile);
}
