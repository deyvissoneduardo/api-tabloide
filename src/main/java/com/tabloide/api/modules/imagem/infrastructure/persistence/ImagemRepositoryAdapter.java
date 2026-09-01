package com.tabloide.api.modules.imagem.infrastructure.persistence;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.imagem.domain.Imagem;
import com.tabloide.api.modules.imagem.domain.ImagemRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class ImagemRepositoryAdapter implements ImagemRepository {

    private final ImagemJpaRepository jpaRepository;

    public ImagemRepositoryAdapter(ImagemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Imagem> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(ImagemRepositoryAdapter::paraDominio);
    }

    @Override
    public long contarAtivasPorSupermercado(Long supermercadoId) {
        return jpaRepository.countBySupermercadoIdAndExcluidoEmIsNull(supermercadoId);
    }

    @Override
    public Imagem salvar(Imagem imagem) {
        ImagemJpaEntity entidade = imagem.id() == null
                ? new ImagemJpaEntity(
                        null,
                        imagem.supermercadoId(),
                        imagem.nomeBusca(),
                        imagem.tipoVinculo(),
                        imagem.vinculoId(),
                        imagem.urlOuChave(),
                        imagem.formato(),
                        imagem.tamanhoBytes(),
                        imagem.uploadEm(),
                        imagem.excluidoEm()
                )
                : atualizar(jpaRepository.findById(imagem.id()).orElseThrow(() ->
                        new IllegalStateException("Imagem " + imagem.id() + " não encontrada para atualização")), imagem);

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public Pagina<Imagem> listarPorSupermercado(Long supermercadoId, String nomeBusca, int pagina, int tamanho) {
        PageRequest paginacao = PageRequest.of(pagina, tamanho, Sort.by(Sort.Direction.DESC, "uploadEm"));
        Page<ImagemJpaEntity> resultado = (nomeBusca == null || nomeBusca.isBlank())
                ? jpaRepository.findBySupermercadoId(supermercadoId, paginacao)
                : jpaRepository.findBySupermercadoIdAndNomeBuscaContainingIgnoreCase(supermercadoId, nomeBusca, paginacao);
        return new Pagina<>(
                resultado.getContent().stream().map(ImagemRepositoryAdapter::paraDominio).toList(),
                pagina,
                tamanho,
                resultado.getTotalElements(),
                resultado.getTotalPages()
        );
    }

    private static ImagemJpaEntity atualizar(ImagemJpaEntity entidade, Imagem imagem) {
        entidade.setVinculoId(imagem.vinculoId());
        entidade.setExcluidoEm(imagem.excluidoEm());
        return entidade;
    }

    private static Imagem paraDominio(ImagemJpaEntity entidade) {
        return new Imagem(
                entidade.getId(),
                entidade.getSupermercadoId(),
                entidade.getNomeBusca(),
                entidade.getTipoVinculo(),
                entidade.getVinculoId(),
                entidade.getUrlOuChave(),
                entidade.getFormato(),
                entidade.getTamanhoBytes(),
                entidade.getUploadEm(),
                entidade.getExcluidoEm()
        );
    }
}
