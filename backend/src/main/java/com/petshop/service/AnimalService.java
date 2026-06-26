package com.petshop.service;

import com.petshop.dto.AnimalDTO;
import com.petshop.model.Animal;
import com.petshop.model.Proprietario;
import com.petshop.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AnimalService {

    private final AnimalRepository repository;
    private final ProprietarioService proprietarioService;

    public List<AnimalDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AnimalDTO buscarPorId(Long id) {
        Animal a = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado com id: " + id));
        return toDTO(a);
    }

    public List<AnimalDTO> buscarPorProprietario(Long proprietarioId) {
        return repository.findByProprietarioId(proprietarioId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<AnimalDTO> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AnimalDTO salvar(AnimalDTO dto) {
        Proprietario proprietario = proprietarioService.buscarEntidadePorId(dto.getProprietarioId());
        Animal animal = toEntity(dto, proprietario);
        return toDTO(repository.save(animal));
    }

    public AnimalDTO atualizar(Long id, AnimalDTO dto) {
        Animal existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado com id: " + id));
        Proprietario proprietario = proprietarioService.buscarEntidadePorId(dto.getProprietarioId());

        existing.setNome(dto.getNome());
        existing.setEspecie(dto.getEspecie());
        existing.setRaca(dto.getRaca());
        existing.setIdade(dto.getIdade());
        existing.setSexo(dto.getSexo());
        existing.setPeso(dto.getPeso());
        existing.setProprietario(proprietario);
        if (dto.getFotoUrl() != null) {
            existing.setFotoUrl(dto.getFotoUrl());
        }
        return toDTO(repository.save(existing));
    }

    public void atualizarFoto(Long id, String fotoUrl) {
        Animal animal = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado com id: " + id));
        animal.setFotoUrl(fotoUrl);
        repository.save(animal);
    }

    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Animal não encontrado com id: " + id);
        }
        repository.deleteById(id);
    }

    public Animal buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado com id: " + id));
    }

    public AnimalDTO toDTO(Animal a) {
        return AnimalDTO.builder()
                .id(a.getId())
                .nome(a.getNome())
                .especie(a.getEspecie())
                .raca(a.getRaca())
                .idade(a.getIdade())
                .sexo(a.getSexo())
                .peso(a.getPeso())
                .fotoUrl(a.getFotoUrl())
                .proprietarioId(a.getProprietario().getId())
                .proprietarioNome(a.getProprietario().getNome())
                .build();
    }

    private Animal toEntity(AnimalDTO dto, Proprietario proprietario) {
        return Animal.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .especie(dto.getEspecie())
                .raca(dto.getRaca())
                .idade(dto.getIdade())
                .sexo(dto.getSexo())
                .peso(dto.getPeso())
                .fotoUrl(dto.getFotoUrl())
                .proprietario(proprietario)
                .build();
    }
}
