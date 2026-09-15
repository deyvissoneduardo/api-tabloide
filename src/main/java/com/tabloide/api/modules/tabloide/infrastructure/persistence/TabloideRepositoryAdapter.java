package com.tabloide.api.modules.tabloide.infrastructure.persistence;

import com.tabloide.api.modules.tabloide.domain.Tabloide;
import com.tabloide.api.modules.tabloide.domain.TabloideRepository;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class TabloideRepositoryAdapter implements TabloideRepository {

    private final TabloideJpaRepository jpaRepository;

    public TabloideRepositoryAdapter(TabloideJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Tabloide salvar(Tabloide tabloide) {
        TabloideJpaEntity entidade = new TabloideJpaEntity(
                tabloide.id(),
                tabloide.supermercadoId(),
                tabloide.titulo(),
                tabloide.tipoArquivo(),
                tabloide.arquivoPdfUrl(),
                tabloide.arquivoPdfTamanhoBytes(),
                tabloide.lojaIds(),
                tabloide.inicio(),
                tabloide.fim(),
                tabloide.estado(),
                tabloide.versao(),
                tabloide.criadoEm(),
                tabloide.atualizadoEm()
        );
        return paraDominio(jpaRepository.saveAndFlush(entidade));
    }

    @Override
    public Optional<Tabloide> buscarVigentePorSupermercado(Long supermercadoId, Instant agora) {
        return jpaRepository.findVigentePorSupermercado(supermercadoId, agora).map(TabloideRepositoryAdapter::paraDominio);
    }

    private static Tabloide paraDominio(TabloideJpaEntity entidade) {
        return new Tabloide(
                entidade.getId(),
                entidade.getSupermercadoId(),
                entidade.getTitulo(),
                entidade.getTipoArquivo(),
                entidade.getArquivoPdfUrl(),
                entidade.getArquivoPdfTamanhoBytes(),
                entidade.getLojaIds(),
                entidade.getInicio(),
                entidade.getFim(),
                entidade.getEstado(),
                entidade.getVersao(),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm()
        );
    }
}
