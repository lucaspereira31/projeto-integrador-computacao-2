package br.com.projeto.controller;

import br.com.projeto.Enum.FaixaEtariaEnum;
import br.com.projeto.dto.request.BebeRequest;
import br.com.projeto.dto.response.BebeResponse;
import br.com.projeto.mapper.BebeMapper;
import br.com.projeto.model.autenticacao.Bebe;
import br.com.projeto.service.BebeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bebes")
@RequiredArgsConstructor
public class BebeController {

    private final BebeService bebeService;

    private final BebeMapper bebeMapper;

    @PostMapping
    public ResponseEntity<BebeResponse> cadastrarBebe(@RequestBody @Valid BebeRequest bebe) {
        final var bebeSalvo = bebeMapper.map(bebe);

        bebeService.cadastrarBebe(bebeSalvo);

        final var response = bebeMapper.map(bebeSalvo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BebeResponse> obterBebe(@PathVariable Long id) {
        final var bebe = bebeService.obterInfoBebe(id);
        final var response = bebeMapper.map(bebe);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarBebe(@PathVariable Long id) {
        bebeService.deletarBebe(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/baba/{idBaba}")
    public ResponseEntity<Page<BebeResponse>> buscarBebesDaBaba(
            @PathVariable Long idBaba,
            @RequestParam(required = false) String nomeBebe,
            @RequestParam(required = false) String nomeResponsavel,
            @RequestParam(required = false) FaixaEtariaEnum faixaEtaria,
            @RequestParam(required = false) Long idBebe,
            Pageable pageable) {

        Page<Bebe> bebes = bebeService.buscarBebesDaBaba(
                pageable,
                nomeBebe,
                nomeResponsavel,
                faixaEtaria,
                idBebe,
                idBaba
        );

        Page<BebeResponse> responsePage = bebes.map(bebeMapper::map);
        return ResponseEntity.ok(responsePage);
    }
}
