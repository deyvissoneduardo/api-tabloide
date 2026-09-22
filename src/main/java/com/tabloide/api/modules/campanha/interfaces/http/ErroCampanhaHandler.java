package com.tabloide.api.modules.campanha.interfaces.http;

import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.ErroResponse;
import com.tabloide.api.modules.campanha.domain.exceptions.CampanhaNaoEncontradaException;
import com.tabloide.api.modules.campanha.domain.exceptions.LojasCampanhaInvalidasException;
import com.tabloide.api.modules.campanha.domain.exceptions.OfertaJaVinculadaAOutraCampanhaException;
import com.tabloide.api.modules.campanha.domain.exceptions.PeriodoCampanhaInvalidoException;
import com.tabloide.api.modules.campanha.domain.exceptions.TransicaoEstadoCampanhaInvalidaException;
import com.tabloide.api.modules.campanha.domain.exceptions.VersaoCampanhaDesatualizadaException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErroCampanhaHandler {

    @ExceptionHandler(CampanhaNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> tratarNaoEncontrada(CampanhaNaoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(LojasCampanhaInvalidasException.class)
    public ResponseEntity<ErroResponse> tratarLojasInvalidas(LojasCampanhaInvalidasException ex) {
        return ResponseEntity.unprocessableContent().body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(PeriodoCampanhaInvalidoException.class)
    public ResponseEntity<ErroResponse> tratarPeriodoInvalido(PeriodoCampanhaInvalidoException ex) {
        return ResponseEntity.unprocessableContent().body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(OfertaJaVinculadaAOutraCampanhaException.class)
    public ResponseEntity<ErroResponse> tratarOfertaJaVinculada(OfertaJaVinculadaAOutraCampanhaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(TransicaoEstadoCampanhaInvalidaException.class)
    public ResponseEntity<ErroResponse> tratarTransicaoInvalida(TransicaoEstadoCampanhaInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(VersaoCampanhaDesatualizadaException.class)
    public ResponseEntity<ErroResponse> tratarVersaoDesatualizada(VersaoCampanhaDesatualizadaException ex) {
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
