package com.nextstep.backend.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public AuthServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
            TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Override
    @Transactional
    public String registrar(AuthDTO data) {
        validarCredenciais(data);

        if (usuarioRepository.findByEmail(data.email()) != null) {
            throw new RegraNegocioException("Email já está em uso.");
        }

        Usuario novoUsuario = new Usuario(data.email().trim().toLowerCase(),
                passwordEncoder.encode(data.senha()));
        usuarioRepository.save(novoUsuario);

        return "Usuário criado com sucesso!";
    }

    @Override
    public TokenDTO login(AuthDTO data) {
        validarCredenciais(data);

        Usuario usuario = usuarioRepository.findByEmail(data.email().trim().toLowerCase());

        if (usuario == null || !passwordEncoder.matches(data.senha(), usuario.getSenha())) {
            throw new RegraNegocioException("Email ou senha incorretos.");
        }

        String token = tokenService.gerarToken(usuario);
        return new TokenDTO(token, usuario.getId(), usuario.getEmail());
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
