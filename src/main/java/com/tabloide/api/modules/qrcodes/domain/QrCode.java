package com.tabloide.api.modules.qrcodes.domain;

import com.tabloide.api.modules.qrcodes.domain.exceptions.TransicaoEstadoQrCodeInvalidaException;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class QrCode {

    private final Long id;
    private final Long supermercadoId;
    private final Long lojaId;
    private String nome;
    private String nomeNormalizado;
    private final String codigoPublico;
    private EstadoQrCode estado;
    private final Long versao;
    private final Instant criadoEm;
    private Instant atualizadoEm;

    public QrCode(
            Long id,
            Long supermercadoId,
            Long lojaId,
            String nome,
            String nomeNormalizado,
            String codigoPublico,
            EstadoQrCode estado,
            Long versao,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        this.id = id;
        this.supermercadoId = supermercadoId;
        this.lojaId = lojaId;
        this.nome = nome;
        this.nomeNormalizado = nomeNormalizado;
        this.codigoPublico = codigoPublico;
        this.estado = estado;
        this.versao = versao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public static QrCode gerar(Long supermercadoId, Long lojaId, String nome, Instant agora) {
        String nomeTratado = nome.trim();
        return new QrCode(
                null, supermercadoId, lojaId, nomeTratado, normalizarNome(nomeTratado), gerarCodigoPublico(),
                EstadoQrCode.ATIVO, null, agora, agora
        );
    }

    // RN-002 (US-103): nome é único dentro do supermercado sem diferenciar caixa ou espaços externos.
    public static String normalizarNome(String nome) {
        return nome.trim().toLowerCase();
    }

    private static String gerarCodigoPublico() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public void ativar(Instant agora) {
        if (!podeAtivar()) {
            throw new TransicaoEstadoQrCodeInvalidaException();
        }
        this.estado = EstadoQrCode.ATIVO;
        this.atualizadoEm = agora;
    }

    public void desativar(Instant agora) {
        if (!podeDesativar()) {
            throw new TransicaoEstadoQrCodeInvalidaException();
        }
        this.estado = EstadoQrCode.DESATIVADO;
        this.atualizadoEm = agora;
    }

    public boolean estaAtivo() {
        return estado == EstadoQrCode.ATIVO;
    }

    public boolean possuiVersao(Long versaoConhecida) {
        return Objects.equals(versao, versaoConhecida);
    }

    private boolean podeAtivar() {
        return estado == EstadoQrCode.DESATIVADO;
    }

    private boolean podeDesativar() {
        return estado == EstadoQrCode.ATIVO;
    }

    public String resumoParaAuditoria() {
        return "supermercadoId=" + supermercadoId
                + ", lojaId=" + lojaId
                + ", nome=" + nome
                + ", estado=" + estado;
    }

    public Long id() {
        return id;
    }

    public Long supermercadoId() {
        return supermercadoId;
    }

    public Long lojaId() {
        return lojaId;
    }

    public String nome() {
        return nome;
    }

    public String nomeNormalizado() {
        return nomeNormalizado;
    }

    public String codigoPublico() {
        return codigoPublico;
    }

    public EstadoQrCode estado() {
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
