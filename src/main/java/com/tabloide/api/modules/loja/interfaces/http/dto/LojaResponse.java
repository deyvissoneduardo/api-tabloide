package com.tabloide.api.modules.loja.interfaces.http.dto;

import com.tabloide.api.modules.loja.domain.EstadoLoja;
import com.tabloide.api.modules.loja.domain.Loja;
import java.time.Instant;

public record LojaResponse(
        Long id,
        Long supermercadoId,
        String nome,
        String cep,
        String logradouro,
        String numero,
        String bairro,
        String municipio,
        String uf,
        String complemento,
        EstadoLoja estado,
        Long versao,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static LojaResponse from(Loja loja) {
        return new LojaResponse(
                loja.id(),
                loja.supermercadoId(),
                loja.nome(),
                loja.endereco().cep(),
                loja.endereco().logradouro(),
                loja.endereco().numero(),
                loja.endereco().bairro(),
                loja.endereco().municipio(),
                loja.endereco().uf(),
                loja.complemento(),
                loja.estado(),
                loja.versao(),
                loja.criadoEm(),
                loja.atualizadoEm()
        );
    }
}
