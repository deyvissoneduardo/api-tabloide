package com.tabloide.api.modules.autenticacao.infrastructure.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final SessaoAutenticacaoInterceptor interceptor;
    private final AuditoriaDeLeituraInterceptor auditoriaDeLeituraInterceptor;

    public WebMvcConfig(SessaoAutenticacaoInterceptor interceptor, AuditoriaDeLeituraInterceptor auditoriaDeLeituraInterceptor) {
        this.interceptor = interceptor;
        this.auditoriaDeLeituraInterceptor = auditoriaDeLeituraInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] caminhosExcluidos = {
                "/api/v1/health",
                "/api/redefinicoes-senha",
                "/api/redefinicoes-senha/**"
        };

        registry.addInterceptor(interceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(caminhosExcluidos);

        // Registrado depois do SessaoAutenticacaoInterceptor: o Spring executa afterCompletion na
        // ordem inversa de registro, então este interceptor roda antes do ContextoAutenticacao.limpar().
        registry.addInterceptor(auditoriaDeLeituraInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(caminhosExcluidos);
    }
}
