package com.tabloide.api.modules.oferta.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.oferta.domain.Oferta;
import com.tabloide.api.modules.oferta.domain.OfertaRepository;
import com.tabloide.api.modules.oferta.domain.exceptions.OfertaNaoEncontradaException;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DesativarOferta {

    private final OfertaRepository ofertaRepository;
    private final AuditoriaRepository auditoriaRepository;

    public DesativarOferta(OfertaRepository ofertaRepository, AuditoriaRepository auditoriaRepository) {
        this.ofertaRepository = ofertaRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Oferta executar(Long supermercadoId, Long ofertaId, Long atorId, Perfil perfilAtor, Long supermercadoIdAtor) {
        if (foraDoEscopoDoAtor(supermercadoId, supermercadoIdAtor)) {
            throw new OfertaNaoEncontradaException();
        }

        Oferta oferta = ofertaRepository.buscarPorIdESupermercado(ofertaId, supermercadoId)
                .orElseThrow(OfertaNaoEncontradaException::new);

        if (oferta.estaDesativada()) {
            return oferta;
        }

        String antes = oferta.resumoParaAuditoria();
        Instant agora = Instant.now();
        oferta.desativar(agora);
        Oferta salva = ofertaRepository.salvar(oferta);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, supermercadoId, "OFERTA_DESATIVADA", "Oferta", salva.id(),
                antes, salva.resumoParaAuditoria(), agora
        ));

        return salva;
    }

    private boolean foraDoEscopoDoAtor(Long supermercadoId, Long supermercadoIdAtor) {
        return !Objects.equals(supermercadoId, supermercadoIdAtor);
    }
}
