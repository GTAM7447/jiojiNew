package com.spring.jwt.config;

import com.spring.jwt.config.filter.*;
import com.spring.jwt.jwt.JwtConfig;
import com.spring.jwt.jwt.JwtService;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.service.security.UserDetailsServiceCustom;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.util.Arrays;
import java.util.List;
import com.spring.jwt.exception.SecurityExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@EnableScheduling
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
@Slf4j
public class AppConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private JwtService jwtService;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    private SecurityHeadersFilter securityHeadersFilter;

    @Autowired
    private XssFilter xssFilter;

    @Autowired
    private SqlInjectionFilter sqlInjectionFilter;

    @Autowired
    private RateLimitingFilter rateLimitingFilter;

    @Autowired
    private com.spring.jwt.jwt.ActiveSessionService activeSessionService;

    @Autowired
    private ObjectMapper objectMapper;


    @Value("${app.url.frontend:http://localhost:5173}")
    private String frontendUrl;

    @Value("#{'${app.cors.allowed-origins:http://localhost:5173,http://localhost:3000,http://localhost:8080,http://localhost:5173/,http://localhost:8091/,http://localhost:8085/}'.split(',')}")
    private List<String> allowedOrigins;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsServiceCustom userDetailsService() {
        return new UserDetailsServiceCustom(userRepository);
    }

    @Bean
    public JwtRefreshTokenFilter jwtRefreshTokenFilter(
            AuthenticationManager authenticationManager,
            JwtConfig jwtConfig,
            JwtService jwtService,
            UserDetailsServiceCustom userDetailsService,
            com.spring.jwt.jwt.ActiveSessionService activeSessionService) {
        return new JwtRefreshTokenFilter(authenticationManager, jwtConfig, jwtService, userDetailsService, activeSessionService);
    }

    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.debug("Configuring security filter chain");
        AuthenticationManager authManager = authenticationManager(http);

        http.csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers(
                        "/api/**",
                        "/user/**",
                        "/api/users/**",

                        jwtConfig.getUrl(),
                        jwtConfig.getRefreshUrl()
                )
        );

        http.cors(Customizer.withDefaults());

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.headers(headers -> headers
                .xssProtection(xss -> xss
                        .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .contentSecurityPolicy(csp -> csp
                        .policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self'; connect-src 'self'"))
                .frameOptions(frame -> frame.deny())
                .referrerPolicy(referrer -> referrer
                        .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                .permissionsPolicy(permissions -> permissions
                        .policy("camera=(), microphone=(), geolocation=()"))
        );

        log.debug("Configuring URL-based security rules");
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(jwtConfig.getUrl()).permitAll()
                .requestMatchers(jwtConfig.getRefreshUrl()).permitAll()
                .requestMatchers("/api/v1/survey/**").permitAll()
                .requestMatchers("/api/v1/farmer-selfie/**").permitAll()
                .requestMatchers(("/api/v1/farmer-form/**")).permitAll()


                .requestMatchers("/api/auth/v1/register/**").permitAll()
                .requestMatchers("/api/v1/users/password/**").permitAll()
                .requestMatchers("/api/users/**").permitAll()

                .requestMatchers("/api/v1/exam/**").permitAll()

                .requestMatchers("/api/completeProfile/getProfile/**").permitAll()
                .requestMatchers("/api/v1/complete-profile/public/**").permitAll()
                .requestMatchers("/api/v1/interests/**").authenticated()

                .requestMatchers(
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/v*/a*-docs/**",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/swagger-ui.html"
                ).permitAll()

                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/user/**").permitAll()

                .requestMatchers("/api/v1/documents/**").authenticated()

                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                .requestMatchers("/api/v1/**").permitAll()
                .requestMatchers("api/v1/farmer_selfie_Survey/**").permitAll()
                .requestMatchers("/api/v1/lab_report/**").permitAll()
                .requestMatchers("/api/v1/products/**").permitAll()

                .anyRequest().authenticated());

        log.debug("Configuring security filters");
        JwtTokenAuthenticationFilter jwtTokenFilter =
                new JwtTokenAuthenticationFilter(
                        jwtConfig,
                        jwtService,
                        userDetailsService(),
                        activeSessionService
                );

        JwtUsernamePasswordAuthenticationFilter loginFilter =
                new JwtUsernamePasswordAuthenticationFilter(
                        authManager,
                        jwtConfig,
                        jwtService,
                        userRepository,
                        activeSessionService
                );

        JwtRefreshTokenFilter refreshTokenFilter =
                new JwtRefreshTokenFilter(
                        authManager,
                        jwtConfig,
                        jwtService,
                        userDetailsService(),
                        activeSessionService
                );

        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(refreshTokenFilter, JwtUsernamePasswordAuthenticationFilter.class);

        http.addFilterBefore(rateLimitingFilter, JwtTokenAuthenticationFilter.class);
        http.addFilterBefore(xssFilter, JwtTokenAuthenticationFilter.class);
        http.addFilterBefore(sqlInjectionFilter, JwtTokenAuthenticationFilter.class);
        http.addFilterBefore(securityHeadersFilter, JwtTokenAuthenticationFilter.class);

        log.debug("Security configuration completed");
        return http.build();}
//        JwtUsernamePasswordAuthenticationFilter jwtUsernamePasswordAuthenticationFilter = new JwtUsernamePasswordAuthenticationFilter(authenticationManager(http), jwtConfig, jwtService, userRepository, activeSessionService);
//        JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter = new JwtTokenAuthenticationFilter(jwtConfig, jwtService, userDetailsService(), activeSessionService);
//        JwtRefreshTokenFilter jwtRefreshTokenFilter = new JwtRefreshTokenFilter(authenticationManager(http), jwtConfig, jwtService, userDetailsService(), activeSessionService);
//
//        http.addFilterBefore(jwtTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(jwtUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(jwtRefreshTokenFilter, UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(xssFilter, UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(sqlInjectionFilter, UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(securityHeadersFilter, UsernamePasswordAuthenticationFilter.class);
//
//        http.authenticationProvider(customAuthenticationProvider);
//
//        http.exceptionHandling(exceptions -> exceptions
//                .authenticationEntryPoint(securityExceptionHandler())
//                .accessDeniedHandler(securityExceptionHandler())
//        );
//
//        log.debug("Security configuration completed");
//        return http.build();
//    }

    @Bean
    public SecurityExceptionHandler securityExceptionHandler() {
        return new SecurityExceptionHandler(objectMapper);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(allowedOrigins);
                config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowCredentials(true);
                config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
                config.setExposedHeaders(Arrays.asList("Authorization"));
                config.setMaxAge(3600L);
                return config;
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
        return builder.build();
    }


}
//package com.spring.jwt.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.spring.jwt.config.filter.*;
//import com.spring.jwt.exception.SecurityExceptionHandler;
//import com.spring.jwt.jwt.ActiveSessionService;
//import com.spring.jwt.jwt.JwtConfig;
//import com.spring.jwt.jwt.JwtService;
//import com.spring.jwt.repository.UserRepository;
//import com.spring.jwt.service.security.UserDetailsServiceCustom;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Configuration
//@EnableWebSecurity
//@EnableScheduling
//@EnableMethodSecurity(prePostEnabled = true)
//@Slf4j
//public class AppConfig {
//
//    private final UserRepository userRepository;
//    private final JwtService jwtService;
//    private final JwtConfig jwtConfig;
//    private final CustomAuthenticationProvider customAuthenticationProvider;
//    private final SecurityHeadersFilter securityHeadersFilter;
//    private final XssFilter xssFilter;
//    private final SqlInjectionFilter sqlInjectionFilter;
//    private final RateLimitingFilter rateLimitingFilter;
//    private final ActiveSessionService activeSessionService;
//    private final ObjectMapper objectMapper;
//
//    public AppConfig(
//            UserRepository userRepository,
//            JwtService jwtService,
//            JwtConfig jwtConfig,
//            CustomAuthenticationProvider customAuthenticationProvider,
//            SecurityHeadersFilter securityHeadersFilter,
//            XssFilter xssFilter,
//            SqlInjectionFilter sqlInjectionFilter,
//            RateLimitingFilter rateLimitingFilter,
//            ActiveSessionService activeSessionService,
//            ObjectMapper objectMapper) {
//
//        this.userRepository = userRepository;
//        this.jwtService = jwtService;
//        this.jwtConfig = jwtConfig;
//        this.customAuthenticationProvider = customAuthenticationProvider;
//        this.securityHeadersFilter = securityHeadersFilter;
//        this.xssFilter = xssFilter;
//        this.sqlInjectionFilter = sqlInjectionFilter;
//        this.rateLimitingFilter = rateLimitingFilter;
//        this.activeSessionService = activeSessionService;
//        this.objectMapper = objectMapper;
//    }
//
//    /* ================= BEANS ================= */
//
//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public UserDetailsServiceCustom userDetailsService() {
//        return new UserDetailsServiceCustom(userRepository);
//    }
//
//    @Bean
//    public SecurityExceptionHandler securityExceptionHandler() {
//        return new SecurityExceptionHandler(objectMapper);
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//        AuthenticationManagerBuilder builder =
//                http.getSharedObject(AuthenticationManagerBuilder.class);
//
//        builder.authenticationProvider(customAuthenticationProvider)
//                .userDetailsService(userDetailsService())
//                .passwordEncoder(passwordEncoder());
//
//        return builder.build();
//    }
//
//    /* ================= SECURITY FILTER CHAIN ================= */
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        AuthenticationManager authManager = authenticationManager(http);
//
//        JwtTokenAuthenticationFilter jwtTokenFilter =
//                new JwtTokenAuthenticationFilter(
//                        jwtConfig,
//                        jwtService,
//                        userDetailsService(),
//                        activeSessionService
//                );
//
//        JwtUsernamePasswordAuthenticationFilter loginFilter =
//                new JwtUsernamePasswordAuthenticationFilter(
//                        authManager,
//                        jwtConfig,
//                        jwtService,
//                        userRepository,
//                        activeSessionService
//                );
//
//        JwtRefreshTokenFilter refreshTokenFilter =
//                new JwtRefreshTokenFilter(
//                        authManager,
//                        jwtConfig,
//                        jwtService,
//                        userDetailsService(),
//                        activeSessionService
//                );
//
//        http
//                .csrf(csrf -> csrf.disable())
//                .cors(Customizer.withDefaults())
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//                .exceptionHandling(ex -> ex
//                        .authenticationEntryPoint(securityExceptionHandler())
//                        .accessDeniedHandler(securityExceptionHandler())
//                )
//
//                /* ================= AUTHORIZATION ================= */
//                .authorizeHttpRequests(auth -> auth
//
//                        /* ---------- AUTH & SYSTEM ---------- */
//                        .requestMatchers(
//                                "/api/auth/**",
//                                "/api/auth/v1/register/**",
//                                jwtConfig.getUrl(),
//                                jwtConfig.getRefreshUrl(),
//                                "/swagger-ui/**",
//                                "/v3/api-docs/**",
//                                "/jwt/login"
//                        ).permitAll()
//
//                        /* ---------- PUBLIC BUSINESS APIs ---------- */
//                        .requestMatchers(
//                                "/api/public/**",
//                                "/api/v1/survey/**",
//                                "/api/v1/farmer-selfie/**",
//                                "/api/v1/farmer-form/**",
//                                "/api/v1/exam/**",
//                                "/api/completeProfile/getProfile/**",
//                                "/api/v1/complete-profile/public/**",
//                                "/api/v1/lab_report/**",
//                                "/api/v1/users/password/**"
//                        ).permitAll()
//
//                        /* ---------- USER AUTH REQUIRED ---------- */
//                        .requestMatchers(
//                                "/user/**",
//                                "/api/v1/interests/**",
//                                "/api/v1/documents/**",
//                                "/api/v1/profile/**"
//                        ).authenticated()
//
//                        /* ---------- ADMIN ---------- */
//                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
//
//                        /* ---------- FALLBACK ---------- */
//                        .anyRequest().authenticated()
//                );
//
//        /* ================= FILTER ORDER ================= */
//
//        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
//
//        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);
//        http.addFilterAfter(refreshTokenFilter, JwtUsernamePasswordAuthenticationFilter.class);
//
//        http.addFilterBefore(rateLimitingFilter, JwtTokenAuthenticationFilter.class);
//        http.addFilterBefore(xssFilter, JwtTokenAuthenticationFilter.class);
//        http.addFilterBefore(sqlInjectionFilter, JwtTokenAuthenticationFilter.class);
//        http.addFilterBefore(securityHeadersFilter, JwtTokenAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//    /* ================= CORS ================= */
//
//    @Value("#{'${app.cors.allowed-origins:http://localhost:5173,http://localhost:3000}'.split(',')}")
//    private List<String> allowedOrigins;
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        return request -> {
//            CorsConfiguration config = new CorsConfiguration();
//            config.setAllowedOrigins(allowedOrigins);
//            config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//            config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
//            config.setAllowCredentials(true);
//            config.setMaxAge(3600L);
//            return config;
//        };
//    }
//}
