package com.tabloide.api.modules.autenticacao.infrastructure.persistence;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, Long> {

    Optional<UsuarioJpaEntity> findByEmail(String email);

    Optional<UsuarioJpaEntity> findByEmailAndSupermercadoCnpj(String email, String supermercadoCnpj);

    Optional<UsuarioJpaEntity> findByIdAndSupermercadoId(Long id, Long supermercadoId);

    Page<UsuarioJpaEntity> findBySupermercadoId(Long supermercadoId, Pageable pageable);

    List<UsuarioJpaEntity> findBySupermercadoIdAndPerfilAndAtivoTrue(Long supermercadoId, Perfil perfil);
}
