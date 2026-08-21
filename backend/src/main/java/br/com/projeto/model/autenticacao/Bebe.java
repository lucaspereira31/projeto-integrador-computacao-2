package br.com.projeto.model.autenticacao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "bebes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bebe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    private String sexo;
    private String fotoPerfil;
    private String observacoes;

    @OneToMany(mappedBy = "bebe")
    private List<UsuarioBebe> usuariosBebes;
}
