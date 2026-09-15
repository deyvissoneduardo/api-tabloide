package com.tabloide.api.modules.conteudopromocional.domain;

import com.tabloide.api.modules.conteudopromocional.domain.exceptions.DestinoBannerInvalidoException;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.LojasConteudoPromocionalInvalidasException;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.NivelAvisoInvalidoException;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.PeriodoConteudoPromocionalInvalidoException;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.TextoConteudoPromocionalInvalidoException;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.TituloConteudoPromocionalInvalidoException;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;

public class ConteudoPromocional {

    private final Long id;
    private final Long supermercadoId;
    private final TipoConteudoPromocional tipo;
    private String titulo;
    private String texto;
    private NivelAviso nivel;
    private String destino;
    private Set<Long> lojaIds;
    private Instant inicio;
    private Instant fim;
    private int posicao;
    private EstadoConteudoPromocional estado;
    private final Long versao;
    private final Instant criadoEm;
    private Instant atualizadoEm;

    public ConteudoPromocional(
            Long id,
            Long supermercadoId,
            TipoConteudoPromocional tipo,
            String titulo,
            String texto,
            NivelAviso nivel,
            String destino,
            Set<Long> lojaIds,
            Instant inicio,
            Instant fim,
            int posicao,
            EstadoConteudoPromocional estado,
            Long versao,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        this.id = id;
        this.supermercadoId = supermercadoId;
        this.tipo = tipo;
        this.titulo = titulo;
        this.texto = texto;
        this.nivel = nivel;
        this.destino = destino;
        this.lojaIds = Set.copyOf(lojaIds);
        this.inicio = inicio;
        this.fim = fim;
        this.posicao = posicao;
        this.estado = estado;
        this.versao = versao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public static ConteudoPromocional cadastrar(
            Long supermercadoId, TipoConteudoPromocional tipo, String titulo, String texto, NivelAviso nivel,
            String destino, Set<Long> lojaIds, Instant inicio, Instant fim, int posicao, Instant agora
    ) {
        String tituloTratado = validarTitulo(titulo);
        String textoTratado = validarCamposPorTipo(tipo, texto, nivel, destino);
        validarLojasInformadas(lojaIds);
        validarPeriodo(inicio, fim);
        EstadoConteudoPromocional estado = calcularEstadoPublicado(inicio, fim, agora);
        return new ConteudoPromocional(
                null, supermercadoId, tipo, tituloTratado, textoTratado, nivel, normalizarOpcional(destino),
                Set.copyOf(lojaIds), inicio, fim, posicao, estado, null, agora, agora
        );
    }

    public void editar(String titulo, String texto, NivelAviso nivel, String destino, Set<Long> lojaIds, Instant inicio, Instant fim, Instant agora) {
        String tituloTratado = validarTitulo(titulo);
        String textoTratado = validarCamposPorTipo(tipo, texto, nivel, destino);
        validarLojasInformadas(lojaIds);
        validarPeriodo(inicio, fim);
        this.titulo = tituloTratado;
        this.texto = textoTratado;
        this.nivel = nivel;
        this.destino = normalizarOpcional(destino);
        this.lojaIds = Set.copyOf(lojaIds);
        this.inicio = inicio;
        this.fim = fim;
        this.atualizadoEm = agora;
    }

    public void atribuirPosicao(int posicao, Instant agora) {
        this.posicao = posicao;
        this.atualizadoEm = agora;
    }

    public boolean estaAtiva() {
        return estado == EstadoConteudoPromocional.AGENDADO || estado == EstadoConteudoPromocional.VIGENTE;
    }

    public EstadoConteudoPromocional estadoEfetivo(Instant agora) {
        if (estaAtiva()) {
            return calcularEstadoPublicado(inicio, fim, agora);
        }
        return estado;
    }

    private static EstadoConteudoPromocional calcularEstadoPublicado(Instant inicio, Instant fim, Instant agora) {
        if (agora.isBefore(inicio)) {
            return EstadoConteudoPromocional.AGENDADO;
        }
        if (agora.isAfter(fim)) {
            return EstadoConteudoPromocional.EXPIRADO;
        }
        return EstadoConteudoPromocional.VIGENTE;
    }

    private static String validarTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new TituloConteudoPromocionalInvalidoException();
        }
        return titulo.trim();
    }

    // RN-002/RN-003/RN-004: cada tipo tem seu próprio conjunto de campos obrigatórios/proibidos.
    private static String validarCamposPorTipo(TipoConteudoPromocional tipo, String texto, NivelAviso nivel, String destino) {
        return switch (tipo) {
            case MENSAGEM -> {
                proibirNivel(nivel);
                proibirDestino(destino);
                yield exigirTexto(texto);
            }
            case AVISO -> {
                exigirNivel(nivel);
                proibirDestino(destino);
                yield exigirTexto(texto);
            }
            case BANNER -> {
                proibirTexto(texto);
                proibirNivel(nivel);
                validarDestinoOpcional(destino);
                yield null;
            }
        };
    }

    private static String exigirTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new TextoConteudoPromocionalInvalidoException();
        }
        return texto.trim();
    }

    private static void proibirTexto(String texto) {
        if (texto != null && !texto.isBlank()) {
            throw new TextoConteudoPromocionalInvalidoException();
        }
    }

    private static void exigirNivel(NivelAviso nivel) {
        if (nivel == null) {
            throw new NivelAvisoInvalidoException();
        }
    }

    private static void proibirNivel(NivelAviso nivel) {
        if (nivel != null) {
            throw new NivelAvisoInvalidoException();
        }
    }

    private static void proibirDestino(String destino) {
        if (destino != null && !destino.isBlank()) {
            throw new DestinoBannerInvalidoException();
        }
    }

    private static void validarDestinoOpcional(String destino) {
        if (destino == null || destino.isBlank()) {
            return;
        }
        if (!destino.startsWith("/") && !destino.startsWith("https://")) {
            throw new DestinoBannerInvalidoException();
        }
    }

    private static void validarLojasInformadas(Set<Long> lojaIds) {
        if (lojaIds == null || lojaIds.isEmpty() || lojaIds.stream().anyMatch(Objects::isNull)) {
            throw new LojasConteudoPromocionalInvalidasException();
        }
    }

    private static void validarPeriodo(Instant inicio, Instant fim) {
        if (inicio == null || fim == null || inicio.isAfter(fim)) {
            throw new PeriodoConteudoPromocionalInvalidoException();
        }
    }

    private static String normalizarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    public boolean possuiVersao(Long versaoConhecida) {
        return Objects.equals(versao, versaoConhecida);
    }

    public String resumoParaAuditoria() {
        return "supermercadoId=" + supermercadoId
                + ", tipo=" + tipo
                + ", titulo=" + titulo
                + ", lojaIds=" + lojaIds
                + ", inicio=" + inicio
                + ", fim=" + fim
                + ", posicao=" + posicao
                + ", estado=" + estado;
    }

    public Long id() {
        return id;
    }

    public Long supermercadoId() {
        return supermercadoId;
    }

    public TipoConteudoPromocional tipo() {
        return tipo;
    }

    public String titulo() {
        return titulo;
    }

    public String texto() {
        return texto;
    }

    public NivelAviso nivel() {
        return nivel;
    }

    public String destino() {
        return destino;
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

    public int posicao() {
        return posicao;
    }

    public EstadoConteudoPromocional estado() {
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
