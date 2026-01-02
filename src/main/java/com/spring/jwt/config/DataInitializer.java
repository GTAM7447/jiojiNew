package com.spring.jwt.config;

import com.spring.jwt.entity.Product;
import com.spring.jwt.entity.Role;
import com.spring.jwt.entity.User;
import com.spring.jwt.repository.ProductRepository;
import com.spring.jwt.repository.RoleRepository;
import com.spring.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProductRepository productRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.default-email}")
    private String defaultAdminEmail;

    @Value("${app.admin.default-password}")
    private String defaultAdminPassword;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            initRoles();
//            initAdmin();
            initProducts();
        };
    }

    private void initRoles() {
        createRoleIfNotFound("ADMIN");
        createRoleIfNotFound("SURVEYOR");
        createRoleIfNotFound("LAB_TECHNICIAN");
        createRoleIfNotFound("USER");
    }

    private void createRoleIfNotFound(String roleName) {
        if (roleRepository.findByName(roleName) == null) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
            log.info("Role created: {}", roleName);
        }
    }




    private void initProducts() {
        if (productRepository.count() == 0) {
            List<Product> products = new ArrayList<>();

            // 17 Seed Products
            String[] seedNames = {
                    "Wheat DBW 187", "Wheat DBW 222", "Wheat HD 2967", "Wheat HD 3086", "Wheat PBW 343",
                    "Rice Pusa Basmati 1121", "Rice Pusa Basmati 1509", "Rice Pusa Basmati 1718", "Rice MTU 1010",
                    "Corn Hybrid 1", "Corn Hybrid 2", "Corn Single Cross",
                    "Soybean JS 335", "Soybean JS 9560",
                    "Cotton Bt II", "Cotton Bt III", "Mustard Pusa Bio 902"
            };

            for (String name : seedNames) {
                Product product = new Product();
                product.setProductName(name);
                product.setProductType(Product.ProductType.SEED);
                product.setDescription("High quality " + name + " for better yield.");
                product.setPrice(100.0 + (Math.random() * 50));
                product.setActive(true);
                product.setCreatedAt(LocalDateTime.now());
                products.add(product);
            }

            productRepository.saveAll(products);
            log.info("Initialized {} products", products.size());
        }
    }
}
