package com.tabloide.api.modules.plano.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "planos")
public class PlanoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(name = "validade_dias", nullable = false)
    private int validadeDias;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(name = "limite_fotos")
    private Integer limiteFotos;

    protected PlanoJpaEntity() {
    }

    public PlanoJpaEntity(Long id, String nome, int validadeDias, BigDecimal valor, Integer limiteFotos) {
        this.id = id;
        this.nome = nome;
        this.validadeDias = validadeDias;
        this.valor = valor;
        this.limiteFotos = limiteFotos;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getValidadeDias() {
        return validadeDias;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public Integer getLimiteFotos() {
        return limiteFotos;
    }
}
