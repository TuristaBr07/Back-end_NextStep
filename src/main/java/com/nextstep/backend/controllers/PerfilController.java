package com.nextstep.backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextstep.backend.dtos.PerfilDTO;
import com.nextstep.backend.dtos.PerfilResponseDTO;
import com.nextstep.backend.models.Usuario;
import com.nextstep.backend.services.PerfilService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/perfis" )
public class PerfilController {

    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<List<PerfilResponseDTO>> getPerfil(@PathVariable String idUsuario) {
        Usuario usuarioLogado = getUsuarioLogado();
        return ResponseEntity.ok(List.of(perfilService.buscarPerfil(idUsuario, usuarioLogado)));
    }

    @PutMapping("/{idUsuario}")
    public ResponseEntity<List<PerfilResponseDTO>> updatePerfil(
            @PathVariable String idUsuario,
            @Valid @RequestBody PerfilDTO dadosAtualizados
    ) {
        Usuario usuarioLogado = getUsuarioLogado();
        return ResponseEntity.ok(List.of(perfilService.atualizarPerfil(idUsuario, dadosAtualizados, usuarioLogado)));
    }

    private Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}