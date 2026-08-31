package com.tabloide.api.modules.plano.infrastructure.persistence;

import com.tabloide.api.modules.plano.domain.TipoAvisoAssinatura;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "avisos_internos")
public class AvisoInternoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assinatura_id", nullable = false)
    private Long assinaturaId;

    @Column(name = "destinatario_usuario_id", nullable = false)
    private Long destinatarioUsuarioId;

    @Column(name = "supermercado_id", nullable = false)
    private Long supermercadoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAvisoAssinatura tipo;

    @Column(nullable = false)
    private String mensagem;

    @Column(name = "lido_em")
    private Instant lidoEm;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    protected AvisoInternoJpaEntity() {
    }

    public AvisoInternoJpaEntity(
            Long id,
            Long assinaturaId,
            Long destinatarioUsuarioId,
            Long supermercadoId,
            TipoAvisoAssinatura tipo,
            String mensagem,
            Instant lidoEm,
            Instant criadoEm
    ) {
        this.id = id;
        this.assinaturaId = assinaturaId;
        this.destinatarioUsuarioId = destinatarioUsuarioId;
        this.supermercadoId = supermercadoId;
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.lidoEm = lidoEm;
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public Long getAssinaturaId() {
        return assinaturaId;
    }

    public Long getDestinatarioUsuarioId() {
        return destinatarioUsuarioId;
    }

    public Long getSupermercadoId() {
        return supermercadoId;
    }

    public TipoAvisoAssinatura getTipo() {
        return tipo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public Instant getLidoEm() {
        return lidoEm;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
