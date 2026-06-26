package com.petshop.service;

import com.petshop.dto.ProprietarioDTO;
import com.petshop.model.Proprietario;
import com.petshop.repository.ProprietarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProprietarioService {

    private final ProprietarioRepository repository;

    public List<ProprietarioDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProprietarioDTO buscarPorId(Long id) {
        Proprietario p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proprietário não encontrado com id: " + id));
        return toDTO(p);
    }

    public List<ProprietarioDTO> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProprietarioDTO salvar(ProprietarioDTO dto) {
        if (dto.getId() == null && repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Já existe um proprietário com este email: " + dto.getEmail());
        }
        Proprietario p = toEntity(dto);
        return toDTO(repository.save(p));
    }

    public ProprietarioDTO atualizar(Long id, ProprietarioDTO dto) {
        Proprietario existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proprietário não encontrado com id: " + id));

        if (!existing.getEmail().equals(dto.getEmail()) && repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Já existe um proprietário com este email: " + dto.getEmail());
        }

        existing.setNome(dto.getNome());
        existing.setEndereco(dto.getEndereco());
        existing.setTelefone(dto.getTelefone());
        existing.setEmail(dto.getEmail());
        return toDTO(repository.save(existing));
    }

    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Proprietário não encontrado com id: " + id);
        }
        repository.deleteById(id);
    }

    public Proprietario buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proprietário não encontrado com id: " + id));
    }

    public ProprietarioDTO toDTO(Proprietario p) {
        return ProprietarioDTO.builder()
                .id(p.getId())
                .nome(p.getNome())
                .endereco(p.getEndereco())
                .telefone(p.getTelefone())
                .email(p.getEmail())
                .build();
    }

    private Proprietario toEntity(ProprietarioDTO dto) {
        return Proprietario.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .endereco(dto.getEndereco())
                .telefone(dto.getTelefone())
                .email(dto.getEmail())
                .build();
    }
}
