package com.tabloide.api.modules.auditoria.infrastructure.persistence;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.FiltroAuditoria;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Pagina;
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
public class AuditoriaRepositoryAdapter implements AuditoriaRepository {

    private final RegistroAuditoriaJpaRepository jpaRepository;

    public AuditoriaRepositoryAdapter(RegistroAuditoriaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void registrar(RegistroAuditoria registro) {
        jpaRepository.save(new RegistroAuditoriaJpaEntity(
                registro.atorId(),
                registro.perfilAtor(),
                registro.supermercadoId(),
                registro.acao(),
                registro.entidade(),
                registro.entidadeId(),
                registro.dadosAntes(),
                registro.dadosDepois(),
                registro.instante()
        ));
    }

    @Override
    public Pagina<RegistroAuditoria> listar(Long supermercadoId, FiltroAuditoria filtro, int pagina, int tamanho) {
        PageRequest paginacao = PageRequest.of(pagina, tamanho, Sort.by(Sort.Direction.DESC, "instante"));
        Page<RegistroAuditoriaJpaEntity> resultado = jpaRepository.findAll(especificacao(supermercadoId, filtro), paginacao);
        return new Pagina<>(
                resultado.getContent().stream().map(AuditoriaRepositoryAdapter::paraDominio).toList(),
                pagina,
                tamanho,
                resultado.getTotalElements(),
                resultado.getTotalPages()
        );
    }

    @Override
    public Optional<RegistroAuditoria> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(AuditoriaRepositoryAdapter::paraDominio);
    }

    private static Specification<RegistroAuditoriaJpaEntity> especificacao(Long supermercadoId, FiltroAuditoria filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicados = new ArrayList<>();
            if (supermercadoId != null) {
                predicados.add(cb.equal(root.get("supermercadoId"), supermercadoId));
            }
            if (filtro.atorId() != null) {
                predicados.add(cb.equal(root.get("atorId"), filtro.atorId()));
            }
            if (filtro.acao() != null) {
                predicados.add(cb.equal(root.get("acao"), filtro.acao()));
            }
            if (filtro.entidade() != null) {
                predicados.add(cb.equal(root.get("entidade"), filtro.entidade()));
            }
            if (filtro.dataInicio() != null) {
                predicados.add(cb.greaterThanOrEqualTo(root.get("instante"), filtro.dataInicio()));
            }
            if (filtro.dataFim() != null) {
                predicados.add(cb.lessThanOrEqualTo(root.get("instante"), filtro.dataFim()));
            }
            return cb.and(predicados.toArray(new Predicate[0]));
        };
    }

    private static RegistroAuditoria paraDominio(RegistroAuditoriaJpaEntity entidade) {
        return new RegistroAuditoria(
                entidade.getId(),
                entidade.getAtorId(),
                entidade.getPerfilAtor(),
                entidade.getSupermercadoId(),
                entidade.getAcao(),
                entidade.getEntidade(),
                entidade.getEntidadeId(),
                entidade.getDadosAntes(),
                entidade.getDadosDepois(),
                entidade.getInstante()
        );
    }
}
