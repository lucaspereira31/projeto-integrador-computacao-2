package br.com.projeto.controller;

import br.com.projeto.dto.request.UsuarioRequest;
import br.com.projeto.dto.response.UsuarioResponse;
import br.com.projeto.mapper.UsuarioMapper;
import br.com.projeto.model.autenticacao.Usuario;
import br.com.projeto.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper mapper;

    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrarUsuario(
            @RequestBody @Valid UsuarioRequest request) {

        Usuario usuario = mapper.toEntity(request);
        Usuario criado = usuarioService.cadastrarUsuario(usuario);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toResponse(criado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obterUsuario(@PathVariable Long id) {

        Usuario usuario = usuarioService.obterUsuario(id);

        return ResponseEntity.ok(mapper.toResponse(usuario));
    }

    @GetMapping("/email")
    public ResponseEntity<UsuarioResponse> buscarPorEmail(
            @RequestParam String email) {

        Usuario usuario = usuarioService.buscarUsuarioPorEmail(email);

        return ResponseEntity.ok(mapper.toResponse(usuario));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {

        List<Usuario> usuarios = usuarioService.listarTodos();

        List<UsuarioResponse> response = usuarios.stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {

        usuarioService.deletarUsuario(id);

        return ResponseEntity.noContent().build();
    }
}