package com.tabloide.api.modules.oferta.domain;

import com.tabloide.api.modules.oferta.domain.exceptions.LojasOfertaInvalidasException;
import com.tabloide.api.modules.oferta.domain.exceptions.PeriodoOfertaInvalidoException;
import com.tabloide.api.modules.oferta.domain.exceptions.PrecoOfertaInvalidoException;
import com.tabloide.api.modules.oferta.domain.exceptions.TransicaoEstadoOfertaInvalidaException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;

public class Oferta {

    private final Long id;
    private final Long supermercadoId;
    private Long produtoId;
    private Set<Long> lojaIds;
    private BigDecimal precoNormal;
    private BigDecimal precoPromocional;
    private Instant inicio;
    private Instant fim;
    private String condicoes;
    private EstadoOferta estado;
    private final Long versao;
    private final Instant criadoEm;
    private Instant atualizadoEm;

    public Oferta(
            Long id,
            Long supermercadoId,
            Long produtoId,
            Set<Long> lojaIds,
            BigDecimal precoNormal,
            BigDecimal precoPromocional,
            Instant inicio,
            Instant fim,
            String condicoes,
            EstadoOferta estado,
            Long versao,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        this.id = id;
        this.supermercadoId = supermercadoId;
        this.produtoId = produtoId;
        this.lojaIds = Set.copyOf(lojaIds);
        this.precoNormal = precoNormal;
        this.precoPromocional = precoPromocional;
        this.inicio = inicio;
        this.fim = fim;
        this.condicoes = condicoes;
        this.estado = estado;
        this.versao = versao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public static Oferta cadastrar(
            Long supermercadoId, Long produtoId, Set<Long> lojaIds, BigDecimal precoNormal, BigDecimal precoPromocional,
            Instant inicio, Instant fim, String condicoes, boolean confirmarPublicacao, Instant agora
    ) {
        validarLojasInformadas(lojaIds);
        validarPrecos(precoNormal, precoPromocional);
        validarPeriodo(inicio, fim);
        EstadoOferta estado = confirmarPublicacao ? calcularEstadoPublicado(inicio, fim, agora) : EstadoOferta.RASCUNHO;
        return new Oferta(
                null, supermercadoId, produtoId, Set.copyOf(lojaIds), precoNormal, precoPromocional, inicio, fim,
                normalizarOpcional(condicoes), estado, null, agora, agora
        );
    }

    public void editar(
            Long produtoId, Set<Long> lojaIds, BigDecimal precoNormal, BigDecimal precoPromocional,
            Instant inicio, Instant fim, String condicoes, Instant agora
    ) {
        if (estaFinalizada()) {
            throw new TransicaoEstadoOfertaInvalidaException();
        }
        validarLojasInformadas(lojaIds);
        validarPrecos(precoNormal, precoPromocional);
        validarPeriodo(inicio, fim);
        this.produtoId = produtoId;
        this.lojaIds = Set.copyOf(lojaIds);
        this.precoNormal = precoNormal;
        this.precoPromocional = precoPromocional;
        this.inicio = inicio;
        this.fim = fim;
        this.condicoes = normalizarOpcional(condicoes);
        this.atualizadoEm = agora;
    }

    public void cancelar(Instant agora) {
        if (estaFinalizada()) {
            throw new TransicaoEstadoOfertaInvalidaException();
        }
        this.estado = EstadoOferta.CANCELADA;
        this.atualizadoEm = agora;
    }

    public void ativar(Instant agora) {
        if (!estaDesativada()) {
            throw new TransicaoEstadoOfertaInvalidaException();
        }
        this.estado = calcularEstadoPublicado(inicio, fim, agora);
        this.atualizadoEm = agora;
    }

    public void desativar(Instant agora) {
        if (!estaAtiva()) {
            throw new TransicaoEstadoOfertaInvalidaException();
        }
        this.estado = EstadoOferta.DESATIVADA;
        this.atualizadoEm = agora;
    }

    public boolean estaFinalizada() {
        return estado == EstadoOferta.CANCELADA || estado == EstadoOferta.EXPIRADA;
    }

    public boolean estaAtiva() {
        return estado == EstadoOferta.AGENDADA || estado == EstadoOferta.VIGENTE;
    }

    public boolean estaDesativada() {
        return estado == EstadoOferta.DESATIVADA;
    }

    public boolean possuiVersao(Long versaoConhecida) {
        return Objects.equals(versao, versaoConhecida);
    }

    public EstadoOferta estadoEfetivo(Instant agora) {
        if (estaAtiva()) {
            return calcularEstadoPublicado(inicio, fim, agora);
        }
        return estado;
    }

    public Integer percentualDesconto() {
        BigDecimal diferenca = precoNormal.subtract(precoPromocional);
        return diferenca.divide(precoNormal, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
    }

    private static EstadoOferta calcularEstadoPublicado(Instant inicio, Instant fim, Instant agora) {
        if (agora.isBefore(inicio)) {
            return EstadoOferta.AGENDADA;
        }
        if (agora.isAfter(fim)) {
            return EstadoOferta.EXPIRADA;
        }
        return EstadoOferta.VIGENTE;
    }

    private static void validarLojasInformadas(Set<Long> lojaIds) {
        if (lojaIds == null || lojaIds.isEmpty() || lojaIds.stream().anyMatch(Objects::isNull)) {
            throw new LojasOfertaInvalidasException();
        }
    }

    private static void validarPrecos(BigDecimal precoNormal, BigDecimal precoPromocional) {
        if (!ehPrecoValido(precoNormal) || !ehPrecoValido(precoPromocional)) {
            throw new PrecoOfertaInvalidoException();
        }
        if (precoPromocional.compareTo(precoNormal) >= 0) {
            throw new PrecoOfertaInvalidoException();
        }
    }

    private static boolean ehPrecoValido(BigDecimal preco) {
        return preco != null && preco.scale() <= 2 && preco.compareTo(BigDecimal.ZERO) > 0;
    }

    private static void validarPeriodo(Instant inicio, Instant fim) {
        if (inicio == null || fim == null || inicio.isAfter(fim)) {
            throw new PeriodoOfertaInvalidoException();
        }
    }

    private static String normalizarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    public String resumoParaAuditoria() {
        return "supermercadoId=" + supermercadoId
                + ", produtoId=" + produtoId
                + ", lojaIds=" + lojaIds
                + ", precoNormal=" + precoNormal
                + ", precoPromocional=" + precoPromocional
                + ", inicio=" + inicio
                + ", fim=" + fim
                + ", condicoes=" + condicoes
                + ", estado=" + estado;
    }

    public Long id() {
        return id;
    }

    public Long supermercadoId() {
        return supermercadoId;
    }

    public Long produtoId() {
        return produtoId;
    }

    public Set<Long> lojaIds() {
        return lojaIds;
    }

    public BigDecimal precoNormal() {
        return precoNormal;
    }

    public BigDecimal precoPromocional() {
        return precoPromocional;
    }

    public Instant inicio() {
        return inicio;
    }

    public Instant fim() {
        return fim;
    }

    public String condicoes() {
        return condicoes;
    }

    public EstadoOferta estado() {
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
