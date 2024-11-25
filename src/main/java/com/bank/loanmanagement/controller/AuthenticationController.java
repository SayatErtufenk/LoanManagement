package com.bank.loanmanagement.controller;

import com.bank.loanmanagement.config.MyUserDetailsService;
import com.bank.loanmanagement.dto.AuthenticationRequest;
import com.bank.loanmanagement.dto.AuthenticationResponse;
import com.bank.loanmanagement.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sayat Ertüfenk
 * @since 25/11/2024
 */

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping( "/authenticate" )
    public AuthenticationResponse createAuthenticationToken(
            @RequestBody AuthenticationRequest request ) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken( request.getUsername(), request.getPassword() ) );
        } catch (BadCredentialsException e) {
            throw new Exception( "Geçersiz kullanıcı adı veya şifre", e );
        }
        
        final UserDetails userDetails = userDetailsService.loadUserByUsername( request.getUsername() );
        final String jwt = jwtUtil.generateToken( userDetails );
        return new AuthenticationResponse( jwt );
    }
}