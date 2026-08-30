package com.tabloide.api.modules.supermercado.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SupermercadoJpaRepository extends JpaRepository<SupermercadoJpaEntity, Long> {

    boolean existsByCnpj(String cnpj);
}
