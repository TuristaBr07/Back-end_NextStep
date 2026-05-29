package com.nextstep.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nextstep.backend.dtos.RelatorioCategoriaDTO;
import com.nextstep.backend.models.Transacao;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByUsuarioId(String usuarioId);

    List<Transacao> findByUsuarioIdOrderByDateDesc(String usuarioId);

    List<Transacao> findByUsuarioIdAndStatusIgnoreCaseOrderByDateAsc(String usuarioId, String status);

    @Query("SELECT COALESCE(SUM(t.amount), 0.0) FROM Transacao t WHERE t.usuario.id = :usuarioId AND (LOWER(t.type) = 'receita' OR LOWER(t.type) = 'income')")
    Double sumReceitasByUsuarioId(@Param("usuarioId") String usuarioId);

    @Query("SELECT COALESCE(SUM(t.amount), 0.0) FROM Transacao t WHERE t.usuario.id = :usuarioId AND (LOWER(t.type) = 'despesa' OR LOWER(t.type) = 'expense')")
    Double sumDespesasByUsuarioId(@Param("usuarioId") String usuarioId);

    @Query("SELECT new com.nextstep.backend.dtos.RelatorioCategoriaDTO(t.category, t.type, SUM(t.amount)) FROM Transacao t WHERE t.usuario.id = :usuarioId GROUP BY t.category, t.type")
    List<RelatorioCategoriaDTO> findRelatorioByUsuarioId(@Param("usuarioId") String usuarioId);
}