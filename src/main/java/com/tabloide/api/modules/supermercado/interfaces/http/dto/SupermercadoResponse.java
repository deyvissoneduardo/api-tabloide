package com.tabloide.api.modules.supermercado.interfaces.http.dto;

import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import java.time.Instant;

public record SupermercadoResponse(
        Long id,
        String cnpj,
        String razaoSocial,
        String nomeFantasia,
        String emailComercial,
        String telefoneComercial,
        String cep,
        String logradouro,
        String numero,
        String bairro,
        String municipio,
        String uf,
        String complemento,
        String logomarcaUrl,
        String observacoesInternas,
        EstadoSupermercado estado,
        Long versao,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static SupermercadoResponse from(Supermercado supermercado) {
        return new SupermercadoResponse(
                supermercado.id(),
                supermercado.cnpj().valor(),
                supermercado.razaoSocial(),
                supermercado.nomeFantasia(),
                supermercado.emailComercial(),
                supermercado.telefoneComercial(),
                supermercado.endereco().cep(),
                supermercado.endereco().logradouro(),
                supermercado.endereco().numero(),
                supermercado.endereco().bairro(),
                supermercado.endereco().municipio(),
                supermercado.endereco().uf(),
                supermercado.complemento(),
                supermercado.logomarcaUrl(),
                supermercado.observacoesInternas(),
                supermercado.estado(),
                supermercado.versao(),
                supermercado.criadoEm(),
                supermercado.atualizadoEm()
        );
    }
}
