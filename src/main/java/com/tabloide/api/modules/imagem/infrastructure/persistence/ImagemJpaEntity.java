package com.tabloide.api.modules.imagem.infrastructure.persistence;

import com.tabloide.api.modules.imagem.domain.FormatoImagem;
import com.tabloide.api.modules.imagem.domain.TipoVinculoImagem;
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
@Table(name = "imagens")
public class ImagemJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supermercado_id", nullable = false)
    private Long supermercadoId;

    @Column(name = "nome_busca", nullable = false)
    private String nomeBusca;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vinculo", nullable = false)
    private TipoVinculoImagem tipoVinculo;

    @Column(name = "vinculo_id")
    private Long vinculoId;

    @Column(name = "url_ou_chave", nullable = false)
    private String urlOuChave;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormatoImagem formato;

    @Column(name = "tamanho_bytes", nullable = false)
    private long tamanhoBytes;

    @Column(name = "upload_em", nullable = false)
    private Instant uploadEm;

    @Column(name = "excluido_em")
    private Instant excluidoEm;

    protected ImagemJpaEntity() {
    }

    public ImagemJpaEntity(
            Long id,
            Long supermercadoId,
            String nomeBusca,
            TipoVinculoImagem tipoVinculo,
            Long vinculoId,
            String urlOuChave,
            FormatoImagem formato,
            long tamanhoBytes,
            Instant uploadEm,
            Instant excluidoEm
    ) {
        this.id = id;
        this.supermercadoId = supermercadoId;
        this.nomeBusca = nomeBusca;
        this.tipoVinculo = tipoVinculo;
        this.vinculoId = vinculoId;
        this.urlOuChave = urlOuChave;
        this.formato = formato;
        this.tamanhoBytes = tamanhoBytes;
        this.uploadEm = uploadEm;
        this.excluidoEm = excluidoEm;
    }

    public Long getId() {
        return id;
    }

    public Long getSupermercadoId() {
        return supermercadoId;
    }

    public String getNomeBusca() {
        return nomeBusca;
    }

    public TipoVinculoImagem getTipoVinculo() {
        return tipoVinculo;
    }

    public Long getVinculoId() {
        return vinculoId;
    }

    public void setVinculoId(Long vinculoId) {
        this.vinculoId = vinculoId;
    }

    public String getUrlOuChave() {
        return urlOuChave;
    }

    public FormatoImagem getFormato() {
        return formato;
    }

    public long getTamanhoBytes() {
        return tamanhoBytes;
    }

    public Instant getUploadEm() {
        return uploadEm;
    }

    public Instant getExcluidoEm() {
        return excluidoEm;
    }

    public void setExcluidoEm(Instant excluidoEm) {
        this.excluidoEm = excluidoEm;
    }
}
