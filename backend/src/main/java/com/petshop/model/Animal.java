package com.petshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "animais")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Espécie é obrigatória")
    @Column(nullable = false)
    private String especie;

    @NotBlank(message = "Raça é obrigatória")
    @Column(nullable = false)
    private String raca;

    @NotNull(message = "Idade é obrigatória")
    @Min(value = 0, message = "Idade deve ser positiva")
    @Column(nullable = false)
    private Integer idade;

    @NotBlank(message = "Sexo é obrigatório")
    @Column(nullable = false)
    private String sexo;

    @NotNull(message = "Peso é obrigatório")
    @DecimalMin(value = "0.1", message = "Peso deve ser maior que zero")
    @Column(nullable = false)
    private Double peso;

    @Column
    private String fotoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proprietario_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Proprietario proprietario;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<LancamentoServico> lancamentos = new ArrayList<>();
}
