package br.com.projeto.repository;

import br.com.projeto.model.autenticacao.UsuarioBebe;
import br.com.projeto.model.autenticacao.UsuarioBebeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioBebeRepository extends JpaRepository<UsuarioBebe, UsuarioBebeId> {

    List<UsuarioBebe> findByBebeId(Long bebeId);

    boolean existsByUsuarioIdAndBebeId(Long usuarioId, Long bebeId);
}
