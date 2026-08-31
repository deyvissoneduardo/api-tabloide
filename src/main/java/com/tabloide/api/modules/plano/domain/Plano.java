package com.tabloide.api.modules.plano.domain;

import java.math.BigDecimal;

public class Plano {

    private final Long id;
    private final String nome;
    private final int validadeDias;
    private final BigDecimal valor;
    private final Integer limiteFotos;

    public Plano(Long id, String nome, int validadeDias, BigDecimal valor, Integer limiteFotos) {
        this.id = id;
        this.nome = nome;
        this.validadeDias = validadeDias;
        this.valor = valor;
        this.limiteFotos = limiteFotos;
    }

    public Long id() {
        return id;
    }

    public String nome() {
        return nome;
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
}
