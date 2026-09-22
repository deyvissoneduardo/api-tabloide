package com.tabloide.api.modules.plano.interfaces.http;

import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.ErroResponse;
import com.tabloide.api.modules.plano.domain.exceptions.AssinaturaNaoEncontradaException;
import com.tabloide.api.modules.plano.domain.exceptions.AssinaturaVigenteJaExisteException;
import com.tabloide.api.modules.plano.domain.exceptions.NenhumaAssinaturaVigenteException;
import com.tabloide.api.modules.plano.domain.exceptions.NomeDePlanoJaCadastradoException;
import com.tabloide.api.modules.plano.domain.exceptions.PlanoExcluidoException;
import com.tabloide.api.modules.plano.domain.exceptions.PlanoNaoEncontradoException;
import com.tabloide.api.modules.plano.domain.exceptions.TransicaoEstadoInvalidaException;
import com.tabloide.api.modules.plano.domain.exceptions.VersaoDesatualizadaException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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

    @ExceptionHandler(NomeDePlanoJaCadastradoException.class)
    public ResponseEntity<ErroResponse> tratarNomeDuplicado(NomeDePlanoJaCadastradoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(PlanoExcluidoException.class)
    public ResponseEntity<ErroResponse> tratarPlanoExcluido(PlanoExcluidoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(TransicaoEstadoInvalidaException.class)
    public ResponseEntity<ErroResponse> tratarTransicaoInvalida(TransicaoEstadoInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(VersaoDesatualizadaException.class)
    public ResponseEntity<ErroResponse> tratarVersaoDesatualizada(VersaoDesatualizadaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErroResponse> tratarConflitoDeConcorrencia(ObjectOptimisticLockingFailureException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse("Versão do recurso está desatualizada"));
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
