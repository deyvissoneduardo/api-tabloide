package com.tabloide.api.modules.loja.interfaces.http;

import com.tabloide.api.modules.autenticacao.interfaces.http.dto.ErroResponse;
import com.tabloide.api.modules.loja.domain.exceptions.LimiteDeLojasDoPlanoAtingidoException;
import com.tabloide.api.modules.loja.domain.exceptions.LojaNaoEncontradaException;
import com.tabloide.api.modules.loja.domain.exceptions.NomeDeLojaJaCadastradoException;
import com.tabloide.api.modules.loja.domain.exceptions.TransicaoEstadoInvalidaException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErroLojaHandler {

    @ExceptionHandler(LojaNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> tratarNaoEncontrada(LojaNaoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(NomeDeLojaJaCadastradoException.class)
    public ResponseEntity<ErroResponse> tratarNomeDuplicado(NomeDeLojaJaCadastradoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(TransicaoEstadoInvalidaException.class)
    public ResponseEntity<ErroResponse> tratarTransicaoInvalida(TransicaoEstadoInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(LimiteDeLojasDoPlanoAtingidoException.class)
    public ResponseEntity<ErroResponse> tratarLimiteAtingido(LimiteDeLojasDoPlanoAtingidoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(ex.getMessage()));
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
