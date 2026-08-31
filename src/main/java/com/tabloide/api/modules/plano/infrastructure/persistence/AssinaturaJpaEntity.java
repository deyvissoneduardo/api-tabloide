package com.tabloide.api.modules.plano.infrastructure.persistence;

import com.tabloide.api.modules.plano.domain.EstadoAssinatura;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "assinaturas")
public class AssinaturaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supermercado_id", nullable = false)
    private Long supermercadoId;

    @Column(name = "plano_id", nullable = false)
    private Long planoId;

    @Column(name = "plano_nome", nullable = false)
    private String planoNome;

    @Column(name = "plano_validade_dias", nullable = false)
    private int planoValidadeDias;

    @Column(name = "plano_valor", nullable = false)
    private BigDecimal planoValor;

    @Column(name = "plano_limite_fotos")
    private Integer planoLimiteFotos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAssinatura estado;

    @Column(name = "data_inicio", nullable = false)
    private Instant dataInicio;

    @Column(name = "data_fim", nullable = false)
    private Instant dataFim;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    protected AssinaturaJpaEntity() {
    }

    public AssinaturaJpaEntity(
            Long id,
            Long supermercadoId,
            Long planoId,
            String planoNome,
            int planoValidadeDias,
            BigDecimal planoValor,
            Integer planoLimiteFotos,
            EstadoAssinatura estado,
            Instant dataInicio,
            Instant dataFim,
            Instant criadoEm
    ) {
        this.id = id;
        this.supermercadoId = supermercadoId;
        this.planoId = planoId;
        this.planoNome = planoNome;
        this.planoValidadeDias = planoValidadeDias;
        this.planoValor = planoValor;
        this.planoLimiteFotos = planoLimiteFotos;
        this.estado = estado;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public Long getSupermercadoId() {
        return supermercadoId;
    }

    public Long getPlanoId() {
        return planoId;
    }

    public String getPlanoNome() {
        return planoNome;
    }

    public int getPlanoValidadeDias() {
        return planoValidadeDias;
    }

    public BigDecimal getPlanoValor() {
        return planoValor;
    }

    public Integer getPlanoLimiteFotos() {
        return planoLimiteFotos;
    }

    public EstadoAssinatura getEstado() {
        return estado;
    }

    public void setEstado(EstadoAssinatura estado) {
        this.estado = estado;
    }

    public Instant getDataInicio() {
        return dataInicio;
    }

    public Instant getDataFim() {
        return dataFim;
    }

    public void setDataFim(Instant dataFim) {
        this.dataFim = dataFim;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
