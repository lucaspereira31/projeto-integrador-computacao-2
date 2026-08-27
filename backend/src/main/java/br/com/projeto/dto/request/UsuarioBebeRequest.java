package br.com.projeto.dto.request;

import br.com.projeto.Enum.TipoUsuarioEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioBebeRequest {

    @NotNull
    private Long usuarioId;

    @NotNull
    private Long bebeId;

    @NotNull
    private TipoUsuarioEnum papel;
}
