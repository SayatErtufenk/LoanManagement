package com.bank.loanmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author Sayat Ertüfenk
 * @since 24/11/2024
 */

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/loans/**").hasAnyRole("ADMIN", "CUSTOMER")
                        .requestMatchers("/customers/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        // CSRF configuration to ignore specific endpoints
        http.csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
        );

        // Frame options configuration for H2 console
        http.headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
        );

        return http.build();
    }

    @Bean
    public UserDetailsService users() {
        var userBuilder = User.builder().passwordEncoder( passwordEncoder()::encode );
        UserDetails admin = userBuilder
                .username( "admin" )
                .password( "adminpass" )
                .roles( "ADMIN" )
                .build();
        UserDetails customer = userBuilder
                .username( "customer" )
                .password( "customerpass" )
                .roles( "CUSTOMER" )
                .build();
        return new InMemoryUserDetailsManager( admin, customer );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}