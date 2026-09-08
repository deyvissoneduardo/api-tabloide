package com.tabloide.api.modules.categoria.domain;

import com.tabloide.api.modules.categoria.domain.exceptions.TransicaoEstadoInvalidaException;
import com.tabloide.api.modules.categoria.domain.exceptions.CategoriaDesativadaNaoAceitaAssociacaoException;
import java.time.Instant;
import java.util.Objects;

public class Categoria {

    private final Long id;
    private final Long supermercadoId;
    private String nome;
    private String descricao;
    private EstadoCategoria estado;
    private final Long versao;
    private final Instant criadoEm;
    private Instant atualizadoEm;

    public Categoria(
            Long id,
            Long supermercadoId,
            String nome,
            String descricao,
            EstadoCategoria estado,
            Long versao,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        this.id = id;
        this.supermercadoId = supermercadoId;
        this.nome = nome;
        this.descricao = descricao;
        this.estado = estado;
        this.versao = versao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public static Categoria cadastrar(Long supermercadoId, String nome, String descricao, Instant agora) {
        return new Categoria(null, supermercadoId, normalizarNome(nome), normalizarOpcional(descricao), EstadoCategoria.ATIVA, null, agora, agora);
    }

    public void editar(String nome, String descricao, Instant agora) {
        this.nome = normalizarNome(nome);
        this.descricao = normalizarOpcional(descricao);
        this.atualizadoEm = agora;
    }

    public void ativar(Instant agora) {
        if (!podeAtivar()) {
            throw new TransicaoEstadoInvalidaException();
        }
        this.estado = EstadoCategoria.ATIVA;
        this.atualizadoEm = agora;
    }

    public void desativar(Instant agora) {
        if (!podeDesativar()) {
            throw new TransicaoEstadoInvalidaException();
        }
        this.estado = EstadoCategoria.DESATIVADA;
        this.atualizadoEm = agora;
    }

    public boolean estaAtiva() {
        return estado == EstadoCategoria.ATIVA;
    }

    public void validarNovaAssociacao() {
        if (!estaAtiva()) {
            throw new CategoriaDesativadaNaoAceitaAssociacaoException();
        }
    }

    public boolean possuiVersao(Long versaoConhecida) {
        return Objects.equals(versao, versaoConhecida);
    }

    private boolean podeAtivar() {
        return estado == EstadoCategoria.DESATIVADA;
    }

    private boolean podeDesativar() {
        return estado == EstadoCategoria.ATIVA;
    }

    private static String normalizarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    private static String normalizarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome da categoria é obrigatório");
        }
        return nome.trim();
    }

    public String resumoParaAuditoria() {
        return "supermercadoId=" + supermercadoId
                + ", nome=" + nome
                + ", descricao=" + descricao
                + ", estado=" + estado;
    }

    public Long id() {
        return id;
    }

    public Long supermercadoId() {
        return supermercadoId;
    }

    public String nome() {
        return nome;
    }

    public String descricao() {
        return descricao;
    }

    public EstadoCategoria estado() {
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
}
