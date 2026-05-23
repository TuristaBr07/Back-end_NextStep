package com.nextstep.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nextstep.backend.dtos.CategoriaDTO;
import com.nextstep.backend.dtos.CategoriaResponseDTO;
import com.nextstep.backend.exceptions.RegraNegocioException;
import com.nextstep.backend.models.Categoria;
import com.nextstep.backend.models.Usuario;
import com.nextstep.backend.repositories.CategoriaRepository;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional
    public CategoriaResponseDTO salvar(CategoriaDTO dto, Usuario usuario) {
        validar(dto);

        Categoria categoria = new Categoria();
        categoria.setName(dto.name().trim());
        categoria.setType(dto.type().trim());
        categoria.setUsuario(usuario);

        return converterParaDTO(categoriaRepository.save(categoria));
    }

    @Override
    public List<CategoriaResponseDTO> listarPorUsuario(String usuarioId) {
        return categoriaRepository.findByUsuarioIdOrderByNameAsc(usuarioId)
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    private void validar(CategoriaDTO dto) {
        if (dto == null) {
            throw new RegraNegocioException("Informe os dados da categoria.");
        }

        if (dto.name() == null || dto.name().isBlank()) {
            throw new RegraNegocioException("O nome da categoria é obrigatório.");
        }

        if (dto.type() == null || dto.type().isBlank()) {
            throw new RegraNegocioException("O tipo da categoria é obrigatório.");
        }
    }

    private CategoriaResponseDTO converterParaDTO(Categoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getName(),
                categoria.getType()
        );
    }
}