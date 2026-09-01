package com.tabloide.api.modules.supermercado.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SupermercadoJpaRepository extends JpaRepository<SupermercadoJpaEntity, Long> {

    boolean existsByCnpj(String cnpj);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SupermercadoJpaEntity s where s.id = :id")
    Optional<SupermercadoJpaEntity> buscarPorIdComLock(@Param("id") Long id);
}
