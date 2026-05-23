package com.nextstep.backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextstep.backend.dtos.CategoriaDTO;
import com.nextstep.backend.dtos.CategoriaResponseDTO;
import com.nextstep.backend.models.Usuario;
import com.nextstep.backend.services.CategoriaService;

@RestController
@RequestMapping("/categorias" )
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> criarCategoria(@RequestBody CategoriaDTO data) {
        Usuario usuarioLogado = getUsuarioLogado();
        return ResponseEntity.ok(categoriaService.salvar(data, usuarioLogado));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias() {
        Usuario usuarioLogado = getUsuarioLogado();
        return ResponseEntity.ok(categoriaService.listarPorUsuario(usuarioLogado.getId()));
    }

    private Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}