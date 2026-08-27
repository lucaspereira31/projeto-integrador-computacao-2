package br.com.projeto.dto.response;

import java.time.LocalDate;

public record BebeResponse(String nome,
                           LocalDate dataNascimento,
                           String sexo,
                           String fotoPerfil,
                           String observacoes) {
}
