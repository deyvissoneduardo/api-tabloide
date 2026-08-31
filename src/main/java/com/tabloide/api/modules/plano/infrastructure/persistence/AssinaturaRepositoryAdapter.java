package com.tabloide.api.modules.plano.infrastructure.persistence;

import com.tabloide.api.modules.plano.domain.Assinatura;
import com.tabloide.api.modules.plano.domain.AssinaturaRepository;
import com.tabloide.api.modules.plano.domain.EstadoAssinatura;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class AssinaturaRepositoryAdapter implements AssinaturaRepository {

    private static final List<EstadoAssinatura> ESTADOS_ATIVOS = List.of(EstadoAssinatura.AGENDADA, EstadoAssinatura.VIGENTE);

    private final AssinaturaJpaRepository jpaRepository;

    public AssinaturaRepositoryAdapter(AssinaturaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Assinatura> buscarVigenteOuAgendadaPorSupermercado(Long supermercadoId) {
        return jpaRepository.findFirstBySupermercadoIdAndEstadoIn(supermercadoId, ESTADOS_ATIVOS)
                .map(AssinaturaRepositoryAdapter::paraDominio);
    }

    @Override
    public Assinatura salvar(Assinatura assinatura) {
        AssinaturaJpaEntity entidade = assinatura.id() == null
                ? new AssinaturaJpaEntity(
                        null,
                        assinatura.supermercadoId(),
                        assinatura.planoId(),
                        assinatura.planoNome(),
                        assinatura.planoValidadeDias(),
                        assinatura.planoValor(),
                        assinatura.planoLimiteFotos(),
                        assinatura.estado(),
                        assinatura.dataInicio(),
                        assinatura.dataFim(),
                        assinatura.criadoEm()
                )
                : atualizar(jpaRepository.findById(assinatura.id()).orElseThrow(() ->
                        new IllegalStateException("Assinatura " + assinatura.id() + " não encontrada para atualização")), assinatura);

        // flush imediato: ao trocar de plano, a substituição da assinatura antiga precisa estar
        // gravada antes do insert da nova, para não violar o índice único de assinatura ativa.
        return paraDominio(jpaRepository.saveAndFlush(entidade));
    }

    private static AssinaturaJpaEntity atualizar(AssinaturaJpaEntity entidade, Assinatura assinatura) {
        entidade.setEstado(assinatura.estado());
        entidade.setDataFim(assinatura.dataFim());
        return entidade;
    }

    private static Assinatura paraDominio(AssinaturaJpaEntity entidade) {
        return new Assinatura(
                entidade.getId(),
                entidade.getSupermercadoId(),
                entidade.getPlanoId(),
                entidade.getPlanoNome(),
                entidade.getPlanoValidadeDias(),
                entidade.getPlanoValor(),
                entidade.getPlanoLimiteFotos(),
                entidade.getEstado(),
                entidade.getDataInicio(),
                entidade.getDataFim(),
                entidade.getCriadoEm()
        );
    }
}
