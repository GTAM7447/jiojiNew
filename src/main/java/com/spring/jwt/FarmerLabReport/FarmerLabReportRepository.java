package com.spring.jwt.FarmerLabReport;

import com.spring.jwt.entity.FarmerLabReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FarmerLabReportRepository extends JpaRepository<FarmerLabReport, Long> {

    boolean existsBySurvey_SurveyId(Long surveyId);

    Optional<FarmerLabReport> findBySurvey_SurveyId(Long surveyId);
}
