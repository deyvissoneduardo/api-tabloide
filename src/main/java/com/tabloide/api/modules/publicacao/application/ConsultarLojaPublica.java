package com.tabloide.api.modules.publicacao.application;

import com.tabloide.api.modules.analytics.application.RegistrarEventoAcesso;
import com.tabloide.api.modules.analytics.domain.TipoEventoAcesso;
import com.tabloide.api.modules.loja.domain.Loja;
import com.tabloide.api.modules.publicacao.domain.exceptions.RecursoPublicoIndisponivelException;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import org.springframework.stereotype.Component;

// US-171/US-179: consulta pública, sem login e sem dados pessoais, contabilizando o acesso à página (US-106/US-010/US-011).
@Component
public class ConsultarLojaPublica {

    private final ResolverLojaPublica resolverLojaPublica;
    private final SupermercadoRepository supermercadoRepository;
    private final RegistrarEventoAcesso registrarEventoAcesso;

    public ConsultarLojaPublica(
            ResolverLojaPublica resolverLojaPublica, SupermercadoRepository supermercadoRepository, RegistrarEventoAcesso registrarEventoAcesso
    ) {
        this.resolverLojaPublica = resolverLojaPublica;
        this.supermercadoRepository = supermercadoRepository;
        this.registrarEventoAcesso = registrarEventoAcesso;
    }

    public LojaPublica executar(Long lojaId) {
        Loja loja = resolverLojaPublica.executar(lojaId);
        Supermercado supermercado = supermercadoRepository.buscarPorId(loja.supermercadoId())
                .orElseThrow(RecursoPublicoIndisponivelException::new);

        registrarEventoAcesso.executar(TipoEventoAcesso.ACESSO_PAGINA, supermercado.id(), loja.id(), null, null);

        return new LojaPublica(supermercado.id(), supermercado.nomeFantasia(), loja.id(), loja.nome());
    }
}
