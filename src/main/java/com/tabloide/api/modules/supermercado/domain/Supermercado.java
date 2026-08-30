package com.tabloide.api.modules.supermercado.domain;

import com.tabloide.api.modules.autenticacao.domain.Cnpj;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoBloqueadoOuDesativadoException;
import com.tabloide.api.modules.supermercado.domain.exceptions.TransicaoEstadoInvalidaException;
import java.time.Instant;
import java.util.Objects;

public class Supermercado {

    private final Long id;
    private final Cnpj cnpj;
    private String razaoSocial;
    private String nomeFantasia;
    private String emailComercial;
    private String telefoneComercial;
    private Endereco endereco;
    private String complemento;
    private String logomarcaUrl;
    private String observacoesInternas;
    private EstadoSupermercado estado;
    private final Long versao;
    private final Instant criadoEm;
    private Instant atualizadoEm;

    public Supermercado(
            Long id,
            Cnpj cnpj,
            String razaoSocial,
            String nomeFantasia,
            String emailComercial,
            String telefoneComercial,
            Endereco endereco,
            String complemento,
            String logomarcaUrl,
            String observacoesInternas,
            EstadoSupermercado estado,
            Long versao,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        this.id = id;
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
        this.emailComercial = emailComercial;
        this.telefoneComercial = telefoneComercial;
        this.endereco = endereco;
        this.complemento = complemento;
        this.logomarcaUrl = logomarcaUrl;
        this.observacoesInternas = observacoesInternas;
        this.estado = estado;
        this.versao = versao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public static Supermercado cadastrar(
            Cnpj cnpj,
            String razaoSocial,
            String nomeFantasia,
            String emailComercial,
            String telefoneComercial,
            Endereco endereco,
            String complemento,
            String logomarcaUrl,
            String observacoesInternas,
            Instant agora
    ) {
        return new Supermercado(
                null,
                cnpj,
                razaoSocial,
                nomeFantasia,
                emailComercial,
                telefoneComercial,
                endereco,
                complemento,
                logomarcaUrl,
                observacoesInternas,
                EstadoSupermercado.ATIVO,
                null,
                agora,
                agora
        );
    }

    public void editarDados(
            String razaoSocial,
            String nomeFantasia,
            String emailComercial,
            String telefoneComercial,
            Endereco endereco,
            String complemento,
            String logomarcaUrl,
            String observacoesInternas,
            Instant agora
    ) {
        if (!podeSerEditado()) {
            throw new SupermercadoBloqueadoOuDesativadoException();
        }
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
        this.emailComercial = emailComercial;
        this.telefoneComercial = telefoneComercial;
        this.endereco = endereco;
        this.complemento = complemento;
        this.logomarcaUrl = logomarcaUrl;
        this.observacoesInternas = observacoesInternas;
        this.atualizadoEm = agora;
    }

    public void ativar(Instant agora) {
        if (!podeAtivar()) {
            throw new TransicaoEstadoInvalidaException();
        }
        this.estado = EstadoSupermercado.ATIVO;
        this.atualizadoEm = agora;
    }

    public void desativar(Instant agora) {
        if (!podeDesativar()) {
            throw new TransicaoEstadoInvalidaException();
        }
        this.estado = EstadoSupermercado.DESATIVADO;
        this.atualizadoEm = agora;
    }

    public void bloquear(Instant agora) {
        if (!podeBloquear()) {
            throw new TransicaoEstadoInvalidaException();
        }
        this.estado = EstadoSupermercado.BLOQUEADO;
        this.atualizadoEm = agora;
    }

    public boolean estaAtivo() {
        return estado == EstadoSupermercado.ATIVO;
    }

    public boolean estaDesativado() {
        return estado == EstadoSupermercado.DESATIVADO;
    }

    public boolean estaBloqueado() {
        return estado == EstadoSupermercado.BLOQUEADO;
    }

    public boolean possuiVersao(Long versaoConhecida) {
        return Objects.equals(versao, versaoConhecida);
    }

    private boolean podeSerEditado() {
        return estado == EstadoSupermercado.ATIVO;
    }

    private boolean podeAtivar() {
        return estado == EstadoSupermercado.BLOQUEADO || estado == EstadoSupermercado.DESATIVADO;
    }

    private boolean podeDesativar() {
        return estado == EstadoSupermercado.ATIVO || estado == EstadoSupermercado.BLOQUEADO;
    }

    private boolean podeBloquear() {
        return estado == EstadoSupermercado.ATIVO;
    }

    public String resumoParaAuditoria() {
        return "cnpj=" + cnpj.valor()
                + ", razaoSocial=" + razaoSocial
                + ", nomeFantasia=" + nomeFantasia
                + ", emailComercial=" + emailComercial
                + ", telefoneComercial=" + telefoneComercial
                + ", endereco=" + endereco
                + ", complemento=" + complemento
                + ", estado=" + estado;
    }

    public Long id() {
        return id;
    }

    public Cnpj cnpj() {
        return cnpj;
    }

    public String razaoSocial() {
        return razaoSocial;
    }

    public String nomeFantasia() {
        return nomeFantasia;
    }

    public String emailComercial() {
        return emailComercial;
    }

    public String telefoneComercial() {
        return telefoneComercial;
    }

    public Endereco endereco() {
        return endereco;
    }

    public String complemento() {
        return complemento;
    }

    public String logomarcaUrl() {
        return logomarcaUrl;
    }

    public String observacoesInternas() {
        return observacoesInternas;
    }

    public EstadoSupermercado estado() {
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
