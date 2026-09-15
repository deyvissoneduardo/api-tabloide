package com.tabloide.api.modules.tabloide.domain;

import com.tabloide.api.modules.tabloide.domain.exceptions.ArquivoTabloideInvalidoException;
import com.tabloide.api.modules.tabloide.domain.exceptions.LojasTabloideInvalidasException;
import com.tabloide.api.modules.tabloide.domain.exceptions.PeriodoTabloideInvalidoException;
import com.tabloide.api.modules.tabloide.domain.exceptions.TituloTabloideInvalidoException;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;

public class Tabloide {

    // RN-002: PDF de até 20 MB.
    public static final long TAMANHO_MAXIMO_PDF_BYTES = 20L * 1024 * 1024;

    private final Long id;
    private final Long supermercadoId;
    private String titulo;
    private TipoArquivoTabloide tipoArquivo;
    private String arquivoPdfUrl;
    private Long arquivoPdfTamanhoBytes;
    private Set<Long> lojaIds;
    private Instant inicio;
    private Instant fim;
    private EstadoTabloide estado;
    private final Long versao;
    private final Instant criadoEm;
    private Instant atualizadoEm;

    public Tabloide(
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
        this.lojaIds = Set.copyOf(lojaIds);
        this.inicio = inicio;
        this.fim = fim;
        this.estado = estado;
        this.versao = versao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public static Tabloide disponibilizar(
            Long supermercadoId, String titulo, TipoArquivoTabloide tipoArquivo, String arquivoPdfUrl,
            Long arquivoPdfTamanhoBytes, Set<Long> lojaIds, Instant inicio, Instant fim, Instant agora
    ) {
        String tituloTratado = validarTitulo(titulo);
        validarLojasInformadas(lojaIds);
        validarPeriodo(inicio, fim);
        validarArquivo(tipoArquivo, arquivoPdfUrl, arquivoPdfTamanhoBytes);
        EstadoTabloide estado = calcularEstadoPublicado(inicio, fim, agora);
        return new Tabloide(
                null, supermercadoId, tituloTratado, tipoArquivo, arquivoPdfUrl, arquivoPdfTamanhoBytes,
                Set.copyOf(lojaIds), inicio, fim, estado, null, agora, agora
        );
    }

    public boolean estaAtiva() {
        return estado == EstadoTabloide.AGENDADO || estado == EstadoTabloide.VIGENTE;
    }

    public EstadoTabloide estadoEfetivo(Instant agora) {
        if (estaAtiva()) {
            return calcularEstadoPublicado(inicio, fim, agora);
        }
        return estado;
    }

    private static EstadoTabloide calcularEstadoPublicado(Instant inicio, Instant fim, Instant agora) {
        if (agora.isBefore(inicio)) {
            return EstadoTabloide.AGENDADO;
        }
        if (agora.isAfter(fim)) {
            return EstadoTabloide.EXPIRADO;
        }
        return EstadoTabloide.VIGENTE;
    }

    private static String validarTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new TituloTabloideInvalidoException();
        }
        return titulo.trim();
    }

    private static void validarLojasInformadas(Set<Long> lojaIds) {
        if (lojaIds == null || lojaIds.isEmpty() || lojaIds.stream().anyMatch(Objects::isNull)) {
            throw new LojasTabloideInvalidasException();
        }
    }

    private static void validarPeriodo(Instant inicio, Instant fim) {
        if (inicio == null || fim == null || inicio.isAfter(fim)) {
            throw new PeriodoTabloideInvalidoException();
        }
    }

    // RN-002/RN-007: PDF exige URL e tamanho até 20 MB; páginas em imagem são registradas
    // à parte no módulo de imagens (TipoVinculoImagem.TABLOIDE) e não usam esses campos.
    private static void validarArquivo(TipoArquivoTabloide tipoArquivo, String arquivoPdfUrl, Long arquivoPdfTamanhoBytes) {
        if (tipoArquivo == null) {
            throw new ArquivoTabloideInvalidoException();
        }
        if (tipoArquivo == TipoArquivoTabloide.PDF) {
            boolean urlAusente = arquivoPdfUrl == null || arquivoPdfUrl.isBlank();
            boolean tamanhoInvalido = arquivoPdfTamanhoBytes == null
                    || arquivoPdfTamanhoBytes <= 0
                    || arquivoPdfTamanhoBytes > TAMANHO_MAXIMO_PDF_BYTES;
            if (urlAusente || tamanhoInvalido) {
                throw new ArquivoTabloideInvalidoException();
            }
            return;
        }
        if (arquivoPdfUrl != null || arquivoPdfTamanhoBytes != null) {
            throw new ArquivoTabloideInvalidoException();
        }
    }

    public boolean possuiVersao(Long versaoConhecida) {
        return Objects.equals(versao, versaoConhecida);
    }

    public String resumoParaAuditoria() {
        return "supermercadoId=" + supermercadoId
                + ", titulo=" + titulo
                + ", tipoArquivo=" + tipoArquivo
                + ", lojaIds=" + lojaIds
                + ", inicio=" + inicio
                + ", fim=" + fim
                + ", estado=" + estado;
    }

    public Long id() {
        return id;
    }

    public Long supermercadoId() {
        return supermercadoId;
    }

    public String titulo() {
        return titulo;
    }

    public TipoArquivoTabloide tipoArquivo() {
        return tipoArquivo;
    }

    public String arquivoPdfUrl() {
        return arquivoPdfUrl;
    }

    public Long arquivoPdfTamanhoBytes() {
        return arquivoPdfTamanhoBytes;
    }

    public Set<Long> lojaIds() {
        return lojaIds;
    }

    public Instant inicio() {
        return inicio;
    }

    public Instant fim() {
        return fim;
    }

    public EstadoTabloide estado() {
        return estado;
    }

    public Long versao() {
        return versao;
    }

    public Instant criadoEm() {
        return criadoEm;
    }

    public Instant atualizadoEm() {
        return atualizadoEm;
    }
}
