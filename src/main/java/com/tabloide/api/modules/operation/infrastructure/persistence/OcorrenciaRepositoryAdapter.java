package com.tabloide.api.modules.operation.infrastructure.persistence;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.operation.domain.EventoOcorrencia;
import com.tabloide.api.modules.operation.domain.FiltroOcorrencia;
import com.tabloide.api.modules.operation.domain.Ocorrencia;
import com.tabloide.api.modules.operation.domain.OcorrenciaRepository;
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
public class OcorrenciaRepositoryAdapter implements OcorrenciaRepository {

    private final OcorrenciaJpaRepository jpaRepository;
    private final EventoOcorrenciaJpaRepository eventoJpaRepository;

    public OcorrenciaRepositoryAdapter(OcorrenciaJpaRepository jpaRepository, EventoOcorrenciaJpaRepository eventoJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.eventoJpaRepository = eventoJpaRepository;
    }

    @Override
    public Optional<Ocorrencia> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(OcorrenciaRepositoryAdapter::paraDominio);
    }

    @Override
    public Ocorrencia salvar(Ocorrencia ocorrencia) {
        OcorrenciaJpaEntity entidade = ocorrencia.id() == null
                ? new OcorrenciaJpaEntity(
                        null,
                        ocorrencia.titulo(),
                        ocorrencia.descricao(),
                        ocorrencia.severidade(),
                        ocorrencia.estado(),
                        ocorrencia.supermercadoId(),
                        ocorrencia.responsavelId(),
                        ocorrencia.abertaEm(),
                        ocorrencia.resolvidaEm(),
                        ocorrencia.encerradaEm(),
                        ocorrencia.atualizadoEm(),
                        null
                )
                : atualizar(jpaRepository.findById(ocorrencia.id()).orElseThrow(() ->
                        new IllegalStateException("Ocorrência " + ocorrencia.id() + " não encontrada para atualização")), ocorrencia);

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public Pagina<Ocorrencia> listar(FiltroOcorrencia filtro, int pagina, int tamanho) {
        PageRequest paginacao = PageRequest.of(pagina, tamanho, Sort.by(Sort.Direction.DESC, "abertaEm"));
        Page<OcorrenciaJpaEntity> resultado = jpaRepository.findAll(especificacao(filtro), paginacao);
        return new Pagina<>(
                resultado.getContent().stream().map(OcorrenciaRepositoryAdapter::paraDominio).toList(),
                pagina,
                tamanho,
                resultado.getTotalElements(),
                resultado.getTotalPages()
        );
    }

    @Override
    public void registrarEvento(EventoOcorrencia evento) {
        eventoJpaRepository.save(new EventoOcorrenciaJpaEntity(
                null,
                evento.ocorrenciaId(),
                evento.tipo(),
                evento.estadoAnterior(),
                evento.estadoNovo(),
                evento.comentario(),
                evento.autorId(),
                evento.criadoEm()
        ));
    }

    @Override
    public List<EventoOcorrencia> listarEventos(Long ocorrenciaId) {
        return eventoJpaRepository.findByOcorrenciaIdOrderByCriadoEmAsc(ocorrenciaId).stream()
                .map(OcorrenciaRepositoryAdapter::paraDominio)
                .toList();
    }

    private static OcorrenciaJpaEntity atualizar(OcorrenciaJpaEntity entidade, Ocorrencia ocorrencia) {
        entidade.setEstado(ocorrencia.estado());
        entidade.setResolvidaEm(ocorrencia.resolvidaEm());
        entidade.setEncerradaEm(ocorrencia.encerradaEm());
        entidade.setAtualizadoEm(ocorrencia.atualizadoEm());
        return entidade;
    }

    private static Specification<OcorrenciaJpaEntity> especificacao(FiltroOcorrencia filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicados = new ArrayList<>();
            if (filtro.estado() != null) {
                predicados.add(cb.equal(root.get("estado"), filtro.estado()));
            }
            if (filtro.severidade() != null) {
                predicados.add(cb.equal(root.get("severidade"), filtro.severidade()));
            }
            if (filtro.supermercadoId() != null) {
                predicados.add(cb.equal(root.get("supermercadoId"), filtro.supermercadoId()));
            }
            return cb.and(predicados.toArray(new Predicate[0]));
        };
    }

    private static Ocorrencia paraDominio(OcorrenciaJpaEntity entidade) {
        return new Ocorrencia(
                entidade.getId(),
                entidade.getTitulo(),
                entidade.getDescricao(),
                entidade.getSeveridade(),
                entidade.getEstado(),
                entidade.getSupermercadoId(),
                entidade.getResponsavelId(),
                entidade.getAbertaEm(),
                entidade.getResolvidaEm(),
                entidade.getEncerradaEm(),
                entidade.getAtualizadoEm(),
                entidade.getVersao()
        );
    }

    private static EventoOcorrencia paraDominio(EventoOcorrenciaJpaEntity entidade) {
        return new EventoOcorrencia(
                entidade.getId(),
                entidade.getOcorrenciaId(),
                entidade.getTipo(),
                entidade.getEstadoAnterior(),
                entidade.getEstadoNovo(),
                entidade.getComentario(),
                entidade.getAutorId(),
                entidade.getCriadoEm()
        );
    }
}
