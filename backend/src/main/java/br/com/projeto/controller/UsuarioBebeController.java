package br.com.projeto.controller;

import br.com.projeto.dto.request.UsuarioBebeRequest;
import br.com.projeto.dto.response.UsuarioBebeResponse;
import br.com.projeto.mapper.UsuarioBebeMapper;
import br.com.projeto.model.autenticacao.UsuarioBebe;
import br.com.projeto.service.UsuarioBebeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UsuarioBebeController {

    private final UsuarioBebeService usuarioBebeService;
    private final UsuarioBebeMapper mapper;

    @PostMapping("/bebes/{bebeId}/usuarios")
    public ResponseEntity<UsuarioBebeResponse> vincularUsuarioAoBebe(
            @PathVariable Long bebeId,
            @RequestBody @Valid UsuarioBebeRequest request) {

        if (!bebeId.equals(request.getBebeId())) {
            return ResponseEntity.badRequest().build();
        }

        usuarioBebeService.vincularUsuarioAoBebe(request.getUsuarioId(), request.getBebeId(), request.getPapel());

        UsuarioBebe vinculo = usuarioBebeService.obterVinculo(request.getUsuarioId(), request.getBebeId());

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(vinculo));
    }

    @GetMapping("/bebes/{bebeId}/usuarios")
    public ResponseEntity<List<UsuarioBebeResponse>> listarUsuariosDoBebe(@PathVariable Long bebeId) {
        List<UsuarioBebe> usuarios = usuarioBebeService.listarUsuariosDoBebe(bebeId);
        List<UsuarioBebeResponse> resp = mapper.toResponseList(usuarios);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/bebes/{bebeId}/usuarios/{usuarioId}")
    public ResponseEntity<UsuarioBebeResponse> obterVinculo(
            @PathVariable Long bebeId,
            @PathVariable Long usuarioId) {

        UsuarioBebe vinculo = usuarioBebeService.obterVinculo(usuarioId, bebeId);
        return ResponseEntity.ok(mapper.toResponse(vinculo));
    }

    @DeleteMapping("/bebes/{bebeId}/usuarios/{usuarioId}")
    public ResponseEntity<Void> removerVinculo(
            @PathVariable Long bebeId,
            @PathVariable Long usuarioId) {

        usuarioBebeService.removerVinculo(usuarioId, bebeId);
        return ResponseEntity.noContent().build();
    }
}

