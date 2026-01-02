package com.spring.jwt.service.impl;

import com.spring.jwt.dto.AdminRegistrationRequest;
import com.spring.jwt.dto.SecureUserRegistrationRequest;
import com.spring.jwt.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFactory {

    private final BCryptPasswordEncoder passwordEncoder;

    public User createUser(SecureUserRegistrationRequest request, HttpServletRequest httpRequest)
    {
        return buildUser(
            request.getEmail(),
            request.getPassword(),
            request.getMobileNumber(),
            request.getFirstName(),
            request.getLastName(),
            false,
            request.getAcceptTerms(),
            request.getAcceptPrivacyPolicy()
        );
    }

    public User createAdmin(AdminRegistrationRequest request, HttpServletRequest httpRequest)
    {
        return buildUser(
            request.getEmail(),
            request.getPassword(),
            request.getMobileNumber(),
            request.getFirstName(),
            request.getLastName(),
            true,
            true,
            true
        );
    }

    private User buildUser(String email, String password, Long mobileNumber,
                          String firstName, String lastName, boolean emailVerified,
                          boolean termsAccepted, boolean privacyPolicyAccepted)
    {
        User user = new User();
        user.setEmail(email.toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(password));
        user.setMobileNumber(mobileNumber);
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setEmailVerified(emailVerified);
        user.setTermsAccepted(termsAccepted);
        user.setPrivacyPolicyAccepted(privacyPolicyAccepted);
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        return user;
    }
}