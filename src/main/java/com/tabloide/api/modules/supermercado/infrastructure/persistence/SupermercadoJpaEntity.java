package com.tabloide.api.modules.supermercado.infrastructure.persistence;

import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
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
@Table(name = "supermercados")
public class SupermercadoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String cnpj;

    @Column(name = "razao_social", nullable = false)
    private String razaoSocial;

    @Column(name = "nome_fantasia", nullable = false)
    private String nomeFantasia;

    @Column(name = "email_comercial", nullable = false)
    private String emailComercial;

    @Column(name = "telefone_comercial", nullable = false)
    private String telefoneComercial;

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

    @Column(name = "logomarca_url")
    private String logomarcaUrl;

    @Column(name = "observacoes_internas")
    private String observacoesInternas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSupermercado estado;

    @Version
    @Column(nullable = false)
    private Long versao;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected SupermercadoJpaEntity() {
    }

    public SupermercadoJpaEntity(
            Long id,
            String cnpj,
            String razaoSocial,
            String nomeFantasia,
            String emailComercial,
            String telefoneComercial,
            String cep,
            String logradouro,
            String numero,
            String bairro,
            String municipio,
            String uf,
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
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.bairro = bairro;
        this.municipio = municipio;
        this.uf = uf;
        this.complemento = complemento;
        this.logomarcaUrl = logomarcaUrl;
        this.observacoesInternas = observacoesInternas;
        this.estado = estado;
        this.versao = versao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public Long getId() {
        return id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getEmailComercial() {
        return emailComercial;
    }

    public void setEmailComercial(String emailComercial) {
        this.emailComercial = emailComercial;
    }

    public String getTelefoneComercial() {
        return telefoneComercial;
    }

    public void setTelefoneComercial(String telefoneComercial) {
        this.telefoneComercial = telefoneComercial;
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

    public String getLogomarcaUrl() {
        return logomarcaUrl;
    }

    public void setLogomarcaUrl(String logomarcaUrl) {
        this.logomarcaUrl = logomarcaUrl;
    }

    public String getObservacoesInternas() {
        return observacoesInternas;
    }

    public void setObservacoesInternas(String observacoesInternas) {
        this.observacoesInternas = observacoesInternas;
    }

    public EstadoSupermercado getEstado() {
        return estado;
    }

    public void setEstado(EstadoSupermercado estado) {
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
