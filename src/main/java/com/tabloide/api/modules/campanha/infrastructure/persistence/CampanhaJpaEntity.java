package com.tabloide.api.modules.campanha.infrastructure.persistence;

import com.tabloide.api.modules.campanha.domain.EstadoCampanha;
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
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "campanhas")
public class CampanhaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supermercado_id", nullable = false)
    private Long supermercadoId;

    @Column(nullable = false)
    private String nome;

    @Column
    private String descricao;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "campanha_lojas", joinColumns = @JoinColumn(name = "campanha_id"))
    @Column(name = "loja_id", nullable = false)
    private Set<Long> lojaIds = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "campanha_ofertas", joinColumns = @JoinColumn(name = "campanha_id"))
    @Column(name = "oferta_id", nullable = false)
    private Set<Long> ofertaIds = new HashSet<>();

    @Column(nullable = false)
    private Instant inicio;

    @Column(nullable = false)
    private Instant fim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCampanha estado;

    @Version
    @Column(nullable = false)
    private Long versao;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected CampanhaJpaEntity() {
    }

    public CampanhaJpaEntity(
            Long id,
            Long supermercadoId,
            String nome,
            String descricao,
            Set<Long> lojaIds,
            Set<Long> ofertaIds,
            Instant inicio,
            Instant fim,
            EstadoCampanha estado,
            Long versao,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        this.id = id;
        this.supermercadoId = supermercadoId;
        this.nome = nome;
        this.descricao = descricao;
        this.lojaIds = new HashSet<>(lojaIds);
        this.ofertaIds = new HashSet<>(ofertaIds);
        this.inicio = inicio;
        this.fim = fim;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Set<Long> getLojaIds() {
        return lojaIds;
    }

    public void setLojaIds(Set<Long> lojaIds) {
        this.lojaIds = new HashSet<>(lojaIds);
    }

    public Set<Long> getOfertaIds() {
        return ofertaIds;
    }

    public void setOfertaIds(Set<Long> ofertaIds) {
        this.ofertaIds = new HashSet<>(ofertaIds);
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

    public EstadoCampanha getEstado() {
        return estado;
    }

    public void setEstado(EstadoCampanha estado) {
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
