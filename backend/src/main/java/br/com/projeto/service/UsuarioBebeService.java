package br.com.projeto.service;

import br.com.projeto.Enum.TipoUsuarioEnum;
import br.com.projeto.exception.BebeNaoEncontratoException;
import br.com.projeto.exception.UsuarioNaoEncontradoException;
import br.com.projeto.exception.VinculoJaExisteException;
import br.com.projeto.exception.VinculoNaoEncontrato;
import br.com.projeto.model.autenticacao.Bebe;
import br.com.projeto.model.autenticacao.Usuario;
import br.com.projeto.model.autenticacao.UsuarioBebe;
import br.com.projeto.model.autenticacao.UsuarioBebeId;
import br.com.projeto.repository.BebeRepository;
import br.com.projeto.repository.UsuarioBebeRepository;
import br.com.projeto.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioBebeService {

    private final UsuarioBebeRepository usuarioBebeRepository;
    private final BebeRepository bebeRepository;
    private final UsuarioRepository usuarioRepository;

    // Vincula um usuário a um bebê com um papel específico
    public void vincularUsuarioAoBebe(Long usuarioId,
                                      Long bebeId,
                                      TipoUsuarioEnum papel) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new UsuarioNaoEncontradoException("Usuário não encontrado"));

        Bebe bebe = bebeRepository.findById(bebeId)
                .orElseThrow(() ->
                        new BebeNaoEncontratoException("Bebê não encontrado"));

        if (usuarioBebeRepository.existsByUsuarioIdAndBebeId(usuarioId, bebeId)) {
            throw new VinculoNaoEncontrato("Usuário já está vinculado a este bebê");
        }

        UsuarioBebe vinculo = new UsuarioBebe();

        vinculo.setId(new UsuarioBebeId(usuarioId, bebeId));
        vinculo.setUsuario(usuario);
        vinculo.setBebe(bebe);
        vinculo.setPapel(papel);
        vinculo.setDataVinculo(LocalDateTime.now());

        usuarioBebeRepository.save(vinculo);
    }

    // Lista todos os usuários vinculados a um bebê
    public List<UsuarioBebe> listarUsuariosDoBebe(Long bebeId) {

        bebeRepository.findById(bebeId)
                .orElseThrow(() ->
                        new BebeNaoEncontratoException("Bebê não encontrado"));

        return usuarioBebeRepository.findByBebeId(bebeId);
    }

    // Busca um vínculo específico
    public UsuarioBebe obterVinculo(Long usuarioId, Long bebeId) {

        UsuarioBebeId id = new UsuarioBebeId(usuarioId, bebeId);

        return usuarioBebeRepository.findById(id)
                .orElseThrow(() ->
                        new VinculoJaExisteException("Vínculo não encontrado"));
    }

    // Remove o vínculo entre usuário e bebê
    public void removerVinculo(Long usuarioId, Long bebeId) {

        UsuarioBebeId id = new UsuarioBebeId(usuarioId, bebeId);

        UsuarioBebe vinculo = usuarioBebeRepository.findById(id)
                .orElseThrow(() ->
                        new VinculoJaExisteException("Vínculo não encontrado"));

        usuarioBebeRepository.delete(vinculo);
    }
}