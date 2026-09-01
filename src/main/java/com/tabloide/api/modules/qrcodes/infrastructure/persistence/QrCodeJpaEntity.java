package com.tabloide.api.modules.qrcodes.infrastructure.persistence;

import com.tabloide.api.modules.qrcodes.domain.EstadoQrCode;
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
@Table(name = "qrcodes")
public class QrCodeJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supermercado_id", nullable = false)
    private Long supermercadoId;

    @Column(name = "loja_id", nullable = false)
    private Long lojaId;

    @Column(nullable = false)
    private String nome;

    @Column(name = "nome_normalizado", nullable = false)
    private String nomeNormalizado;

    @Column(name = "codigo_publico", nullable = false)
    private String codigoPublico;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoQrCode estado;

    @Version
    @Column(nullable = false)
    private Long versao;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected QrCodeJpaEntity() {
    }

    public QrCodeJpaEntity(
            Long id,
            Long supermercadoId,
            Long lojaId,
            String nome,
            String nomeNormalizado,
            String codigoPublico,
            EstadoQrCode estado,
            Long versao,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        this.id = id;
        this.supermercadoId = supermercadoId;
        this.lojaId = lojaId;
        this.nome = nome;
        this.nomeNormalizado = nomeNormalizado;
        this.codigoPublico = codigoPublico;
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

    public Long getLojaId() {
        return lojaId;
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

    public String getCodigoPublico() {
        return codigoPublico;
    }

    public EstadoQrCode getEstado() {
        return estado;
    }

    public void setEstado(EstadoQrCode estado) {
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
