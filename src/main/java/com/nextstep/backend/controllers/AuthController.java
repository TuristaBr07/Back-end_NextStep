package com.nextstep.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextstep.backend.dtos.AuthDTO;
import com.nextstep.backend.dtos.RecuperarSenhaDTO;
import com.nextstep.backend.dtos.TokenDTO;
import com.nextstep.backend.services.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registrar(@Valid @RequestBody AuthDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(data));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody AuthDTO data) {
        return ResponseEntity.ok(authService.login(data));
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<String> recuperarSenha(@Valid @RequestBody RecuperarSenhaDTO data) {
        authService.recuperarSenha(data);
        return ResponseEntity.ok("Se o e-mail existir, enviaremos instruções.");
    }
}
