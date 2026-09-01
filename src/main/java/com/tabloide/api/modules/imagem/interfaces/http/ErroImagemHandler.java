package com.tabloide.api.modules.imagem.interfaces.http;

import com.tabloide.api.modules.autenticacao.interfaces.http.dto.ErroResponse;
import com.tabloide.api.modules.imagem.domain.exceptions.CotaDeImagensExcedidaException;
import com.tabloide.api.modules.imagem.domain.exceptions.FormatoOuTamanhoInvalidoException;
import com.tabloide.api.modules.imagem.domain.exceptions.ImagemNaoEncontradaException;
import com.tabloide.api.modules.imagem.domain.exceptions.ImagemVinculadaNaoPodeSerExcluidaException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErroImagemHandler {

    @ExceptionHandler(ImagemNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> tratarNaoEncontrada(ImagemNaoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(CotaDeImagensExcedidaException.class)
    public ResponseEntity<ErroResponse> tratarCotaExcedida(CotaDeImagensExcedidaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(ImagemVinculadaNaoPodeSerExcluidaException.class)
    public ResponseEntity<ErroResponse> tratarImagemVinculada(ImagemVinculadaNaoPodeSerExcluidaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(FormatoOuTamanhoInvalidoException.class)
    public ResponseEntity<ErroResponse> tratarFormatoOuTamanhoInvalido(FormatoOuTamanhoInvalidoException ex) {
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
