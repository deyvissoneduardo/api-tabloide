package com.tabloide.api.modules.plano.interfaces.http;

import com.tabloide.api.modules.autenticacao.interfaces.http.dto.ErroResponse;
import com.tabloide.api.modules.plano.domain.exceptions.AssinaturaNaoEncontradaException;
import com.tabloide.api.modules.plano.domain.exceptions.AssinaturaVigenteJaExisteException;
import com.tabloide.api.modules.plano.domain.exceptions.NenhumaAssinaturaVigenteException;
import com.tabloide.api.modules.plano.domain.exceptions.PlanoNaoEncontradoException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErroPlanoHandler {

    @ExceptionHandler(PlanoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarPlanoNaoEncontrado(PlanoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(AssinaturaVigenteJaExisteException.class)
    public ResponseEntity<ErroResponse> tratarAssinaturaVigenteJaExiste(AssinaturaVigenteJaExisteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(NenhumaAssinaturaVigenteException.class)
    public ResponseEntity<ErroResponse> tratarNenhumaAssinaturaVigente(NenhumaAssinaturaVigenteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(AssinaturaNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> tratarAssinaturaNaoEncontrada(AssinaturaNaoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.unprocessableEntity().body(new ErroResponse(mensagem));
    }
}
