package com.petshop.controller;

import com.petshop.dto.LancamentoServicoDTO;
import com.petshop.service.LancamentoServicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoServicoController {

    private final LancamentoServicoService service;

    @GetMapping
    public ResponseEntity<List<LancamentoServicoDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LancamentoServicoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<LancamentoServicoDTO>> buscarPorAnimal(
            @PathVariable Long animalId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) Long servicoId) {

        List<LancamentoServicoDTO> resultado;

        if (servicoId != null && inicio != null && fim != null) {
            resultado = service.buscarPorAnimalServicoEPeriodo(animalId, servicoId, inicio, fim);
        } else if (servicoId != null) {
            resultado = service.buscarPorAnimalEServico(animalId, servicoId);
        } else if (inicio != null && fim != null) {
            resultado = service.buscarPorAnimalEPeriodo(animalId, inicio, fim);
        } else {
            resultado = service.buscarPorAnimal(animalId);
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/proprietario/{proprietarioId}")
    public ResponseEntity<List<LancamentoServicoDTO>> buscarPorProprietario(
            @PathVariable Long proprietarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        if (inicio != null && fim != null) {
            return ResponseEntity.ok(service.buscarPorProprietarioEPeriodo(proprietarioId, inicio, fim));
        }
        return ResponseEntity.ok(service.buscarPorProprietario(proprietarioId));
    }

    @PostMapping
    public ResponseEntity<LancamentoServicoDTO> criar(@Valid @RequestBody LancamentoServicoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LancamentoServicoDTO> atualizar(
            @PathVariable Long id, @Valid @RequestBody LancamentoServicoDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
