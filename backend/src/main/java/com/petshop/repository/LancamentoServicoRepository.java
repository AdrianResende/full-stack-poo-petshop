package com.petshop.repository;

import com.petshop.model.LancamentoServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LancamentoServicoRepository extends JpaRepository<LancamentoServico, Long> {

    List<LancamentoServico> findByAnimalId(Long animalId);

    @Query("SELECT l FROM LancamentoServico l JOIN FETCH l.servico JOIN FETCH l.animal a JOIN FETCH a.proprietario " +
           "WHERE l.animal.id = :animalId ORDER BY l.data DESC")
    List<LancamentoServico> findByAnimalIdWithDetails(@Param("animalId") Long animalId);

    @Query("SELECT l FROM LancamentoServico l JOIN FETCH l.servico JOIN FETCH l.animal a JOIN FETCH a.proprietario " +
           "WHERE l.animal.id = :animalId AND l.data BETWEEN :dataInicio AND :dataFim ORDER BY l.data DESC")
    List<LancamentoServico> findByAnimalIdAndPeriodo(
            @Param("animalId") Long animalId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim);

    @Query("SELECT l FROM LancamentoServico l JOIN FETCH l.servico JOIN FETCH l.animal a JOIN FETCH a.proprietario " +
           "WHERE l.animal.id = :animalId AND l.servico.id = :servicoId ORDER BY l.data DESC")
    List<LancamentoServico> findByAnimalIdAndServicoId(
            @Param("animalId") Long animalId,
            @Param("servicoId") Long servicoId);

    @Query("SELECT l FROM LancamentoServico l JOIN FETCH l.servico JOIN FETCH l.animal a JOIN FETCH a.proprietario " +
           "WHERE l.animal.id = :animalId AND l.servico.id = :servicoId " +
           "AND l.data BETWEEN :dataInicio AND :dataFim ORDER BY l.data DESC")
    List<LancamentoServico> findByAnimalIdAndServicoIdAndPeriodo(
            @Param("animalId") Long animalId,
            @Param("servicoId") Long servicoId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim);

    @Query("SELECT l FROM LancamentoServico l JOIN FETCH l.servico JOIN FETCH l.animal a JOIN FETCH a.proprietario " +
           "WHERE a.proprietario.id = :proprietarioId AND l.data BETWEEN :dataInicio AND :dataFim ORDER BY l.data DESC")
    List<LancamentoServico> findByProprietarioIdAndPeriodo(
            @Param("proprietarioId") Long proprietarioId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim);

    @Query("SELECT l FROM LancamentoServico l JOIN FETCH l.servico JOIN FETCH l.animal a JOIN FETCH a.proprietario " +
           "WHERE a.proprietario.id = :proprietarioId ORDER BY l.data DESC")
    List<LancamentoServico> findByProprietarioId(@Param("proprietarioId") Long proprietarioId);
}
