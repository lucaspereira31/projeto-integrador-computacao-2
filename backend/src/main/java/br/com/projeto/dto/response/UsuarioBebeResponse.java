package br.com.projeto.dto.response;

import br.com.projeto.Enum.TipoUsuarioEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioBebeResponse {

    private Long usuarioId;

    private Long bebeId;

    private TipoUsuarioEnum papel;

    private LocalDateTime dataVinculo;
}
