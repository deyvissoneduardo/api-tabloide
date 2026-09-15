package com.tabloide.api.modules.tabloide.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.loja.domain.LojaRepository;
import com.tabloide.api.modules.loja.domain.exceptions.LojaNaoEncontradaException;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import com.tabloide.api.modules.tabloide.domain.Tabloide;
import com.tabloide.api.modules.tabloide.domain.TabloideRepository;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// Cobre US-109 (disponibilizar tabloide já existente) e US-110 (informar a validade do
// tabloide): a doc de origem descreve as duas com o mesmo contrato porque a validade é um
// campo do cadastro, não uma operação separada.
@Component
public class DisponibilizarTabloide {

    private final TabloideRepository tabloideRepository;
    private final LojaRepository lojaRepository;
    private final SupermercadoRepository supermercadoRepository;
    private final AuditoriaRepository auditoriaRepository;

    public DisponibilizarTabloide(
            TabloideRepository tabloideRepository,
            LojaRepository lojaRepository,
            SupermercadoRepository supermercadoRepository,
            AuditoriaRepository auditoriaRepository
    ) {
        this.tabloideRepository = tabloideRepository;
        this.lojaRepository = lojaRepository;
        this.supermercadoRepository = supermercadoRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Tabloide executar(Long supermercadoId, DadosTabloide dados, Long atorId, Perfil perfilAtor, Long supermercadoIdAtor) {
        if (foraDoEscopoDoAtor(supermercadoId, supermercadoIdAtor)) {
            throw new SupermercadoNaoEncontradoException();
        }

        Supermercado supermercado = supermercadoRepository.buscarPorId(supermercadoId)
                .orElseThrow(SupermercadoNaoEncontradoException::new);
        supermercado.validarPermissaoParaMutacao();

        validarLojas(dados.lojaIds(), supermercadoId);

        Instant agora = Instant.now();
        Tabloide tabloide = Tabloide.disponibilizar(
                supermercadoId, dados.titulo(), dados.tipoArquivo(), dados.arquivoPdfUrl(), dados.arquivoPdfTamanhoBytes(),
                dados.lojaIds(), dados.inicio(), dados.fim(), agora
        );
        Tabloide salvo = tabloideRepository.salvar(tabloide);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, supermercadoId, "TABLOIDE_DISPONIBILIZADO", "Tabloide", salvo.id(),
                null, salvo.resumoParaAuditoria(), agora
        ));

        return salvo;
    }

    private void validarLojas(Set<Long> lojaIds, Long supermercadoId) {
        for (Long lojaId : lojaIds) {
            lojaRepository.buscarPorIdESupermercado(lojaId, supermercadoId)
                    .orElseThrow(LojaNaoEncontradaException::new);
        }
    }

    private boolean foraDoEscopoDoAtor(Long supermercadoId, Long supermercadoIdAtor) {
        return !Objects.equals(supermercadoId, supermercadoIdAtor);
    }
}
