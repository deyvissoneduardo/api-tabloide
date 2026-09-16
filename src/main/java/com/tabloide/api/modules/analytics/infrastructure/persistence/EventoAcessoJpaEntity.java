package com.tabloide.api.modules.analytics.infrastructure.persistence;

import com.tabloide.api.modules.analytics.domain.TipoEventoAcesso;
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
@Table(name = "eventos_acesso")
public class EventoAcessoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEventoAcesso tipo;

    @Column(name = "supermercado_id", nullable = false)
    private Long supermercadoId;

    @Column(name = "loja_id")
    private Long lojaId;

    @Column(name = "recurso_id")
    private Long recursoId;

    @Column(name = "evento_tecnico_id")
    private String eventoTecnicoId;

    @Column(nullable = false)
    private Instant instante;

    protected EventoAcessoJpaEntity() {
    }

    public EventoAcessoJpaEntity(
            Long id, TipoEventoAcesso tipo, Long supermercadoId, Long lojaId, Long recursoId, String eventoTecnicoId, Instant instante
    ) {
        this.id = id;
        this.tipo = tipo;
        this.supermercadoId = supermercadoId;
        this.lojaId = lojaId;
        this.recursoId = recursoId;
        this.eventoTecnicoId = eventoTecnicoId;
        this.instante = instante;
    }

    public Long getId() {
        return id;
    }

    public TipoEventoAcesso getTipo() {
        return tipo;
    }

    public Long getSupermercadoId() {
        return supermercadoId;
    }

    public Long getLojaId() {
        return lojaId;
    }

    public Long getRecursoId() {
        return recursoId;
    }

    public String getEventoTecnicoId() {
        return eventoTecnicoId;
    }

    public Instant getInstante() {
        return instante;
    }
}
