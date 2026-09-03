package com.tabloide.api.modules.produto.domain;

import java.time.Instant;
import java.util.Set;

public class Produto {

    private final Long id;
    private final Long supermercadoId;
    private String nome;
    private Set<Long> categoriaIds;
    private String marca;
    private String descricao;
    private String peso;
    private String unidade;
    private String volume;
    private EstadoProduto estado;
    private final Long versao;
    private final Instant criadoEm;
    private Instant atualizadoEm;

    public Produto(
            Long id,
            Long supermercadoId,
            String nome,
            Set<Long> categoriaIds,
            String marca,
            String descricao,
            String peso,
            String unidade,
            String volume,
            EstadoProduto estado,
            Long versao,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        this.id = id;
        this.supermercadoId = supermercadoId;
        this.nome = nome;
        this.categoriaIds = categoriaIds;
        this.marca = marca;
        this.descricao = descricao;
        this.peso = peso;
        this.unidade = unidade;
        this.volume = volume;
        this.estado = estado;
        this.versao = versao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public static Produto cadastrar(
            Long supermercadoId, String nome, Set<Long> categoriaIds, String marca,
            String descricao, String peso, String unidade, String volume, Instant agora
    ) {
        return new Produto(
                null, supermercadoId, nome.trim(), Set.copyOf(categoriaIds), normalizarOpcional(marca),
                normalizarOpcional(descricao), normalizarOpcional(peso), normalizarOpcional(unidade),
                normalizarOpcional(volume), EstadoProduto.ATIVO, null, agora, agora
        );
    }

    public boolean estaAtivo() {
        return estado == EstadoProduto.ATIVO;
    }

    private static String normalizarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    public String resumoParaAuditoria() {
        return "supermercadoId=" + supermercadoId
                + ", nome=" + nome
                + ", categoriaIds=" + categoriaIds
                + ", marca=" + marca
                + ", descricao=" + descricao
                + ", peso=" + peso
                + ", unidade=" + unidade
                + ", volume=" + volume
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

    public Set<Long> categoriaIds() {
        return categoriaIds;
    }

    public String marca() {
        return marca;
    }

    public String descricao() {
        return descricao;
    }

    public String peso() {
        return peso;
    }

    public String unidade() {
        return unidade;
    }

    public String volume() {
        return volume;
    }

    public EstadoProduto estado() {
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
