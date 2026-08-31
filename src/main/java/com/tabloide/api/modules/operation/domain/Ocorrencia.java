package com.tabloide.api.modules.operation.domain;

import com.tabloide.api.modules.operation.domain.exceptions.TransicaoEstadoOcorrenciaInvalidaException;
import java.time.Instant;
import java.util.Objects;

public class Ocorrencia {

    private final Long id;
    private final String titulo;
    private final String descricao;
    private final SeveridadeOcorrencia severidade;
    private EstadoOcorrencia estado;
    private final Long supermercadoId;
    private final Long responsavelId;
    private final Instant abertaEm;
    private Instant resolvidaEm;
    private Instant encerradaEm;
    private Instant atualizadoEm;
    private final Long versao;

    public Ocorrencia(
            Long id,
            String titulo,
            String descricao,
            SeveridadeOcorrencia severidade,
            EstadoOcorrencia estado,
            Long supermercadoId,
            Long responsavelId,
            Instant abertaEm,
            Instant resolvidaEm,
            Instant encerradaEm,
            Instant atualizadoEm,
            Long versao
    ) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.severidade = severidade;
        this.estado = estado;
        this.supermercadoId = supermercadoId;
        this.responsavelId = responsavelId;
        this.abertaEm = abertaEm;
        this.resolvidaEm = resolvidaEm;
        this.encerradaEm = encerradaEm;
        this.atualizadoEm = atualizadoEm;
        this.versao = versao;
    }

    public static Ocorrencia abrir(
            String titulo, String descricao, SeveridadeOcorrencia severidade, Long supermercadoId, Long responsavelId, Instant agora) {
        return new Ocorrencia(
                null, titulo, descricao, severidade, EstadoOcorrencia.ABERTA, supermercadoId, responsavelId, agora, null, null, agora, null);
    }

    public void transicionarPara(EstadoOcorrencia novoEstado, Instant agora) {
        switch (novoEstado) {
            case EM_ANALISE -> iniciarAnalise(agora);
            case RESOLVIDA -> resolver(agora);
            case ENCERRADA -> encerrar(agora);
            case ABERTA -> throw new TransicaoEstadoOcorrenciaInvalidaException();
        }
    }

    private void iniciarAnalise(Instant agora) {
        if (estado != EstadoOcorrencia.ABERTA) {
            throw new TransicaoEstadoOcorrenciaInvalidaException();
        }
        this.estado = EstadoOcorrencia.EM_ANALISE;
        this.atualizadoEm = agora;
    }

    private void resolver(Instant agora) {
        if (estado != EstadoOcorrencia.EM_ANALISE) {
            throw new TransicaoEstadoOcorrenciaInvalidaException();
        }
        this.estado = EstadoOcorrencia.RESOLVIDA;
        this.resolvidaEm = agora;
        this.atualizadoEm = agora;
    }

    private void encerrar(Instant agora) {
        if (estado != EstadoOcorrencia.RESOLVIDA) {
            throw new TransicaoEstadoOcorrenciaInvalidaException();
        }
        this.estado = EstadoOcorrencia.ENCERRADA;
        this.encerradaEm = agora;
        this.atualizadoEm = agora;
    }

    public void registrarAtividade(Instant agora) {
        this.atualizadoEm = agora;
    }

    public boolean possuiVersao(Long versaoConhecida) {
        return Objects.equals(versao, versaoConhecida);
    }

    public String resumoParaAuditoria() {
        return "titulo=" + titulo
                + ", severidade=" + severidade
                + ", estado=" + estado
                + ", supermercadoId=" + supermercadoId
                + ", responsavelId=" + responsavelId;
    }

    public Long id() {
        return id;
    }

    public String titulo() {
        return titulo;
    }

    public String descricao() {
        return descricao;
    }

    public SeveridadeOcorrencia severidade() {
        return severidade;
    }

    public EstadoOcorrencia estado() {
        return estado;
    }

    public Long supermercadoId() {
        return supermercadoId;
    }

    public Long responsavelId() {
        return responsavelId;
    }

    public Instant abertaEm() {
        return abertaEm;
    }

    public Instant resolvidaEm() {
        return resolvidaEm;
    }

    public Instant encerradaEm() {
        return encerradaEm;
    }

    public Instant atualizadoEm() {
        return atualizadoEm;
    }

    public Long versao() {
        return versao;
    }
}
