package com.tabloide.api.modules.autenticacao.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "sessoes")
public class SessaoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, unique = true)
    private String jti;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "expira_em", nullable = false)
    private Instant expiraEm;

    @Column(name = "ultimo_uso_em", nullable = false)
    private Instant ultimoUsoEm;

    @Column(name = "revogada_em")
    private Instant revogadaEm;

    protected SessaoJpaEntity() {
    }

    public SessaoJpaEntity(
            Long id,
            Long usuarioId,
            String jti,
            Instant criadoEm,
            Instant expiraEm,
            Instant ultimoUsoEm,
            Instant revogadaEm
    ) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.jti = jti;
        this.criadoEm = criadoEm;
        this.expiraEm = expiraEm;
        this.ultimoUsoEm = ultimoUsoEm;
        this.revogadaEm = revogadaEm;
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getJti() {
        return jti;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getExpiraEm() {
        return expiraEm;
    }

    public Instant getUltimoUsoEm() {
        return ultimoUsoEm;
    }

    public void setUltimoUsoEm(Instant ultimoUsoEm) {
        this.ultimoUsoEm = ultimoUsoEm;
    }

    public Instant getRevogadaEm() {
        return revogadaEm;
    }

    public void setRevogadaEm(Instant revogadaEm) {
        this.revogadaEm = revogadaEm;
    }
}
