package com.nextstep.backend.services;

import java.util.List;
import java.util.Map;

import com.nextstep.backend.dtos.RelatorioCategoriaDTO;
import com.nextstep.backend.dtos.TransacaoDTO;
import com.nextstep.backend.dtos.TransacaoResponseDTO;
import com.nextstep.backend.models.Usuario;

public interface TransacaoService {
    TransacaoResponseDTO salvar(TransacaoDTO dto, Usuario usuario);
    List<TransacaoResponseDTO> listarPorUsuario(String usuarioId);
    Map<String, Double> obterResumoFinanceiro(String usuarioId);
    List<RelatorioCategoriaDTO> obterRelatorioPorCategoria(String usuarioId);
    TransacaoResponseDTO atualizar(Long id, TransacaoDTO dto, Usuario usuario);
    void deletar(Long id, Usuario usuario);
    List<TransacaoResponseDTO> listarPendentes(String usuarioId);
}