package com.tabloide.api.modules.autenticacao.infrastructure.security;

import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.Sessao;
import com.tabloide.api.modules.autenticacao.domain.Usuario;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenService {

    private static final String CLAIM_PERFIL = "perfil";
    private static final String CLAIM_SUPERMERCADO_ID = "supermercadoId";

    private final SecretKey chave;

    public JwtTokenService(@Value("${app.auth.jwt.secret}") String segredo) {
        this.chave = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
    }

    public String emitir(Usuario usuario, Sessao sessao) {
        var builder = Jwts.builder()
                .subject(String.valueOf(usuario.id()))
                .claim(CLAIM_PERFIL, usuario.perfil().name())
                .id(sessao.jti())
                .issuedAt(Date.from(sessao.criadoEm()))
                .expiration(Date.from(sessao.expiraEm()))
                .signWith(chave);

        if (usuario.supermercadoId() != null) {
            builder.claim(CLAIM_SUPERMERCADO_ID, usuario.supermercadoId());
        }

        return builder.compact();
    }

    public ClaimsSessao decodificar(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(chave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return new ClaimsSessao(
                    Long.valueOf(claims.getSubject()),
                    Perfil.valueOf(claims.get(CLAIM_PERFIL, String.class)),
                    claims.get(CLAIM_SUPERMERCADO_ID, Long.class),
                    claims.getId()
            );
        } catch (JwtException | IllegalArgumentException e) {
            throw new SessaoInvalidaOuExpiradaException();
        }
    }
}
