package com.tabloide.api.modules.oferta.interfaces.http;

import com.tabloide.api.modules.autenticacao.interfaces.http.dto.ErroResponse;
import com.tabloide.api.modules.oferta.domain.exceptions.LojasOfertaInvalidasException;
import com.tabloide.api.modules.oferta.domain.exceptions.PeriodoOfertaInvalidoException;
import com.tabloide.api.modules.oferta.domain.exceptions.PrecoOfertaInvalidoException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErroOfertaHandler {

    @ExceptionHandler(LojasOfertaInvalidasException.class)
    public ResponseEntity<ErroResponse> tratarLojasInvalidas(LojasOfertaInvalidasException ex) {
        return ResponseEntity.unprocessableEntity().body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(PrecoOfertaInvalidoException.class)
    public ResponseEntity<ErroResponse> tratarPrecoInvalido(PrecoOfertaInvalidoException ex) {
        return ResponseEntity.unprocessableEntity().body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(PeriodoOfertaInvalidoException.class)
    public ResponseEntity<ErroResponse> tratarPeriodoInvalido(PeriodoOfertaInvalidoException ex) {
        return ResponseEntity.unprocessableEntity().body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErroResponse> tratarConflitoDeConcorrencia(ObjectOptimisticLockingFailureException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse("Versão do recurso está desatualizada"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.unprocessableEntity().body(new ErroResponse(mensagem));
    }
}
