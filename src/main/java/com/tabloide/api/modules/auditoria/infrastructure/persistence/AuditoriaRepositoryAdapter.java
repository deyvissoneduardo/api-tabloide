package com.tabloide.api.modules.auditoria.infrastructure.persistence;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import org.springframework.stereotype.Repository;

@Repository
public class AuditoriaRepositoryAdapter implements AuditoriaRepository {

    private final RegistroAuditoriaJpaRepository jpaRepository;

    public AuditoriaRepositoryAdapter(RegistroAuditoriaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void registrar(RegistroAuditoria registro) {
        jpaRepository.save(new RegistroAuditoriaJpaEntity(
                registro.atorId(),
                registro.perfilAtor(),
                registro.acao(),
                registro.entidade(),
                registro.entidadeId(),
                registro.dadosAntes(),
                registro.dadosDepois(),
                registro.instante()
        ));
    }
}
