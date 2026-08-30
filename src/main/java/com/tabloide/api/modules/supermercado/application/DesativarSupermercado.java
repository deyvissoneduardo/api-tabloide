package com.tabloide.api.modules.supermercado.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DesativarSupermercado {

    private final SupermercadoRepository supermercadoRepository;
    private final AuditoriaRepository auditoriaRepository;

    public DesativarSupermercado(SupermercadoRepository supermercadoRepository, AuditoriaRepository auditoriaRepository) {
        this.supermercadoRepository = supermercadoRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Supermercado executar(Long id, Long atorId, Perfil perfilAtor) {
        Supermercado supermercado = supermercadoRepository.buscarPorId(id)
                .orElseThrow(SupermercadoNaoEncontradoException::new);

        if (supermercado.estaDesativado()) {
            return supermercado;
        }

        String antes = supermercado.resumoParaAuditoria();
        Instant agora = Instant.now();
        supermercado.desativar(agora);
        Supermercado salvo = supermercadoRepository.salvar(supermercado);

        auditoriaRepository.registrar(new RegistroAuditoria(
                atorId, perfilAtor, "SUPERMERCADO_DESATIVADO", "Supermercado", salvo.id(),
                antes, salvo.resumoParaAuditoria(), agora
        ));

        return salvo;
    }
}
