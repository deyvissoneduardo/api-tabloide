package com.tabloide.api.modules.auditoria.infrastructure;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.infrastructure.security.RegistradorDeAuditoriaDeLeitura;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class RegistradorDeAuditoriaDeLeituraAdapter implements RegistradorDeAuditoriaDeLeitura {

    private final AuditoriaRepository auditoriaRepository;

    public RegistradorDeAuditoriaDeLeituraAdapter(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @Override
    public void registrarConsulta(Long atorId, Perfil perfilAtor, Long supermercadoId, String acao, String entidade, Long entidadeId) {
        auditoriaRepository.registrar(
                RegistroAuditoria.deConsulta(atorId, perfilAtor, supermercadoId, acao, entidade, entidadeId, Instant.now()));
    }
}
