package com.chatop.security.configurations;

import com.chatop.security.services.SwaggerUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SwaggerSecurityConfig {
    /**
     * Configures security settings for Swagger UI and API documentation access.
     * This configuration allows access to Swagger UI and API docs only to authenticated users.
     * CSRF protection is disabled for these endpoints, and form-based login is enabled.
     * Session management is set to create sessions if required.
     * @param http the HttpSecurity instance to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    @Order(1)
    public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/swagger-ui/**", "/v3/api-docs/**", "/login", "/favicon.ico", "/default-ui.css")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/favicon.ico", "/default-ui.css").permitAll()
                        .anyRequest().authenticated())
                .formLogin(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
        return http.build();
    }

    /**
     * Use SwaggerUserDetailsService for loading user details
     * @param swaggerUserDetailsService the SwaggerUserDetailsService instance
     * @return the UserDetailsService bean
     */
    public UserDetailsService userDetailsService(SwaggerUserDetailsService swaggerUserDetailsService) {
        return swaggerUserDetailsService;
    }

    /**
     * Configures the AuthenticationManager with the provided UserDetailsService and PasswordEncoder.
     * authenticationManager for Swagger UI access
     * @param http the HttpSecurity instance to get the shared AuthenticationManagerBuilder
     * @param userDetailsService the UserDetailsService to load user-specific data
     * @param passwordEncoder the PasswordEncoder to encode and verify passwords
     * @return the configured AuthenticationManager
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder
    ) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return builder.build();
    }
}
