package com.nextstep.backend.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nextstep.backend.dtos.AuthDTO;
import com.nextstep.backend.dtos.TokenDTO;
import com.nextstep.backend.services.AuthService;

@RestController
@RequestMapping("/auth")
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

    @GetMapping(value = "/verify-email", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> verificarEmail(@RequestParam String token) {
        try {
            authService.verificarEmail(token);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(buildHtmlPage(
                            "E-mail verificado!",
                            "Sua conta foi ativada com sucesso. Abra o aplicativo NextStep e faça login.",
                            true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_HTML)
                    .body(buildHtmlPage(
                            "Link inválido",
                            "O link de verificação é inválido ou já foi utilizado.",
                            false));
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> reenviarVerificacao(@RequestBody Map<String, String> body) {
        authService.reenviarVerificacao(body.get("email"));
        return ResponseEntity.ok("Se o e-mail existir e ainda não estiver verificado, um novo link foi enviado.");
    }

    private String buildHtmlPage(String title, String message, boolean success) {
        String color = success ? "#059669" : "#EF4444";
        String icon = success ? "✅" : "❌";
        return "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>NextStep</title>" +
            "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"></head>" +
            "<body style=\"font-family:Arial,sans-serif;display:flex;align-items:center;justify-content:center;" +
            "min-height:100vh;background-color:#f8fafc;margin:0;\">" +
            "<div style=\"text-align:center;background:white;padding:48px;border-radius:16px;" +
            "box-shadow:0 4px 24px rgba(0,0,0,0.1);max-width:400px;\">" +
            "<div style=\"font-size:48px;margin-bottom:16px;\">" + icon + "</div>" +
            "<h2 style=\"color:" + color + ";margin:0 0 12px;\">" + title + "</h2>" +
            "<p style=\"color:#475569;margin:0;\">" + message + "</p>" +
            "</div></body></html>";
    }
}
