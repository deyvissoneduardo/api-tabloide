package com.tabloide.api.modules.campanha.domain;

import com.tabloide.api.modules.campanha.domain.exceptions.LojasCampanhaInvalidasException;
import com.tabloide.api.modules.campanha.domain.exceptions.PeriodoCampanhaInvalidoException;
import com.tabloide.api.modules.campanha.domain.exceptions.TransicaoEstadoCampanhaInvalidaException;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;

public class Campanha {

    private final Long id;
    private final Long supermercadoId;
    private String nome;
    private String descricao;
    private Set<Long> lojaIds;
    private Set<Long> ofertaIds;
    private Instant inicio;
    private Instant fim;
    private EstadoCampanha estado;
    private final Long versao;
    private final Instant criadoEm;
    private Instant atualizadoEm;

    public Campanha(
            Long id,
            Long supermercadoId,
            String nome,
            String descricao,
            Set<Long> lojaIds,
            Set<Long> ofertaIds,
            Instant inicio,
            Instant fim,
            EstadoCampanha estado,
            Long versao,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        this.id = id;
        this.supermercadoId = supermercadoId;
        this.nome = nome;
        this.descricao = descricao;
        this.lojaIds = Set.copyOf(lojaIds);
        this.ofertaIds = Set.copyOf(ofertaIds);
        this.inicio = inicio;
        this.fim = fim;
        this.estado = estado;
        this.versao = versao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public static Campanha cadastrar(
            Long supermercadoId, String nome, String descricao, Set<Long> lojaIds, Set<Long> ofertaIds,
            Instant inicio, Instant fim, boolean confirmarPublicacao, Instant agora
    ) {
        validarLojasInformadas(lojaIds);
        validarPeriodo(inicio, fim);
        Set<Long> ofertaIdsTratados = normalizarOfertas(ofertaIds);
        EstadoCampanha estado = confirmarPublicacao ? calcularEstadoPublicado(inicio, fim, agora) : EstadoCampanha.RASCUNHO;
        return new Campanha(
                null, supermercadoId, nome.trim(), normalizarOpcional(descricao), Set.copyOf(lojaIds), ofertaIdsTratados,
                inicio, fim, estado, null, agora, agora
        );
    }

    public void editar(String nome, String descricao, Set<Long> lojaIds, Set<Long> ofertaIds, Instant inicio, Instant fim, Instant agora) {
        if (estaFinalizada()) {
            throw new TransicaoEstadoCampanhaInvalidaException();
        }
        validarLojasInformadas(lojaIds);
        validarPeriodo(inicio, fim);
        this.nome = nome.trim();
        this.descricao = normalizarOpcional(descricao);
        this.lojaIds = Set.copyOf(lojaIds);
        this.ofertaIds = normalizarOfertas(ofertaIds);
        this.inicio = inicio;
        this.fim = fim;
        this.atualizadoEm = agora;
    }

    public void cancelar(Instant agora) {
        if (estaFinalizada()) {
            throw new TransicaoEstadoCampanhaInvalidaException();
        }
        this.estado = EstadoCampanha.CANCELADA;
        this.atualizadoEm = agora;
    }

    public boolean estaFinalizada() {
        return estado == EstadoCampanha.CANCELADA || estado == EstadoCampanha.EXPIRADA;
    }

    public int quantidadeLojas() {
        return lojaIds.size();
    }

    public int quantidadeOfertas() {
        return ofertaIds.size();
    }

    public boolean possuiVersao(Long versaoConhecida) {
        return Objects.equals(versao, versaoConhecida);
    }

    private static EstadoCampanha calcularEstadoPublicado(Instant inicio, Instant fim, Instant agora) {
        if (agora.isBefore(inicio)) {
            return EstadoCampanha.AGENDADA;
        }
        if (agora.isAfter(fim)) {
            return EstadoCampanha.EXPIRADA;
        }
        return EstadoCampanha.VIGENTE;
    }

    private static void validarLojasInformadas(Set<Long> lojaIds) {
        if (lojaIds == null || lojaIds.isEmpty() || lojaIds.stream().anyMatch(Objects::isNull)) {
            throw new LojasCampanhaInvalidasException();
        }
    }

    private static void validarPeriodo(Instant inicio, Instant fim) {
        if (inicio == null || fim == null || inicio.isAfter(fim)) {
            throw new PeriodoCampanhaInvalidoException();
        }
    }

    private static Set<Long> normalizarOfertas(Set<Long> ofertaIds) {
        return ofertaIds == null ? Set.of() : Set.copyOf(ofertaIds);
    }

    private static String normalizarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    public String resumoParaAuditoria() {
        return "supermercadoId=" + supermercadoId
                + ", nome=" + nome
                + ", lojaIds=" + lojaIds
                + ", ofertaIds=" + ofertaIds
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

    public String nome() {
        return nome;
    }

    public String descricao() {
        return descricao;
    }

    public Set<Long> lojaIds() {
        return lojaIds;
    }

    public Set<Long> ofertaIds() {
        return ofertaIds;
    }

    public Instant inicio() {
        return inicio;
    }

    public Instant fim() {
        return fim;
    }

    public EstadoCampanha estado() {
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
