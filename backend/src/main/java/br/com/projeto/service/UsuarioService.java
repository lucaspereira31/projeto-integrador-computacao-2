package br.com.projeto.service;

import br.com.projeto.exception.UsuarioNaoEncontradoException;
import br.com.projeto.model.autenticacao.Usuario;
import br.com.projeto.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    //cadastrarUsuario()
    public Usuario cadastrarUsuario(Usuario usuario){
        usuarioRepository.save(usuario);
        return usuario;
    }

    //deletarUsuario()
    public void deletarUsuario(Long id){
        usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));
        usuarioRepository.deleteById(id);
    }

    //obterUsuario()
    public Usuario obterUsuario(Long id){
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));
    }

    //buscarUsuarioPorEmail()
    public Usuario buscarUsuarioPorEmail(String email){
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }


}
