package br.com.projeto.repository;

import br.com.projeto.model.rotina.RegistroAlimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroAlimentacaoRepository extends JpaRepository<RegistroAlimentacao, Long> {
}
