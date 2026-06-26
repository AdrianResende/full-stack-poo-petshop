package com.petshop.service;

import com.petshop.dto.ServicoDTO;
import com.petshop.model.Servico;
import com.petshop.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ServicoService {

    private final ServicoRepository repository;

    public List<ServicoDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ServicoDTO buscarPorId(Long id) {
        Servico s = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com id: " + id));
        return toDTO(s);
    }

    public ServicoDTO salvar(ServicoDTO dto) {
        if (dto.getId() == null && repository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new RuntimeException("Já existe um serviço com o nome: " + dto.getNome());
        }
        Servico s = toEntity(dto);
        return toDTO(repository.save(s));
    }

    public ServicoDTO atualizar(Long id, ServicoDTO dto) {
        Servico existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com id: " + id));

        if (!existing.getNome().equalsIgnoreCase(dto.getNome()) && repository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new RuntimeException("Já existe um serviço com o nome: " + dto.getNome());
        }

        existing.setNome(dto.getNome());
        existing.setDescricao(dto.getDescricao());
        existing.setPreco(dto.getPreco());
        return toDTO(repository.save(existing));
    }

    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Serviço não encontrado com id: " + id);
        }
        repository.deleteById(id);
    }

    public Servico buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com id: " + id));
    }

    public ServicoDTO toDTO(Servico s) {
        return ServicoDTO.builder()
                .id(s.getId())
                .nome(s.getNome())
                .descricao(s.getDescricao())
                .preco(s.getPreco())
                .build();
    }

    private Servico toEntity(ServicoDTO dto) {
        return Servico.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .preco(dto.getPreco())
                .build();
    }
}
