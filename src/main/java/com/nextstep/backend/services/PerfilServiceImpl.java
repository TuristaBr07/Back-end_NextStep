package com.nextstep.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nextstep.backend.dtos.PerfilDTO;
import com.nextstep.backend.dtos.PerfilResponseDTO;
import com.nextstep.backend.exceptions.RegraNegocioException;
import com.nextstep.backend.models.Usuario;
import com.nextstep.backend.repositories.UsuarioRepository;

@Service
public class PerfilServiceImpl implements PerfilService {

    private final UsuarioRepository usuarioRepository;

    public PerfilServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public PerfilResponseDTO buscarPerfil(String idUsuario, Usuario usuarioLogado) {
        validarAcesso(idUsuario, usuarioLogado);

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado."));

        return converterParaDTO(usuario);
    }

    @Override
    @Transactional
    public PerfilResponseDTO atualizarPerfil(String idUsuario, PerfilDTO dto, Usuario usuarioLogado) {
        validarAcesso(idUsuario, usuarioLogado);

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado."));

        if (dto != null) {
            if (dto.fullName() != null) {
                usuario.setFullName(dto.fullName());
            }

            if (dto.companyName() != null) {
                usuario.setCompanyName(dto.companyName());
            }

            if (dto.avatar() != null) {
                usuario.setAvatar(dto.avatar());
            }
        }

        return converterParaDTO(usuarioRepository.save(usuario));
    }

    private void validarAcesso(String idUsuario, Usuario usuarioLogado) {
        if (usuarioLogado == null || usuarioLogado.getId() == null) {
            throw new RegraNegocioException("Usuário não autenticado.");
        }

        if (idUsuario == null || idUsuario.isBlank()) {
            throw new RegraNegocioException("Informe o id do usuário.");
        }

        if (!usuarioLogado.getId().equals(idUsuario)) {
            throw new RegraNegocioException("Acesso negado.");
        }
    }

    private PerfilResponseDTO converterParaDTO(Usuario usuario) {
        return new PerfilResponseDTO(
                usuario.getId(),
                usuario.getFullName() != null ? usuario.getFullName() : "Usuário NextStep",
                usuario.getCompanyName(),
                usuario.getAvatar()
        );
    }
}