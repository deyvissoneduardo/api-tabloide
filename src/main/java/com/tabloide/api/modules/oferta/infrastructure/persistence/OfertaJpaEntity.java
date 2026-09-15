package com.tabloide.api.modules.oferta.infrastructure.persistence;

import com.tabloide.api.modules.oferta.domain.EstadoOferta;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ofertas")
public class OfertaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supermercado_id", nullable = false)
    private Long supermercadoId;

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "oferta_lojas", joinColumns = @JoinColumn(name = "oferta_id"))
    @Column(name = "loja_id", nullable = false)
    private Set<Long> lojaIds = new HashSet<>();

    @Column(name = "preco_normal", nullable = false)
    private BigDecimal precoNormal;

    @Column(name = "preco_promocional", nullable = false)
    private BigDecimal precoPromocional;

    @Column(nullable = false)
    private Instant inicio;

    @Column(nullable = false)
    private Instant fim;

    @Column
    private String condicoes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoOferta estado;

    @Version
    @Column(nullable = false)
    private Long versao;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected OfertaJpaEntity() {
    }

    public OfertaJpaEntity(
            Long id,
            Long supermercadoId,
            Long produtoId,
            Set<Long> lojaIds,
            BigDecimal precoNormal,
            BigDecimal precoPromocional,
            Instant inicio,
            Instant fim,
            String condicoes,
            EstadoOferta estado,
            Long versao,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        this.id = id;
        this.supermercadoId = supermercadoId;
        this.produtoId = produtoId;
        this.lojaIds = new HashSet<>(lojaIds);
        this.precoNormal = precoNormal;
        this.precoPromocional = precoPromocional;
        this.inicio = inicio;
        this.fim = fim;
        this.condicoes = condicoes;
        this.estado = estado;
        this.versao = versao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public Long getId() {
        return id;
    }

    public Long getSupermercadoId() {
        return supermercadoId;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public Set<Long> getLojaIds() {
        return lojaIds;
    }

    public void setLojaIds(Set<Long> lojaIds) {
        this.lojaIds = new HashSet<>(lojaIds);
    }

    public BigDecimal getPrecoNormal() {
        return precoNormal;
    }

    public void setPrecoNormal(BigDecimal precoNormal) {
        this.precoNormal = precoNormal;
    }

    public BigDecimal getPrecoPromocional() {
        return precoPromocional;
    }

    public void setPrecoPromocional(BigDecimal precoPromocional) {
        this.precoPromocional = precoPromocional;
    }

    public Instant getInicio() {
        return inicio;
    }

    public void setInicio(Instant inicio) {
        this.inicio = inicio;
    }

    public Instant getFim() {
        return fim;
    }

    public void setFim(Instant fim) {
        this.fim = fim;
    }

    public String getCondicoes() {
        return condicoes;
    }

    public void setCondicoes(String condicoes) {
        this.condicoes = condicoes;
    }

    public EstadoOferta getEstado() {
        return estado;
    }

    public void setEstado(EstadoOferta estado) {
        this.estado = estado;
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
}
