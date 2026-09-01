package com.tabloide.api.modules.imagem.domain;

import com.tabloide.api.modules.imagem.domain.exceptions.FormatoOuTamanhoInvalidoException;
import java.time.Instant;

public class Imagem {

    // RN-009: máximo de 5 MB por arquivo.
    public static final long TAMANHO_MAXIMO_BYTES = 5L * 1024 * 1024;

    private final Long id;
    private final Long supermercadoId;
    private final String nomeBusca;
    private final TipoVinculoImagem tipoVinculo;
    private Long vinculoId;
    private final String urlOuChave;
    private final FormatoImagem formato;
    private final long tamanhoBytes;
    private final Instant uploadEm;
    private Instant excluidoEm;

    public Imagem(
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

    public static Imagem registrar(
            Long supermercadoId,
            String nomeBusca,
            TipoVinculoImagem tipoVinculo,
            Long vinculoId,
            String urlOuChave,
            FormatoImagem formato,
            long tamanhoBytes,
            Instant agora
    ) {
        if (tamanhoBytes <= 0 || tamanhoBytes > TAMANHO_MAXIMO_BYTES) {
            throw new FormatoOuTamanhoInvalidoException("Arquivo deve ter até 5 MB");
        }
        return new Imagem(null, supermercadoId, nomeBusca.trim(), tipoVinculo, vinculoId, urlOuChave, formato, tamanhoBytes, agora, null);
    }

    public boolean estaVinculada() {
        return vinculoId != null;
    }

    public boolean estaAtiva() {
        return excluidoEm == null;
    }

    public void excluir(Instant agora) {
        this.excluidoEm = agora;
    }

    public String resumoParaAuditoria() {
        return "supermercadoId=" + supermercadoId
                + ", nomeBusca=" + nomeBusca
                + ", tipoVinculo=" + tipoVinculo
                + ", vinculoId=" + vinculoId
                + ", formato=" + formato
                + ", tamanhoBytes=" + tamanhoBytes
                + ", excluidoEm=" + excluidoEm;
    }

    public Long id() {
        return id;
    }

    public Long supermercadoId() {
        return supermercadoId;
    }

    public String nomeBusca() {
        return nomeBusca;
    }

    public TipoVinculoImagem tipoVinculo() {
        return tipoVinculo;
    }

    public Long vinculoId() {
        return vinculoId;
    }

    public String urlOuChave() {
        return urlOuChave;
    }

    public FormatoImagem formato() {
        return formato;
    }

    public long tamanhoBytes() {
        return tamanhoBytes;
    }

    public Instant uploadEm() {
        return uploadEm;
    }

    public Instant excluidoEm() {
        return excluidoEm;
    }
}
