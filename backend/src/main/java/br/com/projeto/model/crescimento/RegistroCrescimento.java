package br.com.projeto.model.crescimento;

import br.com.projeto.model.autenticacao.Bebe;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "registros_crescimento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistroCrescimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bebe_id", nullable = false)
    private Bebe bebe;

    @Column(nullable = false)
    private LocalDate dataRegistro;

    @Column(precision = 5, scale = 2)
    private BigDecimal pesoKg;

    @Column(precision = 5, scale = 2)
    private BigDecimal alturaCm;

    @Column(precision = 5, scale = 2)
    private BigDecimal perimetroCefalicoCm;

    private String observacoes;
}
