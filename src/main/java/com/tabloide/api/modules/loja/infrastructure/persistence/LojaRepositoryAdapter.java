package com.tabloide.api.modules.loja.infrastructure.persistence;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.loja.domain.EstadoLoja;
import com.tabloide.api.modules.loja.domain.Loja;
import com.tabloide.api.modules.loja.domain.LojaRepository;
import com.tabloide.api.modules.supermercado.domain.Endereco;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class LojaRepositoryAdapter implements LojaRepository {

    private final LojaJpaRepository jpaRepository;

    public LojaRepositoryAdapter(LojaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Loja> buscarPorIdESupermercado(Long id, Long supermercadoId) {
        return jpaRepository.findByIdAndSupermercadoId(id, supermercadoId).map(LojaRepositoryAdapter::paraDominio);
    }

    @Override
    public boolean existeNomeNormalizadoNoSupermercado(String nomeNormalizado, Long supermercadoId) {
        return jpaRepository.existsByNomeNormalizadoAndSupermercadoId(nomeNormalizado, supermercadoId);
    }

    @Override
    public long contarAtivasPorSupermercado(Long supermercadoId) {
        return jpaRepository.countBySupermercadoIdAndEstado(supermercadoId, EstadoLoja.ATIVA);
    }

    @Override
    public Loja salvar(Loja loja) {
        LojaJpaEntity entidade = loja.id() == null
                ? new LojaJpaEntity(
                        null,
                        loja.supermercadoId(),
                        loja.nome(),
                        loja.nomeNormalizado(),
                        loja.endereco().cep(),
                        loja.endereco().logradouro(),
                        loja.endereco().numero(),
                        loja.endereco().bairro(),
                        loja.endereco().municipio(),
                        loja.endereco().uf(),
                        loja.complemento(),
                        loja.estado(),
                        null,
                        loja.criadoEm(),
                        loja.atualizadoEm()
                )
                : atualizar(jpaRepository.findById(loja.id()).orElseThrow(() ->
                        new IllegalStateException("Loja " + loja.id() + " não encontrada para atualização")), loja);

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public Pagina<Loja> listarPorSupermercado(Long supermercadoId, int pagina, int tamanho) {
        PageRequest paginacao = PageRequest.of(pagina, tamanho, Sort.by(Sort.Direction.ASC, "nome"));
        Page<LojaJpaEntity> resultado = jpaRepository.findBySupermercadoId(supermercadoId, paginacao);
        return new Pagina<>(
                resultado.getContent().stream().map(LojaRepositoryAdapter::paraDominio).toList(),
                pagina,
                tamanho,
                resultado.getTotalElements(),
                resultado.getTotalPages()
        );
    }

    private static LojaJpaEntity atualizar(LojaJpaEntity entidade, Loja loja) {
        entidade.setNome(loja.nome());
        entidade.setNomeNormalizado(loja.nomeNormalizado());
        entidade.setCep(loja.endereco().cep());
        entidade.setLogradouro(loja.endereco().logradouro());
        entidade.setNumero(loja.endereco().numero());
        entidade.setBairro(loja.endereco().bairro());
        entidade.setMunicipio(loja.endereco().municipio());
        entidade.setUf(loja.endereco().uf());
        entidade.setComplemento(loja.complemento());
        entidade.setEstado(loja.estado());
        entidade.setAtualizadoEm(loja.atualizadoEm());
        return entidade;
    }

    private static Loja paraDominio(LojaJpaEntity entidade) {
        return new Loja(
                entidade.getId(),
                entidade.getSupermercadoId(),
                entidade.getNome(),
                entidade.getNomeNormalizado(),
                new Endereco(
                        entidade.getCep(),
                        entidade.getLogradouro(),
                        entidade.getNumero(),
                        entidade.getBairro(),
                        entidade.getMunicipio(),
                        entidade.getUf()
                ),
                entidade.getComplemento(),
                entidade.getEstado(),
                entidade.getVersao(),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm()
        );
    }
}
