package com.tabloide.api.modules.tabloide.infrastructure.persistence;

import com.tabloide.api.modules.tabloide.domain.EstadoTabloide;
import com.tabloide.api.modules.tabloide.domain.TipoArquivoTabloide;
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
@Table(name = "tabloides")
public class TabloideJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supermercado_id", nullable = false)
    private Long supermercadoId;

    @Column(nullable = false)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_arquivo", nullable = false)
    private TipoArquivoTabloide tipoArquivo;

    @Column(name = "arquivo_pdf_url")
    private String arquivoPdfUrl;

    @Column(name = "arquivo_pdf_tamanho_bytes")
    private Long arquivoPdfTamanhoBytes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tabloide_lojas", joinColumns = @JoinColumn(name = "tabloide_id"))
    @Column(name = "loja_id", nullable = false)
    private Set<Long> lojaIds = new HashSet<>();

    @Column(nullable = false)
    private Instant inicio;

    @Column(nullable = false)
    private Instant fim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTabloide estado;

    @Version
    @Column(nullable = false)
    private Long versao;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected TabloideJpaEntity() {
    }

    public TabloideJpaEntity(
            Long id,
            Long supermercadoId,
            String titulo,
            TipoArquivoTabloide tipoArquivo,
            String arquivoPdfUrl,
            Long arquivoPdfTamanhoBytes,
            Set<Long> lojaIds,
            Instant inicio,
            Instant fim,
            EstadoTabloide estado,
            Long versao,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        this.id = id;
        this.supermercadoId = supermercadoId;
        this.titulo = titulo;
        this.tipoArquivo = tipoArquivo;
        this.arquivoPdfUrl = arquivoPdfUrl;
        this.arquivoPdfTamanhoBytes = arquivoPdfTamanhoBytes;
        this.lojaIds = new HashSet<>(lojaIds);
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

    public String getTitulo() {
        return titulo;
    }

    public TipoArquivoTabloide getTipoArquivo() {
        return tipoArquivo;
    }

    public String getArquivoPdfUrl() {
        return arquivoPdfUrl;
    }

    public Long getArquivoPdfTamanhoBytes() {
        return arquivoPdfTamanhoBytes;
    }

    public Set<Long> getLojaIds() {
        return lojaIds;
    }

    public Instant getInicio() {
        return inicio;
    }

    public Instant getFim() {
        return fim;
    }

    public EstadoTabloide getEstado() {
        return estado;
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
}
