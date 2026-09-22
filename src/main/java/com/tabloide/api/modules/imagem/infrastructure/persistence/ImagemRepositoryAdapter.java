package com.tabloide.api.modules.imagem.infrastructure.persistence;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.imagem.domain.FiltroImagem;
import com.tabloide.api.modules.imagem.domain.Imagem;
import com.tabloide.api.modules.imagem.domain.ImagemRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    public Pagina<Imagem> listarPorSupermercado(Long supermercadoId, FiltroImagem filtro, int pagina, int tamanho) {
        PageRequest paginacao = PageRequest.of(pagina, tamanho, Sort.by(Sort.Direction.DESC, "uploadEm"));
        Page<ImagemJpaEntity> resultado = jpaRepository.findAll(especificacao(supermercadoId, filtro), paginacao);
        return new Pagina<>(
                resultado.getContent().stream().map(ImagemRepositoryAdapter::paraDominio).toList(),
                pagina,
                tamanho,
                resultado.getTotalElements(),
                resultado.getTotalPages()
        );
    }

    private static Specification<ImagemJpaEntity> especificacao(Long supermercadoId, FiltroImagem filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicados = new ArrayList<>();
            predicados.add(cb.equal(root.get("supermercadoId"), supermercadoId));
            if (filtro.nomeBusca() != null && !filtro.nomeBusca().isBlank()) {
                predicados.add(cb.like(cb.lower(root.get("nomeBusca")), "%" + filtro.nomeBusca().toLowerCase() + "%"));
            }
            if (filtro.dataInicio() != null) {
                predicados.add(cb.greaterThanOrEqualTo(root.get("uploadEm"), filtro.dataInicio()));
            }
            if (filtro.dataFim() != null) {
                predicados.add(cb.lessThanOrEqualTo(root.get("uploadEm"), filtro.dataFim()));
            }
            return cb.and(predicados.toArray(new Predicate[0]));
        };
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
