package com.tabloide.api.modules.conteudogeral.interfaces.http;

import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.ErroResponse;
import com.tabloide.api.modules.conteudogeral.domain.exceptions.ConteudoGeralNaoEncontradoException;
import com.tabloide.api.modules.conteudogeral.domain.exceptions.TransicaoEstadoConteudoGeralInvalidaException;
import com.tabloide.api.modules.conteudogeral.domain.exceptions.VersaoConteudoGeralDesatualizadaException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErroConteudoGeralHandler {

    @ExceptionHandler(ConteudoGeralNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarNaoEncontrado(ConteudoGeralNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(TransicaoEstadoConteudoGeralInvalidaException.class)
    public ResponseEntity<ErroResponse> tratarTransicaoInvalida(TransicaoEstadoConteudoGeralInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(VersaoConteudoGeralDesatualizadaException.class)
    public ResponseEntity<ErroResponse> tratarVersaoDesatualizada(VersaoConteudoGeralDesatualizadaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErroResponse> tratarConflitoDeConcorrencia(ObjectOptimisticLockingFailureException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse("Versão do recurso está desatualizada"));
    }

    @ExceptionHandler(TamanhoPaginaInvalidoException.class)
    public ResponseEntity<ErroResponse> tratarTamanhoPaginaInvalido(TamanhoPaginaInvalidoException ex) {
        return ResponseEntity.unprocessableEntity().body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.unprocessableEntity().body(new ErroResponse(mensagem));
    }
}
