package com.tabloide.api.modules.conteudopromocional.interfaces.http;

import com.tabloide.api.modules.autenticacao.interfaces.http.dto.ErroResponse;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.ConteudoPromocionalNaoEncontradoException;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.DestinoBannerInvalidoException;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.LojasConteudoPromocionalInvalidasException;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.NivelAvisoInvalidoException;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.PeriodoConteudoPromocionalInvalidoException;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.PosicoesConteudoPromocionalInvalidasException;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.TextoConteudoPromocionalInvalidoException;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.TituloConteudoPromocionalInvalidoException;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.VersaoConteudoPromocionalDesatualizadaException;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErroConteudoPromocionalHandler {

    @ExceptionHandler(TituloConteudoPromocionalInvalidoException.class)
    public ResponseEntity<ErroResponse> tratarTituloInvalido(TituloConteudoPromocionalInvalidoException ex) {
        return ResponseEntity.unprocessableContent().body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(TextoConteudoPromocionalInvalidoException.class)
    public ResponseEntity<ErroResponse> tratarTextoInvalido(TextoConteudoPromocionalInvalidoException ex) {
        return ResponseEntity.unprocessableContent().body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(NivelAvisoInvalidoException.class)
    public ResponseEntity<ErroResponse> tratarNivelInvalido(NivelAvisoInvalidoException ex) {
        return ResponseEntity.unprocessableContent().body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(DestinoBannerInvalidoException.class)
    public ResponseEntity<ErroResponse> tratarDestinoInvalido(DestinoBannerInvalidoException ex) {
        return ResponseEntity.unprocessableContent().body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(LojasConteudoPromocionalInvalidasException.class)
    public ResponseEntity<ErroResponse> tratarLojasInvalidas(LojasConteudoPromocionalInvalidasException ex) {
        return ResponseEntity.unprocessableContent().body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(PeriodoConteudoPromocionalInvalidoException.class)
    public ResponseEntity<ErroResponse> tratarPeriodoInvalido(PeriodoConteudoPromocionalInvalidoException ex) {
        return ResponseEntity.unprocessableContent().body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(PosicoesConteudoPromocionalInvalidasException.class)
    public ResponseEntity<ErroResponse> tratarPosicoesInvalidas(PosicoesConteudoPromocionalInvalidasException ex) {
        return ResponseEntity.unprocessableContent().body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(ConteudoPromocionalNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarNaoEncontrado(ConteudoPromocionalNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(VersaoConteudoPromocionalDesatualizadaException.class)
    public ResponseEntity<ErroResponse> tratarVersaoDesatualizada(VersaoConteudoPromocionalDesatualizadaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErroResponse> tratarConflitoDeConcorrencia(ObjectOptimisticLockingFailureException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse("Versão do recurso está desatualizada"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> tratarConflitoDeIntegridade(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse("Conflito ao salvar o conteúdo promocional"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.unprocessableContent().body(new ErroResponse(mensagem));
    }
}
