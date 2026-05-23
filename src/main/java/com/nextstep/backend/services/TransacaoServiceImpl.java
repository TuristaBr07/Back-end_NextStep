package com.nextstep.backend.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nextstep.backend.dtos.RelatorioCategoriaDTO;
import com.nextstep.backend.dtos.TransacaoDTO;
import com.nextstep.backend.dtos.TransacaoResponseDTO;
import com.nextstep.backend.exceptions.RegraNegocioException;
import com.nextstep.backend.models.Transacao;
import com.nextstep.backend.models.Usuario;
import com.nextstep.backend.repositories.TransacaoRepository;

@Service
public class TransacaoServiceImpl implements TransacaoService {

    private final TransacaoRepository transacaoRepository;

    public TransacaoServiceImpl(TransacaoRepository transacaoRepository) {
        this.transacaoRepository = transacaoRepository;
    }

    @Override
    @Transactional
    public TransacaoResponseDTO salvar(TransacaoDTO dto, Usuario usuario) {
        validar(dto);

        Transacao transacao = new Transacao();
        preencherTransacao(transacao, dto);
        transacao.setUsuario(usuario);

        return converterParaDTO(transacaoRepository.save(transacao));
    }

    @Override
    public List<TransacaoResponseDTO> listarPorUsuario(String usuarioId) {
        return transacaoRepository.findByUsuarioIdOrderByDateDesc(usuarioId)
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    @Override
    public Map<String, Double> obterResumoFinanceiro(String usuarioId) {
        Double totalReceitas = transacaoRepository.sumReceitasByUsuarioId(usuarioId);
        Double totalDespesas = transacaoRepository.sumDespesasByUsuarioId(usuarioId);

        if (totalReceitas == null) {
            totalReceitas = 0.0;
        }

        if (totalDespesas == null) {
            totalDespesas = 0.0;
        }

        Double saldo = totalReceitas - totalDespesas;

        Map<String, Double> resumo = new HashMap<>();
        resumo.put("receitas", totalReceitas);
        resumo.put("despesas", totalDespesas);
        resumo.put("saldo", saldo);

        return resumo;
    }

    @Override
    public List<RelatorioCategoriaDTO> obterRelatorioPorCategoria(String usuarioId) {
        return transacaoRepository.findRelatorioByUsuarioId(usuarioId);
    }

    @Override
    @Transactional
    public TransacaoResponseDTO atualizar(Long id, TransacaoDTO dto, Usuario usuario) {
        validar(dto);

        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Transação não encontrada."));

        validarDonoDaTransacao(transacao, usuario);
        preencherTransacao(transacao, dto);

        return converterParaDTO(transacaoRepository.save(transacao));
    }

    @Override
    @Transactional
    public void deletar(Long id, Usuario usuario) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Transação não encontrada."));

        validarDonoDaTransacao(transacao, usuario);
        transacaoRepository.delete(transacao);
    }

    private void preencherTransacao(Transacao transacao, TransacaoDTO dto) {
        transacao.setDate(converterData(dto.date()));
        transacao.setType(dto.type().trim());
        transacao.setCategory(dto.category().trim());
        transacao.setDescription(dto.description() != null ? dto.description().trim() : "");
        transacao.setAmount(dto.amount());
    }

    private void validar(TransacaoDTO dto) {
        if (dto == null) {
            throw new RegraNegocioException("Informe os dados da transação.");
        }

        if (dto.type() == null || dto.type().isBlank()) {
            throw new RegraNegocioException("O tipo da transação é obrigatório.");
        }

        if (dto.category() == null || dto.category().isBlank()) {
            throw new RegraNegocioException("A categoria da transação é obrigatória.");
        }

        if (dto.amount() == null || dto.amount() <= 0) {
            throw new RegraNegocioException("O valor da transação deve ser maior que zero.");
        }
    }

    private void validarDonoDaTransacao(Transacao transacao, Usuario usuario) {
        if (usuario == null || usuario.getId() == null) {
            throw new RegraNegocioException("Usuário não autenticado.");
        }

        if (transacao.getUsuario() == null || !usuario.getId().equals(transacao.getUsuario().getId())) {
            throw new RegraNegocioException("Acesso negado.");
        }
    }

    private LocalDateTime converterData(String dataString) {
        if (dataString == null || dataString.isBlank()) {
            return LocalDateTime.now();
        }

        try {
            return LocalDate.parse(dataString).atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new RegraNegocioException("Data inválida. Use o formato yyyy-MM-dd.");
        }
    }

    private TransacaoResponseDTO converterParaDTO(Transacao transacao) {
        String data = transacao.getDate() != null ? transacao.getDate().toLocalDate().toString() : null;

        return new TransacaoResponseDTO(
                transacao.getId(),
                data,
                transacao.getType(),
                transacao.getCategory(),
                transacao.getDescription(),
                transacao.getAmount()
        );
    }
}