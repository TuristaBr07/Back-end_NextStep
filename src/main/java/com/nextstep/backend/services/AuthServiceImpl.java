package com.nextstep.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nextstep.backend.dtos.AuthDTO;
import com.nextstep.backend.dtos.RecuperarSenhaDTO;
import com.nextstep.backend.dtos.TokenDTO;
import com.nextstep.backend.exceptions.RegraNegocioException;
import com.nextstep.backend.models.Usuario;
import com.nextstep.backend.repositories.UsuarioRepository;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

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

        String email = data.email().trim().toLowerCase();
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario == null || !passwordEncoder.matches(data.senha(), usuario.getSenha())) {
            logger.warn("Falha de login para o e-mail: {}", email);
            throw new RegraNegocioException("Email ou senha incorretos.");
        }

        String token = tokenService.gerarToken(usuario);
        logger.info("Login realizado com sucesso para o usuário: {}", usuario.getId());
        return new TokenDTO(token, usuario.getId(), usuario.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public void recuperarSenha(RecuperarSenhaDTO data) {
        if (data == null || data.email() == null || data.email().isBlank()) {
            // Resposta genérica é tratada no controller; não revelamos detalhes.
            return;
        }

        String email = data.email().trim().toLowerCase();
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario != null) {
            // TODO: integrar envio de e-mail (SMTP) com token de redefinição de senha.
            // Por enquanto apenas registramos no servidor; nunca expomos ao cliente
            // se o e-mail existe ou não, para evitar enumeração de usuários.
            logger.info("Solicitação de recuperação de senha para usuário existente: {}", usuario.getId());
        } else {
            logger.info("Solicitação de recuperação de senha para e-mail inexistente.");
        }
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
