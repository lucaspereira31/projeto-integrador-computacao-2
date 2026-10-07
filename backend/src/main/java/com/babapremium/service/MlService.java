package com.babapremium.service;

import com.babapremium.dto.PrevisaoSonoRequestDTO;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class MlService {

    public String executarPredicaoSono(PrevisaoSonoRequestDTO dto) {
        try {
            // Executa o script predict.py passando os parâmetros
            ProcessBuilder pb = new ProcessBuilder(
                "python",
                "ml/predict.py",
                String.valueOf(dto.idadeMeses()),
                String.valueOf(dto.duracaoUltimaSonecaMin()),
                String.valueOf(dto.volumeLeiteMl()),
                String.valueOf(dto.nivelAgitacao())
            );

            pb.redirectErrorStream(true);
            Process processo = pb.start();

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(processo.getInputStream(), StandardCharsets.UTF_8)
            );

            StringBuilder output = new StringBuilder();
            String linha;
            while ((linha = reader.readLine()) != null) {
                output.append(linha);
            }

            processo.waitFor();
            return output.toString();

        } catch (Exception e) {
            return "{\"status\":\"erro\",\"mensagem\":\"" + e.getMessage() + "\"}";
        }
    }
}
