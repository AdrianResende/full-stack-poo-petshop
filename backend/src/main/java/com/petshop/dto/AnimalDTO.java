package com.petshop.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnimalDTO {
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Espécie é obrigatória")
    private String especie;

    @NotBlank(message = "Raça é obrigatória")
    private String raca;

    @NotNull(message = "Idade é obrigatória")
    @Min(value = 0)
    private Integer idade;

    @NotBlank(message = "Sexo é obrigatório")
    private String sexo;

    @NotNull(message = "Peso é obrigatório")
    @DecimalMin(value = "0.1")
    private Double peso;

    private String fotoUrl;

    @NotNull(message = "Proprietário é obrigatório")
    private Long proprietarioId;

    private String proprietarioNome;
}
