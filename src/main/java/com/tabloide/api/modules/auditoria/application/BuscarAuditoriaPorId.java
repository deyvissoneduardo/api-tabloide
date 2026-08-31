package com.tabloide.api.modules.auditoria.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.auditoria.domain.exceptions.RegistroAuditoriaNaoEncontradoException;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class BuscarAuditoriaPorId {

    private final AuditoriaRepository auditoriaRepository;

    public BuscarAuditoriaPorId(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    public RegistroAuditoria executar(Long id, Perfil perfilSolicitante, Long supermercadoIdSolicitante) {
        RegistroAuditoria registro = auditoriaRepository.buscarPorId(id)
                .orElseThrow(RegistroAuditoriaNaoEncontradoException::new);

        if (foraDoEscopo(registro, perfilSolicitante, supermercadoIdSolicitante)) {
            throw new RegistroAuditoriaNaoEncontradoException();
        }

        return registro;
    }

    private boolean foraDoEscopo(RegistroAuditoria registro, Perfil perfilSolicitante, Long supermercadoIdSolicitante) {
        if (perfilSolicitante == Perfil.SUPER_ADMIN) {
            return false;
        }
        return !Objects.equals(registro.supermercadoId(), supermercadoIdSolicitante);
    }
}
