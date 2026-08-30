package com.tabloide.api.modules.autenticacao.application;

import com.tabloide.api.modules.autenticacao.domain.Cnpj;
import com.tabloide.api.modules.autenticacao.domain.RedefinicaoSenha;
import com.tabloide.api.modules.autenticacao.domain.RedefinicaoSenhaRepository;
import com.tabloide.api.modules.autenticacao.domain.TokenOpaco;
import com.tabloide.api.modules.autenticacao.domain.Usuario;
import com.tabloide.api.modules.autenticacao.domain.UsuarioRepository;
import com.tabloide.api.modules.autenticacao.domain.exceptions.DadosRedefinicaoNaoConferemException;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class VerificarESolicitarRedefinicaoSenha {

    private final UsuarioRepository usuarioRepository;
    private final RedefinicaoSenhaRepository redefinicaoSenhaRepository;
    private final AutenticacaoProperties propriedades;

    public VerificarESolicitarRedefinicaoSenha(
            UsuarioRepository usuarioRepository,
            RedefinicaoSenhaRepository redefinicaoSenhaRepository,
            AutenticacaoProperties propriedades
    ) {
        this.usuarioRepository = usuarioRepository;
        this.redefinicaoSenhaRepository = redefinicaoSenhaRepository;
        this.propriedades = propriedades;
    }

    public ResultadoVerificacaoRedefinicao verificar(String cnpjInformado, String email) {
        Instant agora = Instant.now();
        Cnpj cnpj = tentarCriarCnpj(cnpjInformado);

        Usuario usuario = usuarioRepository.buscarPorEmailECnpj(normalizarEmail(email), cnpj)
                .filter(Usuario::podeSolicitarRedefinicaoSenha)
                .orElseThrow(DadosRedefinicaoNaoConferemException::new);

        TokenOpaco token = TokenOpaco.gerar();
        RedefinicaoSenha redefinicao = RedefinicaoSenha.solicitar(
                usuario.id(),
                token.hash(),
                agora,
                propriedades.redefinicaoSenha().validade()
        );
        redefinicaoSenhaRepository.salvar(redefinicao);

        return new ResultadoVerificacaoRedefinicao(token.valorBruto(), redefinicao.expiraEm());
    }

    private static Cnpj tentarCriarCnpj(String cnpjInformado) {
        try {
            return new Cnpj(cnpjInformado);
        } catch (IllegalArgumentException e) {
            throw new DadosRedefinicaoNaoConferemException();
        }
    }

    private static String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
