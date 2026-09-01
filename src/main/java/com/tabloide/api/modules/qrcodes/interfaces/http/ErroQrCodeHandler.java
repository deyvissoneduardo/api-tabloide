package com.tabloide.api.modules.qrcodes.interfaces.http;

import com.tabloide.api.modules.autenticacao.interfaces.http.dto.ErroResponse;
import com.tabloide.api.modules.qrcodes.domain.exceptions.LojaIndisponivelParaQrCodeException;
import com.tabloide.api.modules.qrcodes.domain.exceptions.NomeDeQrCodeJaCadastradoException;
import com.tabloide.api.modules.qrcodes.domain.exceptions.QrCodeNaoEncontradoException;
import com.tabloide.api.modules.qrcodes.domain.exceptions.TransicaoEstadoQrCodeInvalidaException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErroQrCodeHandler {

    @ExceptionHandler(QrCodeNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarNaoEncontrado(QrCodeNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(NomeDeQrCodeJaCadastradoException.class)
    public ResponseEntity<ErroResponse> tratarNomeDuplicado(NomeDeQrCodeJaCadastradoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(TransicaoEstadoQrCodeInvalidaException.class)
    public ResponseEntity<ErroResponse> tratarTransicaoInvalida(TransicaoEstadoQrCodeInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(LojaIndisponivelParaQrCodeException.class)
    public ResponseEntity<ErroResponse> tratarLojaIndisponivel(LojaIndisponivelParaQrCodeException ex) {
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
