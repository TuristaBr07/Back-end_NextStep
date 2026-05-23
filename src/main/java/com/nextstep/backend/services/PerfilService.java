package com.nextstep.backend.services;

import com.nextstep.backend.dtos.PerfilDTO;
import com.nextstep.backend.dtos.PerfilResponseDTO;
import com.nextstep.backend.models.Usuario;

public interface PerfilService {
    PerfilResponseDTO buscarPerfil(String idUsuario, Usuario usuarioLogado);
    PerfilResponseDTO atualizarPerfil(String idUsuario, PerfilDTO dto, Usuario usuarioLogado);
}