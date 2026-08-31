package com.tabloide.api.modules.operation.infrastructure.persistence;

import com.tabloide.api.modules.operation.domain.EstadoOcorrencia;
import com.tabloide.api.modules.operation.domain.TipoEventoOcorrencia;
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
@Table(name = "ocorrencia_eventos")
public class EventoOcorrenciaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ocorrencia_id", nullable = false)
    private Long ocorrenciaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEventoOcorrencia tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior")
    private EstadoOcorrencia estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_novo")
    private EstadoOcorrencia estadoNovo;

    @Column
    private String comentario;

    @Column(name = "autor_id", nullable = false)
    private Long autorId;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    protected EventoOcorrenciaJpaEntity() {
    }

    public EventoOcorrenciaJpaEntity(
            Long id,
            Long ocorrenciaId,
            TipoEventoOcorrencia tipo,
            EstadoOcorrencia estadoAnterior,
            EstadoOcorrencia estadoNovo,
            String comentario,
            Long autorId,
            Instant criadoEm
    ) {
        this.id = id;
        this.ocorrenciaId = ocorrenciaId;
        this.tipo = tipo;
        this.estadoAnterior = estadoAnterior;
        this.estadoNovo = estadoNovo;
        this.comentario = comentario;
        this.autorId = autorId;
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public Long getOcorrenciaId() {
        return ocorrenciaId;
    }

    public TipoEventoOcorrencia getTipo() {
        return tipo;
    }

    public EstadoOcorrencia getEstadoAnterior() {
        return estadoAnterior;
    }

    public EstadoOcorrencia getEstadoNovo() {
        return estadoNovo;
    }

    public String getComentario() {
        return comentario;
    }

    public Long getAutorId() {
        return autorId;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
