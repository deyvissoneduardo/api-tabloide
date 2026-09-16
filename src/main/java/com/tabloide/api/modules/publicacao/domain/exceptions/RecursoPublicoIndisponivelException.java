package com.tabloide.api.modules.publicacao.domain.exceptions;

// Loja/supermercado/oferta inexistentes ou indisponíveis para consulta pública (RN-004 de Página pública).
public class RecursoPublicoIndisponivelException extends RuntimeException {

    public RecursoPublicoIndisponivelException() {
        super("Conteúdo indisponível");
    }
}
