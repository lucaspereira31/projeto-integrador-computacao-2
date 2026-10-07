package com.babapremium.dto;

public record PrevisaoSonoRequestDTO(
    Double idadeMeses,
    Double duracaoUltimaSonecaMin,
    Double volumeLeiteMl,
    Double nivelAgitacao
) {}
