package com.tabloide.api.modules.plano.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class Assinatura {

    public static final ZoneId FUSO_BRASILIA = ZoneId.of("America/Sao_Paulo");

    private final Long id;
    private final Long supermercadoId;
    private final Long planoId;
    private String planoNome;
    private int planoValidadeDias;
    private BigDecimal planoValor;
    private Integer planoLimiteFotos;
    private EstadoAssinatura estado;
    private final Instant dataInicio;
    private Instant dataFim;
    private final Instant criadoEm;

    public Assinatura(
            Long id,
            Long supermercadoId,
            Long planoId,
            String planoNome,
            int planoValidadeDias,
            BigDecimal planoValor,
            Integer planoLimiteFotos,
            EstadoAssinatura estado,
            Instant dataInicio,
            Instant dataFim,
            Instant criadoEm
    ) {
        this.id = id;
        this.supermercadoId = supermercadoId;
        this.planoId = planoId;
        this.planoNome = planoNome;
        this.planoValidadeDias = planoValidadeDias;
        this.planoValor = planoValor;
        this.planoLimiteFotos = planoLimiteFotos;
        this.estado = estado;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.criadoEm = criadoEm;
    }

    public static Assinatura associar(Long supermercadoId, Plano plano, Instant agora) {
        Instant inicio = inicioNoDiaSeguinte(agora);
        Instant fim = fimDaVigencia(inicio, plano.validadeDias());
        return new Assinatura(
                null,
                supermercadoId,
                plano.id(),
                plano.nome(),
                plano.validadeDias(),
                plano.valor(),
                plano.limiteFotos(),
                EstadoAssinatura.VIGENTE,
                inicio,
                fim,
                agora
        );
    }

    // RN-008: a vigência sempre começa no dia seguinte no fuso de Brasília, mesmo em troca
    // imediata de plano (RN-011) — "imediata" refere-se a substituir a assinatura ativa, não a
    // antecipar a data de início.
    private static Instant inicioNoDiaSeguinte(Instant agora) {
        LocalDate hoje = agora.atZone(FUSO_BRASILIA).toLocalDate();
        return hoje.plusDays(1).atStartOfDay(FUSO_BRASILIA).toInstant();
    }

    private static Instant fimDaVigencia(Instant inicio, int validadeDias) {
        LocalDate dataInicio = inicio.atZone(FUSO_BRASILIA).toLocalDate();
        LocalDate dataFim = dataInicio.plusDays(validadeDias - 1L);
        return dataFim.atTime(23, 59, 59).atZone(FUSO_BRASILIA).toInstant();
    }

    public void substituir(Instant agora) {
        this.estado = EstadoAssinatura.SUBSTITUIDA;
        this.dataFim = agora;
    }

    public boolean estaVigente() {
        return estado == EstadoAssinatura.VIGENTE;
    }

    public boolean estaVencida() {
        return estado == EstadoAssinatura.VENCIDA;
    }

    // RN-009: renovação antecipada preserva os dias restantes (dataFim não retrocede) e
    // enfileira o novo período logo após o atual; o snapshot do plano é atualizado para refletir
    // o plano vigente no momento da renovação (RN-014).
    public void renovarAntecipada(Plano planoAtual) {
        this.dataFim = fimDaVigencia(dataFim.plusSeconds(1), planoAtual.validadeDias());
        this.planoNome = planoAtual.nome();
        this.planoValidadeDias = planoAtual.validadeDias();
        this.planoValor = planoAtual.valor();
        this.planoLimiteFotos = planoAtual.limiteFotos();
    }

    // RN-012: assinatura vigente cujo prazo passou é marcada como vencida; o bloqueio do
    // supermercado é responsabilidade de quem processa o vencimento (não desta entidade).
    public void vencer() {
        this.estado = EstadoAssinatura.VENCIDA;
    }

    public String resumoParaAuditoria() {
        return "supermercadoId=" + supermercadoId
                + ", planoId=" + planoId
                + ", planoNome=" + planoNome
                + ", estado=" + estado
                + ", dataInicio=" + dataInicio
                + ", dataFim=" + dataFim;
    }

    public Long id() {
        return id;
    }

    public Long supermercadoId() {
        return supermercadoId;
    }

    public Long planoId() {
        return planoId;
    }

    public String planoNome() {
        return planoNome;
    }

    public int planoValidadeDias() {
        return planoValidadeDias;
    }

    public BigDecimal planoValor() {
        return planoValor;
    }

    public Integer planoLimiteFotos() {
        return planoLimiteFotos;
    }

    public EstadoAssinatura estado() {
        return estado;
    }

    public Instant dataInicio() {
        return dataInicio;
    }

    public Instant dataFim() {
        return dataFim;
    }

    public Instant criadoEm() {
        return criadoEm;
    }
}
