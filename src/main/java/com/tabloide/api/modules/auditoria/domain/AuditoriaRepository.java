package com.tabloide.api.modules.auditoria.domain;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import java.util.Optional;

public interface AuditoriaRepository {

    void registrar(RegistroAuditoria registro);

    Pagina<RegistroAuditoria> listar(Long supermercadoId, FiltroAuditoria filtro, int pagina, int tamanho);

    Optional<RegistroAuditoria> buscarPorId(Long id);
}
