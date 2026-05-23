package com.nextstep.backend.services;

import java.util.List;

import com.nextstep.backend.dtos.CategoriaDTO;
import com.nextstep.backend.dtos.CategoriaResponseDTO;
import com.nextstep.backend.models.Usuario;

public interface CategoriaService {
    CategoriaResponseDTO salvar(CategoriaDTO dto, Usuario usuario);
    List<CategoriaResponseDTO> listarPorUsuario(String usuarioId);
}