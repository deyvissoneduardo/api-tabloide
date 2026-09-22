package com.tabloide.api.modules.tabloide.interfaces.http;

import com.tabloide.api.modules.autenticacao.interfaces.http.dto.ErroResponse;
import com.tabloide.api.modules.tabloide.domain.exceptions.ArquivoTabloideInvalidoException;
import com.tabloide.api.modules.tabloide.domain.exceptions.LojasTabloideInvalidasException;
import com.tabloide.api.modules.tabloide.domain.exceptions.PeriodoTabloideInvalidoException;
import com.tabloide.api.modules.tabloide.domain.exceptions.TituloTabloideInvalidoException;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErroTabloideHandler {

    @ExceptionHandler(TituloTabloideInvalidoException.class)
    public ResponseEntity<ErroResponse> tratarTituloInvalido(TituloTabloideInvalidoException ex) {
        return ResponseEntity.unprocessableContent().body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(LojasTabloideInvalidasException.class)
    public ResponseEntity<ErroResponse> tratarLojasInvalidas(LojasTabloideInvalidasException ex) {
        return ResponseEntity.unprocessableContent().body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(PeriodoTabloideInvalidoException.class)
    public ResponseEntity<ErroResponse> tratarPeriodoInvalido(PeriodoTabloideInvalidoException ex) {
        return ResponseEntity.unprocessableContent().body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(ArquivoTabloideInvalidoException.class)
    public ResponseEntity<ErroResponse> tratarArquivoInvalido(ArquivoTabloideInvalidoException ex) {
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
