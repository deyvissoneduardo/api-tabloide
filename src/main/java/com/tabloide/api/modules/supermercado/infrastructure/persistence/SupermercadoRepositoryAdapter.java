package com.tabloide.api.modules.supermercado.infrastructure.persistence;

import com.tabloide.api.modules.autenticacao.domain.Cnpj;
import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.supermercado.domain.Endereco;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class SupermercadoRepositoryAdapter implements SupermercadoRepository {

    private final SupermercadoJpaRepository jpaRepository;

    public SupermercadoRepositoryAdapter(SupermercadoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Supermercado> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(SupermercadoRepositoryAdapter::paraDominio);
    }

    @Override
    public Optional<Supermercado> buscarPorIdComLock(Long id) {
        return jpaRepository.buscarPorIdComLock(id).map(SupermercadoRepositoryAdapter::paraDominio);
    }

    @Override
    public boolean existePorCnpj(Cnpj cnpj) {
        return jpaRepository.existsByCnpj(cnpj.valor());
    }

    @Override
    public Supermercado salvar(Supermercado supermercado) {
        SupermercadoJpaEntity entidade = supermercado.id() == null
                ? new SupermercadoJpaEntity(
                        null,
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
                        null,
                        supermercado.criadoEm(),
                        supermercado.atualizadoEm()
                )
                : atualizar(jpaRepository.findById(supermercado.id()).orElseThrow(() ->
                        new IllegalStateException("Supermercado " + supermercado.id() + " não encontrado para atualização")), supermercado);

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public Pagina<Supermercado> listar(int pagina, int tamanho) {
        PageRequest paginacao = PageRequest.of(pagina, tamanho, Sort.by(Sort.Direction.ASC, "razaoSocial"));
        Page<SupermercadoJpaEntity> resultado = jpaRepository.findAll(paginacao);
        return new Pagina<>(
                resultado.getContent().stream().map(SupermercadoRepositoryAdapter::paraDominio).toList(),
                pagina,
                tamanho,
                resultado.getTotalElements(),
                resultado.getTotalPages()
        );
    }

    @Override
    public List<Supermercado> listarTodos() {
        return jpaRepository.findAll().stream().map(SupermercadoRepositoryAdapter::paraDominio).toList();
    }

    private static SupermercadoJpaEntity atualizar(SupermercadoJpaEntity entidade, Supermercado supermercado) {
        entidade.setRazaoSocial(supermercado.razaoSocial());
        entidade.setNomeFantasia(supermercado.nomeFantasia());
        entidade.setEmailComercial(supermercado.emailComercial());
        entidade.setTelefoneComercial(supermercado.telefoneComercial());
        entidade.setCep(supermercado.endereco().cep());
        entidade.setLogradouro(supermercado.endereco().logradouro());
        entidade.setNumero(supermercado.endereco().numero());
        entidade.setBairro(supermercado.endereco().bairro());
        entidade.setMunicipio(supermercado.endereco().municipio());
        entidade.setUf(supermercado.endereco().uf());
        entidade.setComplemento(supermercado.complemento());
        entidade.setLogomarcaUrl(supermercado.logomarcaUrl());
        entidade.setObservacoesInternas(supermercado.observacoesInternas());
        entidade.setEstado(supermercado.estado());
        entidade.setAtualizadoEm(supermercado.atualizadoEm());
        return entidade;
    }

    private static Supermercado paraDominio(SupermercadoJpaEntity entidade) {
        return new Supermercado(
                entidade.getId(),
                new Cnpj(entidade.getCnpj()),
                entidade.getRazaoSocial(),
                entidade.getNomeFantasia(),
                entidade.getEmailComercial(),
                entidade.getTelefoneComercial(),
                new Endereco(
                        entidade.getCep(),
                        entidade.getLogradouro(),
                        entidade.getNumero(),
                        entidade.getBairro(),
                        entidade.getMunicipio(),
                        entidade.getUf()
                ),
                entidade.getComplemento(),
                entidade.getLogomarcaUrl(),
                entidade.getObservacoesInternas(),
                entidade.getEstado(),
                entidade.getVersao(),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm()
        );
    }
}
