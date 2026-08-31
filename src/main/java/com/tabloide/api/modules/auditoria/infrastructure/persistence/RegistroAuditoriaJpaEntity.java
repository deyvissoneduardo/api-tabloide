package com.tabloide.api.modules.auditoria.infrastructure.persistence;

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
@Table(name = "registros_auditoria")
public class RegistroAuditoriaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ator_id")
    private Long atorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "perfil_ator", nullable = false)
    private Perfil perfilAtor;

    @Column(name = "supermercado_id")
    private Long supermercadoId;

    @Column(nullable = false)
    private String acao;

    @Column(nullable = false)
    private String entidade;

    @Column(name = "entidade_id", nullable = false)
    private Long entidadeId;

    @Column(name = "dados_antes")
    private String dadosAntes;

    @Column(name = "dados_depois", nullable = false)
    private String dadosDepois;

    @Column(nullable = false)
    private Instant instante;

    protected RegistroAuditoriaJpaEntity() {
    }

    public RegistroAuditoriaJpaEntity(
            Long atorId,
            Perfil perfilAtor,
            Long supermercadoId,
            String acao,
            String entidade,
            Long entidadeId,
            String dadosAntes,
            String dadosDepois,
            Instant instante
    ) {
        this.atorId = atorId;
        this.perfilAtor = perfilAtor;
        this.supermercadoId = supermercadoId;
        this.acao = acao;
        this.entidade = entidade;
        this.entidadeId = entidadeId;
        this.dadosAntes = dadosAntes;
        this.dadosDepois = dadosDepois;
        this.instante = instante;
    }

    public Long getId() {
        return id;
    }

    public Long getAtorId() {
        return atorId;
    }

    public Perfil getPerfilAtor() {
        return perfilAtor;
    }

    public Long getSupermercadoId() {
        return supermercadoId;
    }

    public String getAcao() {
        return acao;
    }

    public String getEntidade() {
        return entidade;
    }

    public Long getEntidadeId() {
        return entidadeId;
    }

    public String getDadosAntes() {
        return dadosAntes;
    }

    public String getDadosDepois() {
        return dadosDepois;
    }

    public Instant getInstante() {
        return instante;
    }
}
