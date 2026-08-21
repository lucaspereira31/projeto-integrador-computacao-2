package br.com.projeto.service;

import br.com.projeto.Enum.FaixaEtariaEnum;
import br.com.projeto.exception.BebeNaoEncontratoException;
import br.com.projeto.model.autenticacao.Bebe;
import br.com.projeto.repository.BebeRepository;
import br.com.projeto.repository.specification.BebeSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BebeService {

    private final BebeRepository bebeRepository;
    private final BebeSpecification bebeSpecification;

    //salvar novo bebê
    public void cadastrarBebe(Bebe bebe) {
        bebeRepository.save(bebe);
    }

    //obter informações de um bebê
    public Bebe obterInfoBebe(Long id) {
        return bebeRepository.findById(id)
                .orElseThrow(() -> new BebeNaoEncontratoException("Bebê não encontrado"));
    }

    //deletar bebe
    public void deletarBebe(Long id) {
        bebeRepository.findById(id)
                .orElseThrow(() -> new BebeNaoEncontratoException("Bebê não encontrado"));
        bebeRepository.deleteById(id);
    }

    //buscar bebes que sao responsabilidade da baba(buscando por id, nomeBebe, nomeResponsavel, idadeBebe)
    public Page<Bebe> buscarBebesDaBaba(Pageable pageable,
                                        String nomeBebe,
                                        String nomeResponsavel,
                                        FaixaEtariaEnum faixaEtaria,
                                        Long idBebe,
                                        Long idBaba) {
        if (idBaba == null) {
            throw new IllegalArgumentException("ID da babá é obrigatório.");
        }

        Specification<Bebe> spec = bebeSpecification.bebesDaBaba(idBaba);

        // Filtro por ID bebe
        if (idBebe != null) {
            spec = spec.and(bebeSpecification.procurarPorId(idBebe));
        }

        // Filtro por nome do bebê
        if (nomeBebe != null && !nomeBebe.isBlank()) {
            spec = spec.and(bebeSpecification.procurarBebePorNome(nomeBebe));
        }

        // Filtro por nome do responsável
        if (nomeResponsavel != null && !nomeResponsavel.isBlank()) {
            spec = spec.and(bebeSpecification.procurarBebePorNomeResponsavel(nomeResponsavel));
        }

        // Filtro por faixa etária
        if (faixaEtaria != null) {
            spec = spec.and(bebeSpecification.procurarPorFaixaEtaria(faixaEtaria));
        }
        return bebeRepository.findAll(spec, pageable);
    }
}