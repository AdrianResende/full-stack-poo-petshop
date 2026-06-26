package com.petshop.repository;

import com.petshop.model.Proprietario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProprietarioRepository extends JpaRepository<Proprietario, Long> {

    Optional<Proprietario> findByEmail(String email);

    @Query("SELECT p FROM Proprietario p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Proprietario> findByNomeContainingIgnoreCase(String nome);

    boolean existsByEmail(String email);
}
