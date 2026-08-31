package com.tabloide.api.modules.plano.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class Plano {

    private final Long id;
    private String nome;
    private String nomeNormalizado;
    private int validadeDias;
    private BigDecimal valor;
    private Integer limiteFotos;
    private Integer limiteLojas;
    private final Long versao;
    private final Instant criadoEm;
    private Instant atualizadoEm;
    private Instant excluidoEm;

    public Plano(
            Long id,
            String nome,
            String nomeNormalizado,
            int validadeDias,
            BigDecimal valor,
            Integer limiteFotos,
            Integer limiteLojas,
            Long versao,
            Instant criadoEm,
            Instant atualizadoEm,
            Instant excluidoEm
    ) {
        this.id = id;
        this.nome = nome;
        this.nomeNormalizado = nomeNormalizado;
        this.validadeDias = validadeDias;
        this.valor = valor;
        this.limiteFotos = limiteFotos;
        this.limiteLojas = limiteLojas;
        this.versao = versao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
        this.excluidoEm = excluidoEm;
    }

    public static Plano criar(String nome, int validadeDias, BigDecimal valor, Integer limiteFotos, Integer limiteLojas, Instant agora) {
        String nomeTratado = nome.trim();
        return new Plano(null, nomeTratado, normalizarNome(nomeTratado), validadeDias, valor, limiteFotos, limiteLojas, null, agora, agora, null);
    }

    // RN-003: nome de plano disponível é único sem diferenciar caixa ou espaços externos.
    public static String normalizarNome(String nome) {
        return nome.trim().toLowerCase();
    }

    public void editar(String nome, int validadeDias, BigDecimal valor, Integer limiteFotos, Integer limiteLojas, Instant agora) {
        String nomeTratado = nome.trim();
        this.nome = nomeTratado;
        this.nomeNormalizado = normalizarNome(nomeTratado);
        this.validadeDias = validadeDias;
        this.valor = valor;
        this.limiteFotos = limiteFotos;
        this.limiteLojas = limiteLojas;
        this.atualizadoEm = agora;
    }

    public void excluirLogicamente(Instant agora) {
        if (estaExcluido()) {
            return;
        }
        this.excluidoEm = agora;
        this.atualizadoEm = agora;
    }

    public boolean estaExcluido() {
        return excluidoEm != null;
    }

    public boolean possuiVersao(Long versaoConhecida) {
        return Objects.equals(versao, versaoConhecida);
    }

    public String resumoParaAuditoria() {
        return "nome=" + nome
                + ", validadeDias=" + validadeDias
                + ", valor=" + valor
                + ", limiteFotos=" + limiteFotos
                + ", limiteLojas=" + limiteLojas
                + ", excluidoEm=" + excluidoEm;
    }

    public Long id() {
        return id;
    }

    public String nome() {
        return nome;
    }

    public String nomeNormalizado() {
        return nomeNormalizado;
    }

    public int validadeDias() {
        return validadeDias;
    }

    public BigDecimal valor() {
        return valor;
    }

    public Integer limiteFotos() {
        return limiteFotos;
    }

    public Integer limiteLojas() {
        return limiteLojas;
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

    public Instant excluidoEm() {
        return excluidoEm;
    }
}
