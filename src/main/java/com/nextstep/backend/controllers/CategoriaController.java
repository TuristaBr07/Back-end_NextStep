package com.nextstep.backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextstep.backend.dtos.CategoriaDTO;
import com.nextstep.backend.dtos.CategoriaResponseDTO;
import com.nextstep.backend.models.Usuario;
import com.nextstep.backend.services.CategoriaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categorias" )
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> criarCategoria(@Valid @RequestBody CategoriaDTO data) {
        Usuario usuarioLogado = getUsuarioLogado();
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.salvar(data, usuarioLogado));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias() {
        Usuario usuarioLogado = getUsuarioLogado();
        return ResponseEntity.ok(categoriaService.listarPorUsuario(usuarioLogado.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCategoria(@PathVariable Long id) {
        Usuario usuarioLogado = getUsuarioLogado();
        categoriaService.deletar(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }

    private Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
