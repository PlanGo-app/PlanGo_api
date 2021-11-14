package com.plango.api.controller;

import com.plango.api.dto.CredentialDto;
import com.plango.api.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthController {

    @PostMapping("/login")
    ResponseEntity<String> login(@RequestBody CredentialDto credentials);

    @PostMapping("/signup")
    ResponseEntity<String> signup(@RequestBody User newUser);
}
