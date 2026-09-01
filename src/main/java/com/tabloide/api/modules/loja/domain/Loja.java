package com.tabloide.api.modules.loja.domain;

import com.tabloide.api.modules.loja.domain.exceptions.TransicaoEstadoInvalidaException;
import com.tabloide.api.modules.supermercado.domain.Endereco;
import java.time.Instant;
import java.util.Objects;

public class Loja {

    private final Long id;
    private final Long supermercadoId;
    private String nome;
    private String nomeNormalizado;
    private Endereco endereco;
    private String complemento;
    private EstadoLoja estado;
    private final Long versao;
    private final Instant criadoEm;
    private Instant atualizadoEm;

    public Loja(
            Long id,
            Long supermercadoId,
            String nome,
            String nomeNormalizado,
            Endereco endereco,
            String complemento,
            EstadoLoja estado,
            Long versao,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        this.id = id;
        this.supermercadoId = supermercadoId;
        this.nome = nome;
        this.nomeNormalizado = nomeNormalizado;
        this.endereco = endereco;
        this.complemento = complemento;
        this.estado = estado;
        this.versao = versao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public static Loja cadastrar(Long supermercadoId, String nome, Endereco endereco, String complemento, Instant agora) {
        String nomeTratado = nome.trim();
        return new Loja(
                null, supermercadoId, nomeTratado, normalizarNome(nomeTratado), endereco, complemento,
                EstadoLoja.ATIVA, null, agora, agora
        );
    }

    // RN-002: nome é único dentro do supermercado sem diferenciar caixa ou espaços externos.
    public static String normalizarNome(String nome) {
        return nome.trim().toLowerCase();
    }

    public void ativar(Instant agora) {
        if (!podeAtivar()) {
            throw new TransicaoEstadoInvalidaException();
        }
        this.estado = EstadoLoja.ATIVA;
        this.atualizadoEm = agora;
    }

    public void desativar(Instant agora) {
        if (!podeDesativar()) {
            throw new TransicaoEstadoInvalidaException();
        }
        this.estado = EstadoLoja.DESATIVADA;
        this.atualizadoEm = agora;
    }

    public boolean estaAtiva() {
        return estado == EstadoLoja.ATIVA;
    }

    public boolean possuiVersao(Long versaoConhecida) {
        return Objects.equals(versao, versaoConhecida);
    }

    private boolean podeAtivar() {
        return estado == EstadoLoja.DESATIVADA;
    }

    private boolean podeDesativar() {
        return estado == EstadoLoja.ATIVA;
    }

    public String resumoParaAuditoria() {
        return "supermercadoId=" + supermercadoId
                + ", nome=" + nome
                + ", endereco=" + endereco
                + ", complemento=" + complemento
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

    public String nomeNormalizado() {
        return nomeNormalizado;
    }

    public Endereco endereco() {
        return endereco;
    }

    public String complemento() {
        return complemento;
    }

    public EstadoLoja estado() {
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
