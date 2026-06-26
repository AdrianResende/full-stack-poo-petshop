package com.petshop.controller;

import com.petshop.dto.AnimalDTO;
import com.petshop.service.AnimalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/animais")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService service;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @GetMapping
    public ResponseEntity<List<AnimalDTO>> listarTodos(
            @RequestParam(required = false) Long proprietarioId,
            @RequestParam(required = false) String nome) {
        if (proprietarioId != null) {
            return ResponseEntity.ok(service.buscarPorProprietario(proprietarioId));
        }
        if (nome != null && !nome.isBlank()) {
            return ResponseEntity.ok(service.buscarPorNome(nome));
        }
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<AnimalDTO> criar(@Valid @RequestBody AnimalDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimalDTO> atualizar(
            @PathVariable Long id, @Valid @RequestBody AnimalDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @PostMapping("/{id}/foto")
    public ResponseEntity<String> uploadFoto(
            @PathVariable Long id, @RequestParam("foto") MultipartFile foto) throws IOException {
        String extensao = foto.getOriginalFilename() != null
                ? foto.getOriginalFilename().substring(foto.getOriginalFilename().lastIndexOf("."))
                : ".jpg";
        String nomeArquivo = UUID.randomUUID() + extensao;

        Path diretorio = Paths.get(uploadDir);
        Files.createDirectories(diretorio);
        Files.copy(foto.getInputStream(), diretorio.resolve(nomeArquivo));

        String fotoUrl = "/uploads/" + nomeArquivo;
        service.atualizarFoto(id, fotoUrl);
        return ResponseEntity.ok(fotoUrl);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
