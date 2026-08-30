package com.tabloide.api.modules.autenticacao.infrastructure.persistence;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SessaoJpaRepository extends JpaRepository<SessaoJpaEntity, Long> {

    Optional<SessaoJpaEntity> findByJti(String jti);

    @Modifying
    @Query("UPDATE SessaoJpaEntity s SET s.revogadaEm = :agora WHERE s.usuarioId = :usuarioId AND s.revogadaEm IS NULL")
    void revogarTodasDoUsuario(@Param("usuarioId") Long usuarioId, @Param("agora") Instant agora);
}
