package com.tabloide.api.modules.imagem.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.imagem.domain.Imagem;
import com.tabloide.api.modules.imagem.domain.ImagemRepository;
import com.tabloide.api.modules.imagem.domain.exceptions.ImagemNaoEncontradaException;
import com.tabloide.api.modules.imagem.domain.exceptions.ImagemVinculadaNaoPodeSerExcluidaException;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ExcluirImagem {

    private final ImagemRepository imagemRepository;
    private final AuditoriaRepository auditoriaRepository;

    public ExcluirImagem(ImagemRepository imagemRepository, AuditoriaRepository auditoriaRepository) {
        this.imagemRepository = imagemRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Imagem executar(Long id, Long atorId, Perfil perfilAtor, Long supermercadoIdAtor) {
        Imagem imagem = imagemRepository.buscarPorId(id).orElseThrow(ImagemNaoEncontradaException::new);
        if (!Objects.equals(imagem.supermercadoId(), supermercadoIdAtor)) {
            throw new ImagemNaoEncontradaException();
        }
        if (imagem.estaVinculada()) {
            throw new ImagemVinculadaNaoPodeSerExcluidaException();
        }
        if (!imagem.estaAtiva()) {
            return imagem;
        }

        String antes = imagem.resumoParaAuditoria();
        Instant agora = Instant.now();
        imagem.excluir(agora);
        Imagem salva = imagemRepository.salvar(imagem);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, supermercadoIdAtor, "IMAGEM_EXCLUIDA", "Imagem", salva.id(),
                antes, salva.resumoParaAuditoria(), agora
        ));

        return salva;
    }
}
