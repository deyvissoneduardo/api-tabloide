package com.tabloide.api.modules.autenticacao.infrastructure.persistence;

import com.tabloide.api.modules.autenticacao.domain.SessaoDetalhada;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SessaoJpaRepository extends JpaRepository<SessaoJpaEntity, Long> {

    Optional<SessaoJpaEntity> findByJti(String jti);

    @Modifying
    @Query("UPDATE SessaoJpaEntity s SET s.revogadaEm = :agora WHERE s.usuarioId = :usuarioId AND s.revogadaEm IS NULL")
    void revogarTodasDoUsuario(@Param("usuarioId") Long usuarioId, @Param("agora") Instant agora);

    @Query("""
            SELECT new com.tabloide.api.modules.autenticacao.domain.SessaoDetalhada(
                s.jti, u.email, u.perfil, u.supermercadoId, s.criadoEm, s.expiraEm, s.ultimoUsoEm, s.revogadaEm)
            FROM SessaoJpaEntity s JOIN UsuarioJpaEntity u ON u.id = s.usuarioId
            WHERE (:supermercadoId IS NULL OR u.supermercadoId = :supermercadoId)
            ORDER BY s.criadoEm DESC
            """)
    Page<SessaoDetalhada> listar(@Param("supermercadoId") Long supermercadoId, Pageable pageable);

    @Query("""
            SELECT new com.tabloide.api.modules.autenticacao.domain.SessaoDetalhada(
                s.jti, u.email, u.perfil, u.supermercadoId, s.criadoEm, s.expiraEm, s.ultimoUsoEm, s.revogadaEm)
            FROM SessaoJpaEntity s JOIN UsuarioJpaEntity u ON u.id = s.usuarioId
            WHERE s.jti = :jti
            """)
    Optional<SessaoDetalhada> buscarDetalhePorJti(@Param("jti") String jti);
}
