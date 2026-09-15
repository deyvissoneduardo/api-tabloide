package com.tabloide.api.modules.conteudogeral.domain;

import com.tabloide.api.modules.conteudogeral.domain.exceptions.TransicaoEstadoConteudoGeralInvalidaException;
import java.time.Instant;
import java.util.Objects;

public class ConteudoGeral {

    private final Long id;
    private final TipoConteudoGeral tipo;
    private String titulo;
    private String corpo;
    private EstadoConteudoGeral estado;
    private final Long versao;
    private final Instant criadoEm;
    private Instant atualizadoEm;
    private Instant publicadoEm;

    public ConteudoGeral(
            Long id,
            TipoConteudoGeral tipo,
            String titulo,
            String corpo,
            EstadoConteudoGeral estado,
            Long versao,
            Instant criadoEm,
            Instant atualizadoEm,
            Instant publicadoEm
    ) {
        this.id = id;
        this.tipo = tipo;
        this.titulo = titulo;
        this.corpo = corpo;
        this.estado = estado;
        this.versao = versao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
        this.publicadoEm = publicadoEm;
    }

    public static ConteudoGeral criar(TipoConteudoGeral tipo, String titulo, String corpo, Instant agora) {
        return new ConteudoGeral(null, tipo, titulo.trim(), corpo.trim(), EstadoConteudoGeral.RASCUNHO, null, agora, agora, null);
    }

    public void editar(String titulo, String corpo, Instant agora) {
        if (!podeSerEditado()) {
            throw new TransicaoEstadoConteudoGeralInvalidaException();
        }
        this.titulo = titulo.trim();
        this.corpo = corpo.trim();
        this.atualizadoEm = agora;
    }

    public void publicar(Instant agora) {
        if (!podeSerPublicado()) {
            throw new TransicaoEstadoConteudoGeralInvalidaException();
        }
        this.estado = EstadoConteudoGeral.PUBLICADO;
        this.publicadoEm = agora;
        this.atualizadoEm = agora;
    }

    public void arquivar(Instant agora) {
        if (!podeSerArquivado()) {
            throw new TransicaoEstadoConteudoGeralInvalidaException();
        }
        this.estado = EstadoConteudoGeral.ARQUIVADO;
        this.atualizadoEm = agora;
    }

    public boolean podeSerEditado() {
        return estado == EstadoConteudoGeral.RASCUNHO;
    }

    private boolean podeSerPublicado() {
        return estado == EstadoConteudoGeral.RASCUNHO;
    }

    private boolean podeSerArquivado() {
        return estado == EstadoConteudoGeral.PUBLICADO;
    }

    public boolean possuiVersao(Long versaoConhecida) {
        return Objects.equals(versao, versaoConhecida);
    }

    public String resumoParaAuditoria() {
        return "tipo=" + tipo
                + ", titulo=" + titulo
                + ", estado=" + estado;
    }

    public Long id() {
        return id;
    }

    public TipoConteudoGeral tipo() {
        return tipo;
    }

    public String titulo() {
        return titulo;
    }

    public String corpo() {
        return corpo;
    }

    public EstadoConteudoGeral estado() {
        return estado;
    }

    public Long versao() {
        return versao;
    }

    public Instant criadoEm() {
        return criadoEm;
    }

    public Instant atualizadoEm() {
        return atualizadoEm;
    }

    public Instant publicadoEm() {
        return publicadoEm;
    }
}
