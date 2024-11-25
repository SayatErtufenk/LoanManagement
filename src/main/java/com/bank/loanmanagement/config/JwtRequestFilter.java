package com.bank.loanmanagement.config;

import com.bank.loanmanagement.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author Sayat Ertüfenk
 * @since 25/11/2024
 */

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain )
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader( "Authorization" );

        String username = null;
        String jwt = null;

        // JWT "Bearer token" formatında olmalı
        if ( authorizationHeader != null && authorizationHeader.startsWith( "Bearer " ) ) {
            jwt = authorizationHeader.substring( 7 ); // "Bearer " kelimesini atla
            username = jwtUtil.extractUsername( jwt );
        }

        if ( username != null && SecurityContextHolder.getContext().getAuthentication() == null ) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername( username );

            if ( jwtUtil.validateToken( jwt, userDetails ) ) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities() );
                authToken.setDetails( new WebAuthenticationDetailsSource().buildDetails( request ) );
                SecurityContextHolder.getContext().setAuthentication( authToken );
            }
        }
        chain.doFilter( request, response );
    }
}
