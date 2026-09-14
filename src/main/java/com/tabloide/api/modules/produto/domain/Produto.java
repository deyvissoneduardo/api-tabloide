package com.tabloide.api.modules.produto.domain;

import com.tabloide.api.modules.produto.domain.exceptions.CategoriasProdutoInvalidasException;
import com.tabloide.api.modules.produto.domain.exceptions.TransicaoEstadoProdutoInvalidaException;
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
        this.categoriaIds = Set.copyOf(categoriaIds);
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
        validarCategoriasInformadas(categoriaIds);
        return new Produto(
                null, supermercadoId, normalizarNome(nome), Set.copyOf(categoriaIds), normalizarOpcional(marca),
                normalizarOpcional(descricao), normalizarOpcional(peso), normalizarOpcional(unidade),
                normalizarOpcional(volume), EstadoProduto.ATIVO, null, agora, agora
        );
    }

    public boolean estaAtivo() {
        return estado == EstadoProduto.ATIVO;
    }

    public void substituirCategorias(Set<Long> novasCategoriaIds, Instant agora) {
        validarCategoriasInformadas(novasCategoriaIds);
        this.categoriaIds = Set.copyOf(novasCategoriaIds);
        this.atualizadoEm = agora;
    }

    public void editar(String nome, String marca, String descricao, String peso, String unidade, String volume, Instant agora) {
        this.nome = normalizarNome(nome);
        this.marca = normalizarOpcional(marca);
        this.descricao = normalizarOpcional(descricao);
        this.peso = normalizarOpcional(peso);
        this.unidade = normalizarOpcional(unidade);
        this.volume = normalizarOpcional(volume);
        this.atualizadoEm = agora;
    }

    public void ativar(Instant agora) {
        if (!podeAtivar()) {
            throw new TransicaoEstadoProdutoInvalidaException();
        }
        this.estado = EstadoProduto.ATIVO;
        this.atualizadoEm = agora;
    }

    public void desativar(Instant agora) {
        if (!podeDesativar()) {
            throw new TransicaoEstadoProdutoInvalidaException();
        }
        this.estado = EstadoProduto.DESATIVADO;
        this.atualizadoEm = agora;
    }

    private boolean podeAtivar() {
        return estado == EstadoProduto.DESATIVADO;
    }

    private boolean podeDesativar() {
        return estado == EstadoProduto.ATIVO;
    }

    public boolean possuiVersao(Long versaoConhecida) {
        return java.util.Objects.equals(versao, versaoConhecida);
    }

    private static String normalizarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }
        return nome.trim();
    }

    private static void validarCategoriasInformadas(Set<Long> categoriaIds) {
        if (categoriasNaoForamInformadas(categoriaIds)) {
            throw new CategoriasProdutoInvalidasException();
        }
    }

    private static boolean categoriasNaoForamInformadas(Set<Long> categoriaIds) {
        return categoriaIds == null || categoriaIds.isEmpty() || categoriaIds.stream().anyMatch(java.util.Objects::isNull);
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
