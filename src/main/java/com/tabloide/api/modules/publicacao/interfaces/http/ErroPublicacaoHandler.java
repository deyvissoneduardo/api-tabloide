package com.tabloide.api.modules.publicacao.interfaces.http;

import com.tabloide.api.modules.autenticacao.interfaces.http.dto.ErroResponse;
import com.tabloide.api.modules.publicacao.domain.exceptions.RecursoPublicoIndisponivelException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErroPublicacaoHandler {

    @ExceptionHandler(RecursoPublicoIndisponivelException.class)
    public ResponseEntity<ErroResponse> tratarIndisponivel(RecursoPublicoIndisponivelException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResponse(ex.getMessage()));
    }
}
