package com.tabloide.api.modules.oferta.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.oferta.domain.Oferta;
import com.tabloide.api.modules.oferta.domain.OfertaRepository;
import com.tabloide.api.modules.oferta.domain.exceptions.OfertaNaoEncontradaException;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CopiarOferta {

    private final OfertaRepository ofertaRepository;
    private final SupermercadoRepository supermercadoRepository;
    private final AuditoriaRepository auditoriaRepository;

    public CopiarOferta(
            OfertaRepository ofertaRepository,
            SupermercadoRepository supermercadoRepository,
            AuditoriaRepository auditoriaRepository
    ) {
        this.ofertaRepository = ofertaRepository;
        this.supermercadoRepository = supermercadoRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Oferta executar(Long supermercadoId, Long ofertaId, Long atorId, Perfil perfilAtor, Long supermercadoIdAtor) {
        if (foraDoEscopoDoAtor(supermercadoId, supermercadoIdAtor)) {
            throw new OfertaNaoEncontradaException();
        }

        Supermercado supermercado = supermercadoRepository.buscarPorId(supermercadoId)
                .orElseThrow(SupermercadoNaoEncontradoException::new);
        supermercado.validarPermissaoParaMutacao();

        Oferta original = ofertaRepository.buscarPorIdESupermercado(ofertaId, supermercadoId)
                .orElseThrow(OfertaNaoEncontradaException::new);

        Instant agora = Instant.now();
        Oferta copia = Oferta.cadastrar(
                supermercadoId, original.produtoId(), original.lojaIds(), original.precoNormal(), original.precoPromocional(),
                original.inicio(), original.fim(), original.condicoes(), false, agora
        );
        Oferta salva = ofertaRepository.salvar(copia);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, supermercadoId, "OFERTA_COPIADA", "Oferta", salva.id(),
                null, salva.resumoParaAuditoria(), agora
        ));

        return salva;
    }

    private boolean foraDoEscopoDoAtor(Long supermercadoId, Long supermercadoIdAtor) {
        return !Objects.equals(supermercadoId, supermercadoIdAtor);
    }
}
