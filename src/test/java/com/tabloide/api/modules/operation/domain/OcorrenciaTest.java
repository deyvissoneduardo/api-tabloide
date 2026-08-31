package com.tabloide.api.modules.operation.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tabloide.api.modules.operation.domain.exceptions.TransicaoEstadoOcorrenciaInvalidaException;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class OcorrenciaTest {

    private static Ocorrencia abrirOcorrencia() {
        return Ocorrencia.abrir("Título", "Descrição", SeveridadeOcorrencia.ALTA, 1L, 2L, Instant.now());
    }

    @Test
    void deveIniciarComEstadoAberta() {
        assertThat(abrirOcorrencia().estado()).isEqualTo(EstadoOcorrencia.ABERTA);
    }

    @Test
    void deveTransicionarDeAbertaParaEmAnalise() {
        Ocorrencia ocorrencia = abrirOcorrencia();

        ocorrencia.transicionarPara(EstadoOcorrencia.EM_ANALISE, Instant.now());

        assertThat(ocorrencia.estado()).isEqualTo(EstadoOcorrencia.EM_ANALISE);
    }

    @Test
    void deveTransicionarDeEmAnaliseParaResolvida() {
        Ocorrencia ocorrencia = abrirOcorrencia();
        ocorrencia.transicionarPara(EstadoOcorrencia.EM_ANALISE, Instant.now());

        ocorrencia.transicionarPara(EstadoOcorrencia.RESOLVIDA, Instant.now());

        assertThat(ocorrencia.estado()).isEqualTo(EstadoOcorrencia.RESOLVIDA);
        assertThat(ocorrencia.resolvidaEm()).isNotNull();
    }

    @Test
    void deveTransicionarDeResolvidaParaEncerrada() {
        Ocorrencia ocorrencia = abrirOcorrencia();
        ocorrencia.transicionarPara(EstadoOcorrencia.EM_ANALISE, Instant.now());
        ocorrencia.transicionarPara(EstadoOcorrencia.RESOLVIDA, Instant.now());

        ocorrencia.transicionarPara(EstadoOcorrencia.ENCERRADA, Instant.now());

        assertThat(ocorrencia.estado()).isEqualTo(EstadoOcorrencia.ENCERRADA);
        assertThat(ocorrencia.encerradaEm()).isNotNull();
    }

    @Test
    void naoDevePularEstadoDeAbertaParaResolvida() {
        Ocorrencia ocorrencia = abrirOcorrencia();

        assertThatThrownBy(() -> ocorrencia.transicionarPara(EstadoOcorrencia.RESOLVIDA, Instant.now()))
                .isInstanceOf(TransicaoEstadoOcorrenciaInvalidaException.class);
    }

    @Test
    void naoDevePularEstadoDeAbertaParaEncerrada() {
        Ocorrencia ocorrencia = abrirOcorrencia();

        assertThatThrownBy(() -> ocorrencia.transicionarPara(EstadoOcorrencia.ENCERRADA, Instant.now()))
                .isInstanceOf(TransicaoEstadoOcorrenciaInvalidaException.class);
    }

    @Test
    void naoDeveVoltarParaAberta() {
        Ocorrencia ocorrencia = abrirOcorrencia();

        assertThatThrownBy(() -> ocorrencia.transicionarPara(EstadoOcorrencia.ABERTA, Instant.now()))
                .isInstanceOf(TransicaoEstadoOcorrenciaInvalidaException.class);
    }

    @Test
    void naoDeveTransicionarNovamenteAposEncerrada() {
        Ocorrencia ocorrencia = abrirOcorrencia();
        ocorrencia.transicionarPara(EstadoOcorrencia.EM_ANALISE, Instant.now());
        ocorrencia.transicionarPara(EstadoOcorrencia.RESOLVIDA, Instant.now());
        ocorrencia.transicionarPara(EstadoOcorrencia.ENCERRADA, Instant.now());

        assertThatThrownBy(() -> ocorrencia.transicionarPara(EstadoOcorrencia.EM_ANALISE, Instant.now()))
                .isInstanceOf(TransicaoEstadoOcorrenciaInvalidaException.class);
    }

    @Test
    void deveCompararVersaoConhecida() {
        Ocorrencia ocorrencia = new Ocorrencia(
                1L, "Título", "Descrição", SeveridadeOcorrencia.BAIXA, EstadoOcorrencia.ABERTA, 1L, 2L,
                Instant.now(), null, null, Instant.now(), 3L);

        assertThat(ocorrencia.possuiVersao(3L)).isTrue();
        assertThat(ocorrencia.possuiVersao(2L)).isFalse();
    }
}
