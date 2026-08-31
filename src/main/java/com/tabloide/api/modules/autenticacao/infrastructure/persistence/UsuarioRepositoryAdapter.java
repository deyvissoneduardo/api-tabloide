package com.tabloide.api.modules.autenticacao.infrastructure.persistence;

import com.tabloide.api.modules.autenticacao.domain.Cnpj;
import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Usuario;
import com.tabloide.api.modules.autenticacao.domain.UsuarioRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return jpaRepository.findByEmail(email).map(UsuarioRepositoryAdapter::paraDominio);
    }

    @Override
    public Optional<Usuario> buscarPorEmailECnpj(String email, Cnpj cnpj) {
        return jpaRepository.findByEmailAndSupermercadoCnpj(email, cnpj.valor()).map(UsuarioRepositoryAdapter::paraDominio);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(UsuarioRepositoryAdapter::paraDominio);
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        UsuarioJpaEntity entidade = usuario.id() == null
                ? new UsuarioJpaEntity(
                        null,
                        usuario.email(),
                        usuario.senhaHash(),
                        usuario.perfil(),
                        usuario.supermercadoId(),
                        usuario.supermercadoCnpj() == null ? null : usuario.supermercadoCnpj().valor(),
                        usuario.estaAtivo(),
                        usuario.tentativasLoginInvalidas(),
                        usuario.bloqueadoAte(),
                        usuario.criadoEm(),
                        usuario.atualizadoEm()
                )
                : atualizar(usuario);
        return paraDominio(jpaRepository.save(entidade));
    }

    private UsuarioJpaEntity atualizar(Usuario usuario) {
        UsuarioJpaEntity entidade = jpaRepository.findById(usuario.id())
                .orElseThrow(() -> new IllegalStateException("Usuário " + usuario.id() + " não encontrado para atualização"));
        entidade.setSenhaHash(usuario.senhaHash());
        entidade.setTentativasLoginInvalidas(usuario.tentativasLoginInvalidas());
        entidade.setBloqueadoAte(usuario.bloqueadoAte());
        entidade.setAtualizadoEm(usuario.atualizadoEm());
        return entidade;
    }

    @Override
    public Pagina<Usuario> listarPorSupermercado(Long supermercadoId, int pagina, int tamanho) {
        Page<UsuarioJpaEntity> resultado = jpaRepository.findBySupermercadoId(supermercadoId, PageRequest.of(pagina, tamanho));
        return new Pagina<>(
                resultado.getContent().stream().map(UsuarioRepositoryAdapter::paraDominio).toList(),
                pagina,
                tamanho,
                resultado.getTotalElements(),
                resultado.getTotalPages()
        );
    }

    private static Usuario paraDominio(UsuarioJpaEntity entidade) {
        Cnpj cnpj = entidade.getSupermercadoCnpj() == null ? null : new Cnpj(entidade.getSupermercadoCnpj());
        return new Usuario(
                entidade.getId(),
                entidade.getEmail(),
                entidade.getSenhaHash(),
                entidade.getPerfil(),
                entidade.getSupermercadoId(),
                cnpj,
                entidade.isAtivo(),
                entidade.getTentativasLoginInvalidas(),
                entidade.getBloqueadoAte(),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm()
        );
    }
}
