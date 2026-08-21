package br.com.projeto.model.rotina;

import br.com.projeto.Enum.LadoAmamentacaoEnum;
import br.com.projeto.Enum.TipoAlimentacaoEnum;
import br.com.projeto.model.autenticacao.Bebe;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "registro_alimentacao")
public class RegistroAlimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bebe_id", nullable = false)
    private Bebe bebe;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAlimentacaoEnum tipo;

    private String alimento; //quando for do tipo SOLIDA, informar o alimento (ex.: papinha de fruta, arroz, feijão, etc.)

    private LocalDateTime dataHora;
    private String quantidade; // Ex.: 120 ml ou 15 minutos ou 1 porção

    @Enumerated(EnumType.STRING)
    private LadoAmamentacaoEnum lado;

    private String observacao;
}
