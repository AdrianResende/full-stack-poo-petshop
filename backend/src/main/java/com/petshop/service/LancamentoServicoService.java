package com.petshop.service;

import com.petshop.dto.LancamentoServicoDTO;
import com.petshop.model.Animal;
import com.petshop.model.LancamentoServico;
import com.petshop.model.Servico;
import com.petshop.repository.LancamentoServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LancamentoServicoService {

    private final LancamentoServicoRepository repository;
    private final AnimalService animalService;
    private final ServicoService servicoService;

    public List<LancamentoServicoDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public LancamentoServicoDTO buscarPorId(Long id) {
        LancamentoServico l = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lançamento não encontrado com id: " + id));
        return toDTO(l);
    }

    public List<LancamentoServicoDTO> buscarPorAnimal(Long animalId) {
        return repository.findByAnimalIdWithDetails(animalId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<LancamentoServicoDTO> buscarPorAnimalEPeriodo(Long animalId, LocalDate inicio, LocalDate fim) {
        return repository.findByAnimalIdAndPeriodo(animalId, inicio, fim).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<LancamentoServicoDTO> buscarPorAnimalEServico(Long animalId, Long servicoId) {
        return repository.findByAnimalIdAndServicoId(animalId, servicoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<LancamentoServicoDTO> buscarPorAnimalServicoEPeriodo(
            Long animalId, Long servicoId, LocalDate inicio, LocalDate fim) {
        return repository.findByAnimalIdAndServicoIdAndPeriodo(animalId, servicoId, inicio, fim).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<LancamentoServicoDTO> buscarPorProprietario(Long proprietarioId) {
        return repository.findByProprietarioId(proprietarioId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<LancamentoServicoDTO> buscarPorProprietarioEPeriodo(
            Long proprietarioId, LocalDate inicio, LocalDate fim) {
        return repository.findByProprietarioIdAndPeriodo(proprietarioId, inicio, fim).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public LancamentoServicoDTO salvar(LancamentoServicoDTO dto) {
        Animal animal = animalService.buscarEntidadePorId(dto.getAnimalId());
        Servico servico = servicoService.buscarEntidadePorId(dto.getServicoId());

        LancamentoServico lancamento = LancamentoServico.builder()
                .data(dto.getData())
                .valor(dto.getValor())
                .observacoes(dto.getObservacoes())
                .animal(animal)
                .servico(servico)
                .build();

        return toDTO(repository.save(lancamento));
    }

    public LancamentoServicoDTO atualizar(Long id, LancamentoServicoDTO dto) {
        LancamentoServico existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lançamento não encontrado com id: " + id));
        Animal animal = animalService.buscarEntidadePorId(dto.getAnimalId());
        Servico servico = servicoService.buscarEntidadePorId(dto.getServicoId());

        existing.setData(dto.getData());
        existing.setValor(dto.getValor());
        existing.setObservacoes(dto.getObservacoes());
        existing.setAnimal(animal);
        existing.setServico(servico);
        return toDTO(repository.save(existing));
    }

    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Lançamento não encontrado com id: " + id);
        }
        repository.deleteById(id);
    }

    public LancamentoServico buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lançamento não encontrado com id: " + id));
    }

    public LancamentoServicoDTO toDTO(LancamentoServico l) {
        return LancamentoServicoDTO.builder()
                .id(l.getId())
                .data(l.getData())
                .valor(l.getValor())
                .observacoes(l.getObservacoes())
                .animalId(l.getAnimal().getId())
                .animalNome(l.getAnimal().getNome())
                .servicoId(l.getServico().getId())
                .servicoNome(l.getServico().getNome())
                .proprietarioId(l.getAnimal().getProprietario().getId())
                .proprietarioNome(l.getAnimal().getProprietario().getNome())
                .build();
    }
}
