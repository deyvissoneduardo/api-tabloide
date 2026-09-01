package com.tabloide.api.modules.loja.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.loja.domain.Loja;
import com.tabloide.api.modules.loja.domain.LojaRepository;
import com.tabloide.api.modules.loja.domain.exceptions.LimiteDeLojasDoPlanoAtingidoException;
import com.tabloide.api.modules.loja.domain.exceptions.NomeDeLojaJaCadastradoException;
import com.tabloide.api.modules.plano.domain.Assinatura;
import com.tabloide.api.modules.plano.domain.AssinaturaRepository;
import com.tabloide.api.modules.plano.domain.Plano;
import com.tabloide.api.modules.plano.domain.PlanoRepository;
import com.tabloide.api.modules.plano.domain.exceptions.AssinaturaNaoEncontradaException;
import com.tabloide.api.modules.plano.domain.exceptions.PlanoNaoEncontradoException;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoBloqueadoOuDesativadoException;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CadastrarLoja {

    private final LojaRepository lojaRepository;
    private final SupermercadoRepository supermercadoRepository;
    private final AssinaturaRepository assinaturaRepository;
    private final PlanoRepository planoRepository;
    private final AuditoriaRepository auditoriaRepository;

    public CadastrarLoja(
            LojaRepository lojaRepository,
            SupermercadoRepository supermercadoRepository,
            AssinaturaRepository assinaturaRepository,
            PlanoRepository planoRepository,
            AuditoriaRepository auditoriaRepository
    ) {
        this.lojaRepository = lojaRepository;
        this.supermercadoRepository = supermercadoRepository;
        this.assinaturaRepository = assinaturaRepository;
        this.planoRepository = planoRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Loja executar(Long supermercadoId, DadosLoja dados, Long atorId, Perfil perfilAtor, Long supermercadoIdAtor) {
        if (foraDoEscopoDoAtor(supermercadoId, supermercadoIdAtor)) {
            throw new SupermercadoNaoEncontradoException();
        }

        // Lock pessimista na linha do supermercado: serializa cadastros concorrentes de loja no
        // mesmo supermercado para que a checagem de limite do plano (contagem + inserção) seja segura.
        Supermercado supermercado = supermercadoRepository.buscarPorIdComLock(supermercadoId)
                .orElseThrow(SupermercadoNaoEncontradoException::new);
        if (!supermercado.estaAtivo()) {
            throw new SupermercadoBloqueadoOuDesativadoException();
        }

        String nomeNormalizado = Loja.normalizarNome(dados.nome());
        if (lojaRepository.existeNomeNormalizadoNoSupermercado(nomeNormalizado, supermercadoId)) {
            throw new NomeDeLojaJaCadastradoException();
        }

        validarLimiteDoPlano(supermercadoId);

        Instant agora = Instant.now();
        Loja loja = Loja.cadastrar(supermercadoId, dados.nome(), dados.endereco(), dados.complemento(), agora);
        Loja salva = lojaRepository.salvar(loja);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, supermercadoId, "LOJA_CADASTRADA", "Loja", salva.id(),
                null, salva.resumoParaAuditoria(), agora
        ));

        return salva;
    }

    private void validarLimiteDoPlano(Long supermercadoId) {
        Assinatura assinatura = assinaturaRepository.buscarVigenteOuAgendadaPorSupermercado(supermercadoId)
                .orElseThrow(AssinaturaNaoEncontradaException::new);
        Plano plano = planoRepository.buscarPorId(assinatura.planoId())
                .orElseThrow(PlanoNaoEncontradoException::new);

        Integer limiteLojas = plano.limiteLojas();
        if (limiteLojas == null) {
            return;
        }

        long ativas = lojaRepository.contarAtivasPorSupermercado(supermercadoId);
        if (ativas >= limiteLojas) {
            throw new LimiteDeLojasDoPlanoAtingidoException();
        }
    }

    private boolean foraDoEscopoDoAtor(Long supermercadoId, Long supermercadoIdAtor) {
        return !Objects.equals(supermercadoId, supermercadoIdAtor);
    }
}
