package com.tabloide.api.modules.conteudogeral.infrastructure.persistence;

import com.tabloide.api.modules.conteudogeral.domain.EstadoConteudoGeral;
import com.tabloide.api.modules.conteudogeral.domain.TipoConteudoGeral;
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
@Table(name = "conteudos_gerais")
public class ConteudoGeralJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConteudoGeral tipo;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String corpo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoConteudoGeral estado;

    @Version
    @Column(nullable = false)
    private Long versao;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    @Column(name = "publicado_em")
    private Instant publicadoEm;

    protected ConteudoGeralJpaEntity() {
    }

    public ConteudoGeralJpaEntity(
            Long id,
            TipoConteudoGeral tipo,
            String titulo,
            String corpo,
            EstadoConteudoGeral estado,
            Long versao,
            Instant criadoEm,
            Instant atualizadoEm,
            Instant publicadoEm
    ) {
        this.id = id;
        this.tipo = tipo;
        this.titulo = titulo;
        this.corpo = corpo;
        this.estado = estado;
        this.versao = versao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
        this.publicadoEm = publicadoEm;
    }

    public Long getId() {
        return id;
    }

    public TipoConteudoGeral getTipo() {
        return tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCorpo() {
        return corpo;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

    public EstadoConteudoGeral getEstado() {
        return estado;
    }

    public void setEstado(EstadoConteudoGeral estado) {
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

    public Instant getPublicadoEm() {
        return publicadoEm;
    }

    public void setPublicadoEm(Instant publicadoEm) {
        this.publicadoEm = publicadoEm;
    }
}
