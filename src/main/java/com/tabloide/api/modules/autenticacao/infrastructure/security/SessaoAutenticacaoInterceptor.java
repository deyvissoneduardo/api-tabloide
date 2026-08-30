package com.tabloide.api.modules.autenticacao.infrastructure.security;

import com.tabloide.api.modules.autenticacao.application.ValidarSessao;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.AcessoNegadoException;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
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
        validarPerfilAutorizado(handler, claims.perfil());
        ContextoAutenticacao.definir(claims);
        return true;
    }

    private void validarPerfilAutorizado(Object handler, Perfil perfil) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return;
        }

        RequerPerfil requerPerfil = handlerMethod.getMethodAnnotation(RequerPerfil.class);
        if (requerPerfil != null && !perfilPermitido(requerPerfil, perfil)) {
            throw new AcessoNegadoException();
        }
    }

    private boolean perfilPermitido(RequerPerfil requerPerfil, Perfil perfil) {
        return Arrays.asList(requerPerfil.value()).contains(perfil);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ContextoAutenticacao.limpar();
    }
}
