package com.stateloan.lms.service;

import com.stateloan.lms.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    public String authenticate(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            String jwt = jwtUtils.generateJwtToken(authentication);
            
            logger.info("User {} authenticated successfully", username);
            return jwt;
            
        } catch (Exception e) {
            logger.error("Authentication failed for user {}: {}", username, e.getMessage());
            throw new RuntimeException("Invalid username or password");
        }
    }
    
    public void logout() {
        SecurityContextHolder.clearContext();
    }
    
    public boolean validateToken(String token) {
        return jwtUtils.validateJwtToken(token);
    }
    
    public String getUsernameFromToken(String token) {
        return jwtUtils.getUsernameFromJwtToken(token);
    }
    
    public Long getUserIdFromToken(String token) {
        return jwtUtils.getUserIdFromJwtToken(token);
    }
}