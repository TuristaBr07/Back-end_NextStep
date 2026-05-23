package com.nextstep.backend.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextstep.backend.dtos.RelatorioCategoriaDTO;
import com.nextstep.backend.dtos.TransacaoDTO;
import com.nextstep.backend.dtos.TransacaoResponseDTO;
import com.nextstep.backend.models.Usuario;
import com.nextstep.backend.services.TransacaoService;

@RestController
@RequestMapping("/transacoes" )
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @PostMapping
    public ResponseEntity<TransacaoResponseDTO> criarTransacao(@RequestBody TransacaoDTO data) {
        Usuario usuarioLogado = getUsuarioLogado();
        return ResponseEntity.ok(transacaoService.salvar(data, usuarioLogado));
    }

    @GetMapping
    public ResponseEntity<List<TransacaoResponseDTO>> listarTransacoes() {
        Usuario usuarioLogado = getUsuarioLogado();
        return ResponseEntity.ok(transacaoService.listarPorUsuario(usuarioLogado.getId()));
    }

    @GetMapping("/resumo")
    public ResponseEntity<Map<String, Double>> obterResumo() {
        Usuario usuarioLogado = getUsuarioLogado();
        return ResponseEntity.ok(transacaoService.obterResumoFinanceiro(usuarioLogado.getId()));
    }

    @GetMapping("/relatorio")
    public ResponseEntity<List<RelatorioCategoriaDTO>> obterRelatorio() {
        Usuario usuarioLogado = getUsuarioLogado();
        return ResponseEntity.ok(transacaoService.obterRelatorioPorCategoria(usuarioLogado.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransacaoResponseDTO> atualizarTransacao(@PathVariable Long id, @RequestBody TransacaoDTO data) {
        Usuario usuarioLogado = getUsuarioLogado();
        return ResponseEntity.ok(transacaoService.atualizar(id, data, usuarioLogado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTransacao(@PathVariable Long id) {
        Usuario usuarioLogado = getUsuarioLogado();
        transacaoService.deletar(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }

    private Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}