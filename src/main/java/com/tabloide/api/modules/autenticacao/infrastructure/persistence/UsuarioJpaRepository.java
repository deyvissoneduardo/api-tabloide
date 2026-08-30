package com.tabloide.api.modules.autenticacao.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, Long> {

    Optional<UsuarioJpaEntity> findByEmail(String email);

    Optional<UsuarioJpaEntity> findByEmailAndSupermercadoCnpj(String email, String supermercadoCnpj);
}
