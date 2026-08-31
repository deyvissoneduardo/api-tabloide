package com.tabloide.api.modules.operation.infrastructure.persistence;

import com.tabloide.api.modules.operation.domain.EstadoOcorrencia;
import com.tabloide.api.modules.operation.domain.SeveridadeOcorrencia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;

@Entity
@Table(name = "ocorrencias")
public class OcorrenciaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeveridadeOcorrencia severidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoOcorrencia estado;

    @Column(name = "supermercado_id")
    private Long supermercadoId;

    @Column(name = "responsavel_id")
    private Long responsavelId;

    @Column(name = "aberta_em", nullable = false)
    private Instant abertaEm;

    @Column(name = "resolvida_em")
    private Instant resolvidaEm;

    @Column(name = "encerrada_em")
    private Instant encerradaEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    @Version
    @Column(nullable = false)
    private Long versao;

    protected OcorrenciaJpaEntity() {
    }

    public OcorrenciaJpaEntity(
            Long id,
            String titulo,
            String descricao,
            SeveridadeOcorrencia severidade,
            EstadoOcorrencia estado,
            Long supermercadoId,
            Long responsavelId,
            Instant abertaEm,
            Instant resolvidaEm,
            Instant encerradaEm,
            Instant atualizadoEm,
            Long versao
    ) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.severidade = severidade;
        this.estado = estado;
        this.supermercadoId = supermercadoId;
        this.responsavelId = responsavelId;
        this.abertaEm = abertaEm;
        this.resolvidaEm = resolvidaEm;
        this.encerradaEm = encerradaEm;
        this.atualizadoEm = atualizadoEm;
        this.versao = versao;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public SeveridadeOcorrencia getSeveridade() {
        return severidade;
    }

    public EstadoOcorrencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoOcorrencia estado) {
        this.estado = estado;
    }

    public Long getSupermercadoId() {
        return supermercadoId;
    }

    public Long getResponsavelId() {
        return responsavelId;
    }

    public Instant getAbertaEm() {
        return abertaEm;
    }

    public Instant getResolvidaEm() {
        return resolvidaEm;
    }

    public void setResolvidaEm(Instant resolvidaEm) {
        this.resolvidaEm = resolvidaEm;
    }

    public Instant getEncerradaEm() {
        return encerradaEm;
    }

    public void setEncerradaEm(Instant encerradaEm) {
        this.encerradaEm = encerradaEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(Instant atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public Long getVersao() {
        return versao;
    }
}
