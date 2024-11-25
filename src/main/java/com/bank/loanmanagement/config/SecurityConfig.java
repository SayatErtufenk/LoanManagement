package com.bank.loanmanagement.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author Sayat Ertüfenk
 * @since 24/11/2024
 */

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager( AuthenticationConfiguration authenticationConfiguration ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain( HttpSecurity http ) throws Exception {
        http
                .csrf( csrf -> csrf.disable() )
                .authorizeHttpRequests( authorize -> authorize
                        .requestMatchers( "/authenticate", "/h2-console/**" ).permitAll()
                        .requestMatchers( "/loans/my-loans" ).hasRole( "CUSTOMER" )
                        .requestMatchers( "/customers/**" ).hasRole( "ADMIN" )
                        .requestMatchers( "/loans/**", "/payments/**" ).hasAnyRole( "ADMIN", "CUSTOMER" )
                        .anyRequest().authenticated()
                )
                .sessionManagement( session -> session
                        .sessionCreationPolicy( SessionCreationPolicy.STATELESS )
                )
                .headers( headers -> headers
                        .frameOptions( frameOptions -> frameOptions.disable() )
                );

        http.addFilterBefore( jwtRequestFilter, UsernamePasswordAuthenticationFilter.class );

        return http.build();
    }
}