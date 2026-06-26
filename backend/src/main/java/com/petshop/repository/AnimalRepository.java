package com.petshop.repository;

import com.petshop.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    List<Animal> findByProprietarioId(Long proprietarioId);

    @Query("SELECT a FROM Animal a WHERE LOWER(a.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Animal> findByNomeContainingIgnoreCase(@Param("nome") String nome);

    List<Animal> findByEspecieIgnoreCase(String especie);

    @Query("SELECT a FROM Animal a JOIN FETCH a.proprietario WHERE a.proprietario.id = :proprietarioId")
    List<Animal> findByProprietarioIdWithProprietario(@Param("proprietarioId") Long proprietarioId);
}
