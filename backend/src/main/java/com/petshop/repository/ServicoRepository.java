package com.petshop.repository;

import com.petshop.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {

    Optional<Servico> findByNomeIgnoreCase(String nome);

    List<Servico> findByNomeContainingIgnoreCase(String nome);

    boolean existsByNomeIgnoreCase(String nome);
}
