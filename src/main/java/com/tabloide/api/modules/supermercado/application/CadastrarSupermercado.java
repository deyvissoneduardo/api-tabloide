package com.tabloide.api.modules.supermercado.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Cnpj;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.CnpjJaCadastradoException;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CadastrarSupermercado {

    private final SupermercadoRepository supermercadoRepository;
    private final AuditoriaRepository auditoriaRepository;

    public CadastrarSupermercado(SupermercadoRepository supermercadoRepository, AuditoriaRepository auditoriaRepository) {
        this.supermercadoRepository = supermercadoRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Supermercado executar(Cnpj cnpj, DadosSupermercado dados, Long atorId, Perfil perfilAtor) {
        if (supermercadoRepository.existePorCnpj(cnpj)) {
            throw new CnpjJaCadastradoException();
        }

        Instant agora = Instant.now();
        Supermercado supermercado = Supermercado.cadastrar(
                cnpj,
                dados.razaoSocial(),
                dados.nomeFantasia(),
                dados.emailComercial(),
                dados.telefoneComercial(),
                dados.endereco(),
                dados.complemento(),
                dados.logomarcaUrl(),
                dados.observacoesInternas(),
                agora
        );
        Supermercado salvo = supermercadoRepository.salvar(supermercado);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, salvo.id(), "SUPERMERCADO_CADASTRADO", "Supermercado", salvo.id(),
                null, salvo.resumoParaAuditoria(), agora
        ));

        return salvo;
    }
}
