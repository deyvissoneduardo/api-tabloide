package com.tabloide.api.modules.autenticacao.infrastructure.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final SessaoAutenticacaoInterceptor interceptor;

    public WebMvcConfig(SessaoAutenticacaoInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/v1/health",
                        "/api/redefinicoes-senha",
                        "/api/redefinicoes-senha/**"
                );
    }
}
