package br.com.projeto.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BebeRequest(@NotBlank String nome,
                          @NotNull LocalDate dataNascimento,
                          @NotBlank String sexo,
                          @NotBlank String fotoPerfil,
                          @NotBlank String observacoes) {

}
