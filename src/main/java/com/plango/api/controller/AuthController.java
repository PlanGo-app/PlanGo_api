package com.plango.api.controller;

import com.plango.api.dto.CredentialDto;
import com.plango.api.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/auth")
public interface AuthController {

    @PostMapping("/login")
    ResponseEntity<String> login(@RequestBody CredentialDto credentials);

    @PostMapping("/signup")
    ResponseEntity<String> signup(@RequestBody UserDto userDto);
}
