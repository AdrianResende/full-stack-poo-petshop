package com.petshop.controller;

import com.petshop.report.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/cliente/{proprietarioId}")
    public ResponseEntity<byte[]> relatorioCliente(
            @PathVariable Long proprietarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        byte[] pdf = relatorioService.gerarRelatorioCliente(proprietarioId, inicio, fim);

        String dataAtual = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String nomeArquivo = "relatorio_cliente_" + proprietarioId + "_" + dataAtual + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
