package com.plango.api.controller;

import com.plango.api.security.UserAuthDetails;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class AbstractController {
    
    public static UserAuthDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (UserAuthDetails) auth.getPrincipal();
    }
}