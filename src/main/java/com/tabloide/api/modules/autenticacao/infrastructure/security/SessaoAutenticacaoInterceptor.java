package com.tabloide.api.modules.autenticacao.infrastructure.security;

import com.tabloide.api.modules.autenticacao.application.ValidarSessao;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessaoAutenticacaoInterceptor implements HandlerInterceptor {

    private static final String PREFIXO_BEARER = "Bearer ";
    private static final String CAMINHO_LOGIN = "/api/sessoes";

    private final JwtTokenService jwtTokenService;
    private final ValidarSessao validarSessao;

    public SessaoAutenticacaoInterceptor(JwtTokenService jwtTokenService, ValidarSessao validarSessao) {
        this.jwtTokenService = jwtTokenService;
        this.validarSessao = validarSessao;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (HttpMethod.POST.matches(request.getMethod()) && CAMINHO_LOGIN.equals(request.getRequestURI())) {
            return true;
        }

        String cabecalho = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (cabecalho == null || !cabecalho.startsWith(PREFIXO_BEARER)) {
            throw new SessaoInvalidaOuExpiradaException();
        }

        String token = cabecalho.substring(PREFIXO_BEARER.length());
        ClaimsSessao claims = jwtTokenService.decodificar(token);
        validarSessao.validar(claims.jti());
        ContextoAutenticacao.definir(claims);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ContextoAutenticacao.limpar();
    }
}
