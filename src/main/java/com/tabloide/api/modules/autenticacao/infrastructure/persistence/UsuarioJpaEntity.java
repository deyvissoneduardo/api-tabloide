package com.tabloide.api.modules.autenticacao.infrastructure.persistence;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "usuarios")
public class UsuarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Perfil perfil;

    @Column(name = "supermercado_id")
    private Long supermercadoId;

    @Column(name = "supermercado_cnpj")
    private String supermercadoCnpj;

    @Column(nullable = false)
    private boolean ativo;

    @Column(name = "tentativas_login_invalidas", nullable = false)
    private int tentativasLoginInvalidas;

    @Column(name = "bloqueado_ate")
    private Instant bloqueadoAte;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected UsuarioJpaEntity() {
    }

    public UsuarioJpaEntity(
            Long id,
            String email,
            String senhaHash,
            Perfil perfil,
            Long supermercadoId,
            String supermercadoCnpj,
            boolean ativo,
            int tentativasLoginInvalidas,
            Instant bloqueadoAte,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        this.id = id;
        this.email = email;
        this.senhaHash = senhaHash;
        this.perfil = perfil;
        this.supermercadoId = supermercadoId;
        this.supermercadoCnpj = supermercadoCnpj;
        this.ativo = ativo;
        this.tentativasLoginInvalidas = tentativasLoginInvalidas;
        this.bloqueadoAte = bloqueadoAte;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public Long getSupermercadoId() {
        return supermercadoId;
    }

    public String getSupermercadoCnpj() {
        return supermercadoCnpj;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public int getTentativasLoginInvalidas() {
        return tentativasLoginInvalidas;
    }

    public void setTentativasLoginInvalidas(int tentativasLoginInvalidas) {
        this.tentativasLoginInvalidas = tentativasLoginInvalidas;
    }

    public Instant getBloqueadoAte() {
        return bloqueadoAte;
    }

    public void setBloqueadoAte(Instant bloqueadoAte) {
        this.bloqueadoAte = bloqueadoAte;
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
