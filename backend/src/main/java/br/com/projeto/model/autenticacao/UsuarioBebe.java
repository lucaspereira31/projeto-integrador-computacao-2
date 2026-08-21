package br.com.projeto.model.autenticacao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios_bebes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioBebe {

    @EmbeddedId
    private UsuarioBebeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bebeId")
    @JoinColumn(name = "bebe_id")
    private Bebe bebe;

    @Column(nullable = false)
    private String papel;

    @Column(nullable = false)
    private LocalDateTime dataVinculo;
}
