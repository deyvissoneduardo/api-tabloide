package com.tabloide.api.modules.auditoria.interfaces.http;

import com.tabloide.api.modules.auditoria.domain.exceptions.RegistroAuditoriaNaoEncontradoException;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.ErroResponse;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErroAuditoriaHandler {

    @ExceptionHandler(RegistroAuditoriaNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarNaoEncontrado(RegistroAuditoriaNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(TamanhoPaginaInvalidoException.class)
    public ResponseEntity<ErroResponse> tratarTamanhoPaginaInvalido(TamanhoPaginaInvalidoException ex) {
        return ResponseEntity.unprocessableContent().body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.unprocessableContent().body(new ErroResponse(mensagem));
    }
}
