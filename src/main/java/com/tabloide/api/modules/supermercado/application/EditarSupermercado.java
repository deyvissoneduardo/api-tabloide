package com.tabloide.api.modules.supermercado.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import com.tabloide.api.modules.supermercado.domain.exceptions.VersaoDesatualizadaException;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EditarSupermercado {

    private final SupermercadoRepository supermercadoRepository;
    private final AuditoriaRepository auditoriaRepository;

    public EditarSupermercado(SupermercadoRepository supermercadoRepository, AuditoriaRepository auditoriaRepository) {
        this.supermercadoRepository = supermercadoRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Supermercado executar(Long id, Long versaoConhecida, DadosSupermercado dados, Long atorId, Perfil perfilAtor) {
        Supermercado supermercado = supermercadoRepository.buscarPorId(id)
                .orElseThrow(SupermercadoNaoEncontradoException::new);

        if (!supermercado.possuiVersao(versaoConhecida)) {
            throw new VersaoDesatualizadaException();
        }

        String antes = supermercado.resumoParaAuditoria();
        Instant agora = Instant.now();

        supermercado.editarDados(
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
                null, atorId, perfilAtor, salvo.id(), "SUPERMERCADO_EDITADO", "Supermercado", salvo.id(),
                antes, salvo.resumoParaAuditoria(), agora
        ));

        return salvo;
    }
}
