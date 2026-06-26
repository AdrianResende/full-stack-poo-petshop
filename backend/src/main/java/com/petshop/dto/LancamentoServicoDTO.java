package com.petshop.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LancamentoServicoDTO {
    private Long id;

    @NotNull(message = "Data é obrigatória")
    private LocalDate data;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01")
    private BigDecimal valor;

    private String observacoes;

    @NotNull(message = "Animal é obrigatório")
    private Long animalId;

    private String animalNome;

    @NotNull(message = "Serviço é obrigatório")
    private Long servicoId;

    private String servicoNome;

    private Long proprietarioId;
    private String proprietarioNome;
}
