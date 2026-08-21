package br.com.projeto.repository;

import br.com.projeto.model.autenticacao.Bebe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BebeRepository extends JpaRepository<Bebe, Long> {

    List<Bebe> findByUsuarioId(Long usuarioId);
}
