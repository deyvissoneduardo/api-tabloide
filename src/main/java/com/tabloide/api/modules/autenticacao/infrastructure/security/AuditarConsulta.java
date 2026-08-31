package com.tabloide.api.modules.autenticacao.infrastructure.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditarConsulta {

    String acao();

    String entidade();

    String paramEntidadeId() default "";

    String paramSupermercadoId() default "";
}
