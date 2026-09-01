package com.tabloide.api.modules.qrcodes.infrastructure.persistence;

import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.qrcodes.domain.QrCode;
import com.tabloide.api.modules.qrcodes.domain.QrCodeRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class QrCodeRepositoryAdapter implements QrCodeRepository {

    private final QrCodeJpaRepository jpaRepository;

    public QrCodeRepositoryAdapter(QrCodeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<QrCode> buscarPorIdESupermercado(Long id, Long supermercadoId) {
        return jpaRepository.findByIdAndSupermercadoId(id, supermercadoId).map(QrCodeRepositoryAdapter::paraDominio);
    }

    @Override
    public boolean existeNomeNormalizadoNoSupermercado(String nomeNormalizado, Long supermercadoId) {
        return jpaRepository.existsByNomeNormalizadoAndSupermercadoId(nomeNormalizado, supermercadoId);
    }

    @Override
    public QrCode salvar(QrCode qrCode) {
        QrCodeJpaEntity entidade = qrCode.id() == null
                ? new QrCodeJpaEntity(
                        null,
                        qrCode.supermercadoId(),
                        qrCode.lojaId(),
                        qrCode.nome(),
                        qrCode.nomeNormalizado(),
                        qrCode.codigoPublico(),
                        qrCode.estado(),
                        null,
                        qrCode.criadoEm(),
                        qrCode.atualizadoEm()
                )
                : atualizar(jpaRepository.findById(qrCode.id()).orElseThrow(() ->
                        new IllegalStateException("QR Code " + qrCode.id() + " não encontrado para atualização")), qrCode);

        return paraDominio(jpaRepository.save(entidade));
    }

    @Override
    public Pagina<QrCode> listarPorLoja(Long lojaId, Long supermercadoId, int pagina, int tamanho) {
        PageRequest paginacao = PageRequest.of(pagina, tamanho, Sort.by(Sort.Direction.ASC, "nome"));
        Page<QrCodeJpaEntity> resultado = jpaRepository.findByLojaIdAndSupermercadoId(lojaId, supermercadoId, paginacao);
        return new Pagina<>(
                resultado.getContent().stream().map(QrCodeRepositoryAdapter::paraDominio).toList(),
                pagina,
                tamanho,
                resultado.getTotalElements(),
                resultado.getTotalPages()
        );
    }

    private static QrCodeJpaEntity atualizar(QrCodeJpaEntity entidade, QrCode qrCode) {
        entidade.setNome(qrCode.nome());
        entidade.setNomeNormalizado(qrCode.nomeNormalizado());
        entidade.setEstado(qrCode.estado());
        entidade.setAtualizadoEm(qrCode.atualizadoEm());
        return entidade;
    }

    private static QrCode paraDominio(QrCodeJpaEntity entidade) {
        return new QrCode(
                entidade.getId(),
                entidade.getSupermercadoId(),
                entidade.getLojaId(),
                entidade.getNome(),
                entidade.getNomeNormalizado(),
                entidade.getCodigoPublico(),
                entidade.getEstado(),
                entidade.getVersao(),
                entidade.getCriadoEm(),
                entidade.getAtualizadoEm()
        );
    }
}
