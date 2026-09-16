package com.tabloide.api.modules.publicacao.application;

import com.tabloide.api.modules.analytics.application.RegistrarEventoAcesso;
import com.tabloide.api.modules.analytics.domain.TipoEventoAcesso;
import com.tabloide.api.modules.loja.domain.Loja;
import com.tabloide.api.modules.oferta.domain.Oferta;
import com.tabloide.api.modules.oferta.domain.OfertaRepository;
import com.tabloide.api.modules.publicacao.domain.exceptions.RecursoPublicoIndisponivelException;
import org.springframework.stereotype.Component;

// RN-010 (Página pública): detalhe da oferta com URL compartilhável, sem exigir login.
@Component
public class BuscarOfertaPublicaPorId {

    private final ResolverLojaPublica resolverLojaPublica;
    private final OfertaRepository ofertaRepository;
    private final ProjetorDeOfertaPublica projetorDeOfertaPublica;
    private final RegistrarEventoAcesso registrarEventoAcesso;

    public BuscarOfertaPublicaPorId(
            ResolverLojaPublica resolverLojaPublica,
            OfertaRepository ofertaRepository,
            ProjetorDeOfertaPublica projetorDeOfertaPublica,
            RegistrarEventoAcesso registrarEventoAcesso
    ) {
        this.resolverLojaPublica = resolverLojaPublica;
        this.ofertaRepository = ofertaRepository;
        this.projetorDeOfertaPublica = projetorDeOfertaPublica;
        this.registrarEventoAcesso = registrarEventoAcesso;
    }

    public OfertaPublica executar(Long lojaId, Long ofertaId) {
        Loja loja = resolverLojaPublica.executar(lojaId);
        Oferta oferta = ofertaRepository.buscarPorIdESupermercado(ofertaId, loja.supermercadoId())
                .filter(candidata -> candidata.lojaIds().contains(lojaId))
                .orElseThrow(RecursoPublicoIndisponivelException::new);
        OfertaPublica ofertaPublica = projetorDeOfertaPublica.projetar(oferta, loja.supermercadoId())
                .orElseThrow(RecursoPublicoIndisponivelException::new);

        registrarEventoAcesso.executar(TipoEventoAcesso.VISUALIZACAO_OFERTA, loja.supermercadoId(), loja.id(), oferta.id(), null);

        return ofertaPublica;
    }
}
