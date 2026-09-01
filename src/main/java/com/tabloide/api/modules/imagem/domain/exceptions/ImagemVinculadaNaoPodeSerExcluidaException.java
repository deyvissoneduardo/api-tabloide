package com.tabloide.api.modules.imagem.domain.exceptions;

public class ImagemVinculadaNaoPodeSerExcluidaException extends RuntimeException {

    public ImagemVinculadaNaoPodeSerExcluidaException() {
        super("Imagem vinculada não pode ser excluída até que o vínculo seja removido");
    }
}
