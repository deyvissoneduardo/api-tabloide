package com.tabloide.api.modules.autenticacao.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RedefinicaoSenhaJpaRepository extends JpaRepository<RedefinicaoSenhaJpaEntity, Long> {

    Optional<RedefinicaoSenhaJpaEntity> findByTokenHash(String tokenHash);
}
