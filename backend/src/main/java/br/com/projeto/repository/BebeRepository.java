package br.com.projeto.repository;

import br.com.projeto.model.autenticacao.Bebe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BebeRepository extends JpaRepository<Bebe, Long>, JpaSpecificationExecutor<Bebe> {
}
