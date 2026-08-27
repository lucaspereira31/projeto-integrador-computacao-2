package br.com.projeto.dto.response;

import br.com.projeto.Enum.TipoUsuarioEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private Long id;

    private String nome;

    private String email;

    private String telefone;

    private String fotoPerfil;

    private LocalDate dataCadastro;

    private TipoUsuarioEnum tipo;
}
