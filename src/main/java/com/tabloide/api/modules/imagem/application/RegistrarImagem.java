package com.tabloide.api.modules.imagem.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.imagem.domain.Imagem;
import com.tabloide.api.modules.imagem.domain.ImagemRepository;
import com.tabloide.api.modules.imagem.domain.exceptions.CotaDeImagensExcedidaException;
import com.tabloide.api.modules.plano.domain.Assinatura;
import com.tabloide.api.modules.plano.domain.AssinaturaRepository;
import com.tabloide.api.modules.plano.domain.exceptions.AssinaturaNaoEncontradaException;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoBloqueadoOuDesativadoException;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RegistrarImagem {

    private final ImagemRepository imagemRepository;
    private final SupermercadoRepository supermercadoRepository;
    private final AssinaturaRepository assinaturaRepository;
    private final AuditoriaRepository auditoriaRepository;

    public RegistrarImagem(
            ImagemRepository imagemRepository,
            SupermercadoRepository supermercadoRepository,
            AssinaturaRepository assinaturaRepository,
            AuditoriaRepository auditoriaRepository
    ) {
        this.imagemRepository = imagemRepository;
        this.supermercadoRepository = supermercadoRepository;
        this.assinaturaRepository = assinaturaRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Imagem executar(Long supermercadoId, DadosImagem dados, Long atorId, Perfil perfilAtor) {
        // Lock pessimista na linha do supermercado: serializa registros concorrentes de imagem
        // para que a checagem de cota (contagem + inserção) seja segura.
        var supermercado = supermercadoRepository.buscarPorIdComLock(supermercadoId)
                .orElseThrow(SupermercadoNaoEncontradoException::new);
        if (!supermercado.estaAtivo()) {
            throw new SupermercadoBloqueadoOuDesativadoException();
        }

        Assinatura assinatura = assinaturaRepository.buscarVigenteOuAgendadaPorSupermercado(supermercadoId)
                .orElseThrow(AssinaturaNaoEncontradaException::new);

        Integer limiteFotos = assinatura.planoLimiteFotos();
        if (limiteFotos != null) {
            long ativas = imagemRepository.contarAtivasPorSupermercado(supermercadoId);
            if (ativas >= limiteFotos) {
                throw new CotaDeImagensExcedidaException();
            }
        }

        Instant agora = Instant.now();
        Imagem imagem = Imagem.registrar(
                supermercadoId, dados.nomeBusca(), dados.tipoVinculo(), dados.vinculoId(),
                dados.urlOuChave(), dados.formato(), dados.tamanhoBytes(), agora
        );
        Imagem salva = imagemRepository.salvar(imagem);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, supermercadoId, "IMAGEM_REGISTRADA", "Imagem", salva.id(),
                null, salva.resumoParaAuditoria(), agora
        ));

        return salva;
    }
}
