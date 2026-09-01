package com.tabloide.api.modules.loja.infrastructure.persistence;

import com.tabloide.api.modules.loja.domain.EstadoLoja;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;

@Entity
@Table(name = "lojas")
public class LojaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supermercado_id", nullable = false)
    private Long supermercadoId;

    @Column(nullable = false)
    private String nome;

    @Column(name = "nome_normalizado", nullable = false)
    private String nomeNormalizado;

    @Column(nullable = false)
    private String cep;

    @Column(nullable = false)
    private String logradouro;

    @Column(nullable = false)
    private String numero;

    @Column(nullable = false)
    private String bairro;

    @Column(nullable = false)
    private String municipio;

    @Column(nullable = false, length = 2)
    private String uf;

    @Column
    private String complemento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoLoja estado;

    @Version
    @Column(nullable = false)
    private Long versao;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected LojaJpaEntity() {
    }

    public LojaJpaEntity(
            Long id,
            Long supermercadoId,
            String nome,
            String nomeNormalizado,
            String cep,
            String logradouro,
            String numero,
            String bairro,
            String municipio,
            String uf,
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
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.bairro = bairro;
        this.municipio = municipio;
        this.uf = uf;
        this.complemento = complemento;
        this.estado = estado;
        this.versao = versao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public Long getId() {
        return id;
    }

    public Long getSupermercadoId() {
        return supermercadoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeNormalizado() {
        return nomeNormalizado;
    }

    public void setNomeNormalizado(String nomeNormalizado) {
        this.nomeNormalizado = nomeNormalizado;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public EstadoLoja getEstado() {
        return estado;
    }

    public void setEstado(EstadoLoja estado) {
        this.estado = estado;
    }

    public Long getVersao() {
        return versao;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(Instant atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}
