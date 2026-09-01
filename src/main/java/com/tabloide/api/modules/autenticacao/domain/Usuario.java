package com.tabloide.api.modules.autenticacao.domain;

import com.tabloide.api.modules.autenticacao.domain.exceptions.PerfilInvalidoParaCadastroException;
import java.time.Duration;
import java.time.Instant;

public class Usuario {

    private final Long id;
    private String email;
    private String senhaHash;
    private final Perfil perfil;
    private final Long supermercadoId;
    private final Cnpj supermercadoCnpj;
    private boolean ativo;
    private int tentativasLoginInvalidas;
    private Instant bloqueadoAte;
    private final Instant criadoEm;
    private Instant atualizadoEm;

    public Usuario(
            Long id,
            String email,
            String senhaHash,
            Perfil perfil,
            Long supermercadoId,
            Cnpj supermercadoCnpj,
            boolean ativo,
            int tentativasLoginInvalidas,
            Instant bloqueadoAte,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        this.id = id;
        this.email = email.toLowerCase();
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

    public static void validarPerfilCadastravel(Perfil perfil) {
        if (perfil == Perfil.SUPER_ADMIN) {
            throw new PerfilInvalidoParaCadastroException();
        }
    }

    public static Usuario cadastrar(
            String email,
            String senhaHash,
            Perfil perfil,
            Long supermercadoId,
            Cnpj supermercadoCnpj,
            Instant agora
    ) {
        validarPerfilCadastravel(perfil);
        return new Usuario(null, email, senhaHash, perfil, supermercadoId, supermercadoCnpj, true, 0, null, agora, agora);
    }

    public String resumoParaAuditoria() {
        return "email=" + email + ", perfil=" + perfil + ", supermercadoId=" + supermercadoId + ", ativo=" + ativo;
    }

    public boolean estaAtivo() {
        return ativo;
    }

    public void ativar(Instant agora) {
        this.ativo = true;
        this.atualizadoEm = agora;
    }

    public void desativar(Instant agora) {
        this.ativo = false;
        this.atualizadoEm = agora;
    }

    public boolean estaBloqueadoTemporariamente(Instant agora) {
        return bloqueadoAte != null && agora.isBefore(bloqueadoAte);
    }

    public void registrarTentativaInvalida(Instant agora, int maxTentativas, Duration bloqueioTemporario) {
        tentativasLoginInvalidas++;
        if (tentativasLoginInvalidas >= maxTentativas) {
            bloqueadoAte = agora.plus(bloqueioTemporario);
        }
        atualizadoEm = agora;
    }

    public void registrarLoginBemSucedido(Instant agora) {
        tentativasLoginInvalidas = 0;
        bloqueadoAte = null;
        atualizadoEm = agora;
    }

    public void alterarSenha(String novoHash, Instant agora) {
        this.senhaHash = novoHash;
        this.atualizadoEm = agora;
    }

    public void alterarEmail(String novoEmail, Instant agora) {
        this.email = novoEmail.toLowerCase();
        this.atualizadoEm = agora;
    }

    public boolean podeSolicitarRedefinicaoSenha() {
        return ativo && perfil != Perfil.SUPER_ADMIN;
    }

    public boolean pertenceAoSupermercado(Cnpj cnpj) {
        return supermercadoCnpj != null && supermercadoCnpj.equals(cnpj);
    }

    public Long id() {
        return id;
    }

    public String email() {
        return email;
    }

    public String senhaHash() {
        return senhaHash;
    }

    public Perfil perfil() {
        return perfil;
    }

    public Long supermercadoId() {
        return supermercadoId;
    }

    public Cnpj supermercadoCnpj() {
        return supermercadoCnpj;
    }

    public int tentativasLoginInvalidas() {
        return tentativasLoginInvalidas;
    }

    public Instant bloqueadoAte() {
        return bloqueadoAte;
    }

    public Instant criadoEm() {
        return criadoEm;
    }

    public Instant atualizadoEm() {
        return atualizadoEm;
    }
}
