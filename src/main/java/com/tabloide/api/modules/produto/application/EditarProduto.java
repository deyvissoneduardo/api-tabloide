package com.tabloide.api.modules.produto.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.produto.domain.Produto;
import com.tabloide.api.modules.produto.domain.ProdutoRepository;
import com.tabloide.api.modules.produto.domain.exceptions.ProdutoNaoEncontradoException;
import com.tabloide.api.modules.produto.domain.exceptions.VersaoProdutoDesatualizadaException;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.SupermercadoNaoEncontradoException;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EditarProduto {

    private final ProdutoRepository produtoRepository;
    private final AuditoriaRepository auditoriaRepository;
    private final SupermercadoRepository supermercadoRepository;

    public EditarProduto(ProdutoRepository produtoRepository, AuditoriaRepository auditoriaRepository,
                          SupermercadoRepository supermercadoRepository) {
        this.produtoRepository = produtoRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.supermercadoRepository = supermercadoRepository;
    }

    @Transactional
    public Produto executar(
            Long supermercadoId, Long produtoId, Long versaoConhecida, DadosProduto dados,
            Long atorId, Perfil perfilAtor, Long supermercadoIdAtor
    ) {
        validarEscopo(supermercadoId, supermercadoIdAtor);
        validarSupermercado(supermercadoId);

        Produto produto = produtoRepository.buscarPorIdESupermercado(produtoId, supermercadoId)
                .orElseThrow(ProdutoNaoEncontradoException::new);
        validarVersao(produto, versaoConhecida);

        String antes = produto.resumoParaAuditoria();
        Instant agora = Instant.now();
        produto.editar(dados.nome(), dados.marca(), dados.descricao(), dados.peso(), dados.unidade(), dados.volume(), agora);
        Produto salvo = produtoRepository.salvar(produto);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, supermercadoId, "PRODUTO_EDITADO", "Produto", salvo.id(),
                antes, salvo.resumoParaAuditoria(), agora));

        return salvo;
    }

    private void validarEscopo(Long supermercadoId, Long supermercadoIdAtor) {
        if (!Objects.equals(supermercadoId, supermercadoIdAtor)) {
            throw new ProdutoNaoEncontradoException();
        }
    }

    private void validarSupermercado(Long supermercadoId) {
        Supermercado supermercado = supermercadoRepository.buscarPorId(supermercadoId)
                .orElseThrow(SupermercadoNaoEncontradoException::new);
        supermercado.validarPermissaoParaMutacao();
    }

    private void validarVersao(Produto produto, Long versaoConhecida) {
        if (!produto.possuiVersao(versaoConhecida)) {
            throw new VersaoProdutoDesatualizadaException();
        }
    }
}
