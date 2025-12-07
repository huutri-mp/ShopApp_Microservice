package com.example.uploadfileservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Các endpoint internal dùng Basic Auth
    private static final String[] INTERNAL_ENDPOINTS = {
            "/api/v1/internal/upload/**",

    };

    @Value("${auth.username}")
    private String authUsername;

    @Value("${auth.password}")
    private String authPassword;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity

                .authorizeHttpRequests(request -> request
                        .requestMatchers(INTERNAL_ENDPOINTS).authenticated()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(httpBasic -> httpBasic
                        .authenticationEntryPoint(basicAuthenticationEntryPoint())
                        .realmName("Upload Service Internal API")
                );

        return httpSecurity.build();
    }

    @Bean
    public InMemoryUserDetailsManager internalUserDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails serviceUser = User.withUsername(authUsername)
                .password(passwordEncoder.encode(authPassword))
                .roles("INTERNAL_SERVICE")
                .build();

        return new InMemoryUserDetailsManager(serviceUser);
    }

    @Bean
    public BasicAuthenticationEntryPoint basicAuthenticationEntryPoint() {
        BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName("Upload Service Internal API");
        return entryPoint;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

}