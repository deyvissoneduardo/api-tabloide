package com.tabloide.api.modules.autenticacao.interfaces.http;

import com.tabloide.api.modules.autenticacao.domain.exceptions.AcessoNegadoException;
import com.tabloide.api.modules.autenticacao.domain.exceptions.CredenciaisInvalidasException;
import com.tabloide.api.modules.autenticacao.domain.exceptions.DadosRedefinicaoNaoConferemException;
import com.tabloide.api.modules.autenticacao.domain.exceptions.EmailJaCadastradoException;
import com.tabloide.api.modules.autenticacao.domain.exceptions.PerfilInvalidoParaCadastroException;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoInvalidaOuExpiradaException;
import com.tabloide.api.modules.autenticacao.domain.exceptions.SessaoNaoEncontradaException;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TokenRedefinicaoInvalidoOuExpiradoException;
import com.tabloide.api.modules.autenticacao.interfaces.http.dto.ErroResponse;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErroAutenticacaoHandler {

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<ErroResponse> tratarAcessoNegado(AcessoNegadoException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(SessaoNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> tratarSessaoNaoEncontrada(SessaoNaoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(TamanhoPaginaInvalidoException.class)
    public ResponseEntity<ErroResponse> tratarTamanhoPaginaInvalido(TamanhoPaginaInvalidoException ex) {
        return ResponseEntity.unprocessableEntity().body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErroResponse> tratarCredenciaisInvalidas(CredenciaisInvalidasException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(SessaoInvalidaOuExpiradaException.class)
    public ResponseEntity<ErroResponse> tratarSessaoInvalida(SessaoInvalidaOuExpiradaException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(DadosRedefinicaoNaoConferemException.class)
    public ResponseEntity<ErroResponse> tratarDadosNaoConferem(DadosRedefinicaoNaoConferemException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(TokenRedefinicaoInvalidoOuExpiradoException.class)
    public ResponseEntity<ErroResponse> tratarTokenInvalido(TokenRedefinicaoInvalidoOuExpiradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ErroResponse> tratarEmailDuplicado(EmailJaCadastradoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(PerfilInvalidoParaCadastroException.class)
    public ResponseEntity<ErroResponse> tratarPerfilInvalido(PerfilInvalidoParaCadastroException ex) {
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
