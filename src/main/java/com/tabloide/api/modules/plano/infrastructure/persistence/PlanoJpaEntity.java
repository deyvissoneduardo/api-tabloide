package com.tabloide.api.modules.plano.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "planos")
public class PlanoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "nome_normalizado", nullable = false)
    private String nomeNormalizado;

    @Column(name = "validade_dias", nullable = false)
    private int validadeDias;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(name = "limite_fotos")
    private Integer limiteFotos;

    @Column(name = "limite_lojas")
    private Integer limiteLojas;

    @Version
    @Column(nullable = false)
    private Long versao;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    @Column(name = "excluido_em")
    private Instant excluidoEm;

    protected PlanoJpaEntity() {
    }

    public PlanoJpaEntity(
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

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeNormalizado() {
        return nomeNormalizado;
    }

    public void setNomeNormalizado(String nomeNormalizado) {
        this.nomeNormalizado = nomeNormalizado;
    }

    public int getValidadeDias() {
        return validadeDias;
    }

    public void setValidadeDias(int validadeDias) {
        this.validadeDias = validadeDias;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Integer getLimiteFotos() {
        return limiteFotos;
    }

    public void setLimiteFotos(Integer limiteFotos) {
        this.limiteFotos = limiteFotos;
    }

    public Integer getLimiteLojas() {
        return limiteLojas;
    }

    public void setLimiteLojas(Integer limiteLojas) {
        this.limiteLojas = limiteLojas;
    }

    public Long getVersao() {
        return versao;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(Instant atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public Instant getExcluidoEm() {
        return excluidoEm;
    }

    public void setExcluidoEm(Instant excluidoEm) {
        this.excluidoEm = excluidoEm;
    }
}
