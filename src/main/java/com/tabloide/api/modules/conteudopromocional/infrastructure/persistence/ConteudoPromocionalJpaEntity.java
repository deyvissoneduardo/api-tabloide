package com.tabloide.api.modules.conteudopromocional.infrastructure.persistence;

import com.tabloide.api.modules.conteudopromocional.domain.EstadoConteudoPromocional;
import com.tabloide.api.modules.conteudopromocional.domain.NivelAviso;
import com.tabloide.api.modules.conteudopromocional.domain.TipoConteudoPromocional;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "conteudos_promocionais")
public class ConteudoPromocionalJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supermercado_id", nullable = false)
    private Long supermercadoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConteudoPromocional tipo;

    @Column(nullable = false)
    private String titulo;

    @Column
    private String texto;

    @Enumerated(EnumType.STRING)
    @Column
    private NivelAviso nivel;

    @Column
    private String destino;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "conteudo_promocional_lojas", joinColumns = @JoinColumn(name = "conteudo_promocional_id"))
    @Column(name = "loja_id", nullable = false)
    private Set<Long> lojaIds = new HashSet<>();

    @Column(nullable = false)
    private Instant inicio;

    @Column(nullable = false)
    private Instant fim;

    @Column(nullable = false)
    private int posicao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoConteudoPromocional estado;

    @Version
    @Column(nullable = false)
    private Long versao;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected ConteudoPromocionalJpaEntity() {
    }

    public ConteudoPromocionalJpaEntity(
            Long id,
            Long supermercadoId,
            TipoConteudoPromocional tipo,
            String titulo,
            String texto,
            NivelAviso nivel,
            String destino,
            Set<Long> lojaIds,
            Instant inicio,
            Instant fim,
            int posicao,
            EstadoConteudoPromocional estado,
            Long versao,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        this.id = id;
        this.supermercadoId = supermercadoId;
        this.tipo = tipo;
        this.titulo = titulo;
        this.texto = texto;
        this.nivel = nivel;
        this.destino = destino;
        this.lojaIds = new HashSet<>(lojaIds);
        this.inicio = inicio;
        this.fim = fim;
        this.posicao = posicao;
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

    public TipoConteudoPromocional getTipo() {
        return tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public NivelAviso getNivel() {
        return nivel;
    }

    public void setNivel(NivelAviso nivel) {
        this.nivel = nivel;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public Set<Long> getLojaIds() {
        return lojaIds;
    }

    public void setLojaIds(Set<Long> lojaIds) {
        this.lojaIds = new HashSet<>(lojaIds);
    }

    public Instant getInicio() {
        return inicio;
    }

    public void setInicio(Instant inicio) {
        this.inicio = inicio;
    }

    public Instant getFim() {
        return fim;
    }

    public void setFim(Instant fim) {
        this.fim = fim;
    }

    public int getPosicao() {
        return posicao;
    }

    public void setPosicao(int posicao) {
        this.posicao = posicao;
    }

    public EstadoConteudoPromocional getEstado() {
        return estado;
    }

    public void setEstado(EstadoConteudoPromocional estado) {
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
