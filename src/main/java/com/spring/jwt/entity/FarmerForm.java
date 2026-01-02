package com.spring.jwt.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "farmer_form",
        indexes = {
                @Index(name = "idx_farmer_form_number", columnList = "formNumber"),
                @Index(name = "idx_farmer_form_mobile", columnList = "mobile"),
                @Index(name = "idx_farmer_form_name", columnList = "farmerName"),
                @Index(name = "idx_farmer_form_district", columnList = "district")
        }
)
public class FarmerForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long farmerFormId;

    @Column(unique = true)
    private String formNumber;

    private String farmerName;

    private String mobile;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String village;

    private String taluka;

    private String district;

    @Column(columnDefinition = "TEXT")
    private String problem;

    private LocalDateTime createdAt = LocalDateTime.now();
}