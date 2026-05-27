package com.nextstep.backend.services;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.nextstep.backend.dtos.AuthDTO;
import com.nextstep.backend.dtos.TokenDTO;
import com.nextstep.backend.exceptions.RegraNegocioException;
import com.nextstep.backend.models.Usuario;
import com.nextstep.backend.repositories.UsuarioRepository;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final EmailService emailService;

    public AuthServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
            TokenService tokenService, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public String registrar(AuthDTO data) {
        validarCredenciais(data);

        if (usuarioRepository.findByEmail(data.email()) != null) {
            throw new RegraNegocioException("Email já está em uso.");
        }

        String token = UUID.randomUUID().toString();
        Usuario novoUsuario = new Usuario(data.email().trim().toLowerCase(), passwordEncoder.encode(data.senha()));
        novoUsuario.setEmailVerificationToken(token);
        novoUsuario.setEmailVerified(false);
        usuarioRepository.save(novoUsuario);

        emailService.sendVerificationEmail(novoUsuario.getEmail(), token);

        return "Cadastro realizado! Verifique seu e-mail para ativar a conta.";
    }

    @Override
    public TokenDTO login(AuthDTO data) {
        validarCredenciais(data);

        Usuario usuario = usuarioRepository.findByEmail(data.email().trim().toLowerCase());

        if (usuario == null || !passwordEncoder.matches(data.senha(), usuario.getSenha())) {
            throw new RegraNegocioException("Email ou senha incorretos.");
        }

        if (!usuario.isEmailVerified()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "E-mail não verificado. Confirme seu e-mail antes de fazer login.");
        }

        String jwtToken = tokenService.gerarToken(usuario);
        return new TokenDTO(jwtToken, usuario.getId(), usuario.getEmail());
    }

    @Override
    @Transactional
    public String verificarEmail(String token) {
        Usuario usuario = usuarioRepository.findByEmailVerificationToken(token);
        if (usuario == null) {
            throw new RegraNegocioException("Token de verificação inválido ou já utilizado.");
        }
        usuario.setEmailVerified(true);
        usuario.setEmailVerificationToken(null);
        usuarioRepository.save(usuario);
        return "E-mail verificado com sucesso!";
    }

    @Override
    @Transactional
    public void reenviarVerificacao(String email) {
        if (email == null || email.isBlank()) return;
        Usuario usuario = usuarioRepository.findByEmail(email.trim().toLowerCase());
        if (usuario == null || usuario.isEmailVerified()) return;
        String token = UUID.randomUUID().toString();
        usuario.setEmailVerificationToken(token);
        usuarioRepository.save(usuario);
        emailService.sendVerificationEmail(usuario.getEmail(), token);
    }

    private void validarCredenciais(AuthDTO data) {
        if (data == null) {
            throw new RegraNegocioException("Informe email e senha.");
        }

        if (data.email() == null || data.email().isBlank()) {
            throw new RegraNegocioException("O email é obrigatório.");
        }

        if (data.senha() == null || data.senha().isBlank()) {
            throw new RegraNegocioException("A senha é obrigatória.");
        }
    }
}
