package br.com.projeto.dto.request;

import br.com.projeto.Enum.TipoUsuarioEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequest {

    private Long id;

    @NotNull
    private String nome;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String senha;

    private String telefone;

    private String fotoPerfil;

    private TipoUsuarioEnum tipo;

    private LocalDate dataCadastro;
}
