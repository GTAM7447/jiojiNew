package com.spring.jwt.repository;

import com.spring.jwt.entity.FarmerForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FarmerFormRepository extends JpaRepository<FarmerForm,Long> {

    boolean existsByFormNumber(String formNumber);
    Optional<FarmerForm> findById(Long farmerFormId);
}
