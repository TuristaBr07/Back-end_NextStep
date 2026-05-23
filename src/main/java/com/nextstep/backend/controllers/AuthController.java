package com.nextstep.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextstep.backend.dtos.AuthDTO;
import com.nextstep.backend.dtos.TokenDTO;
import com.nextstep.backend.services.AuthService;

@RestController
@RequestMapping("/auth" )
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registrar(@RequestBody AuthDTO data) {
        return ResponseEntity.ok(authService.registrar(data));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody AuthDTO data) {
        return ResponseEntity.ok(authService.login(data));
    }
}