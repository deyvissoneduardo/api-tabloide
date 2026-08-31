package com.tabloide.api.modules.plano.infrastructure.persistence;

import com.tabloide.api.modules.plano.domain.AvisoInterno;
import com.tabloide.api.modules.plano.domain.AvisoInternoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
public class AvisoInternoRepositoryAdapter implements AvisoInternoRepository {

    private final AvisoInternoJpaRepository jpaRepository;

    public AvisoInternoRepositoryAdapter(AvisoInternoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean salvarSeNaoExiste(AvisoInterno aviso) {
        if (jpaRepository.existsByAssinaturaIdAndTipo(aviso.assinaturaId(), aviso.tipo())) {
            return false;
        }

        try {
            jpaRepository.saveAndFlush(new AvisoInternoJpaEntity(
                    null,
                    aviso.assinaturaId(),
                    aviso.destinatarioUsuarioId(),
                    aviso.supermercadoId(),
                    aviso.tipo(),
                    aviso.mensagem(),
                    aviso.lidoEm(),
                    aviso.criadoEm()
            ));
            return true;
        } catch (DataIntegrityViolationException ex) {
            // outra execução concorrente já gravou o mesmo aviso (assinatura_id, tipo) — idempotência do job.
            return false;
        }
    }
}
