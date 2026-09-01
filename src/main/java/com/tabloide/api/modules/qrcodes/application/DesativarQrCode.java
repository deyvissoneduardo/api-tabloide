package com.tabloide.api.modules.qrcodes.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.qrcodes.domain.QrCode;
import com.tabloide.api.modules.qrcodes.domain.QrCodeRepository;
import com.tabloide.api.modules.qrcodes.domain.exceptions.QrCodeNaoEncontradoException;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DesativarQrCode {

    private final QrCodeRepository qrCodeRepository;
    private final AuditoriaRepository auditoriaRepository;

    public DesativarQrCode(QrCodeRepository qrCodeRepository, AuditoriaRepository auditoriaRepository) {
        this.qrCodeRepository = qrCodeRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public QrCode executar(Long supermercadoId, Long id, Long atorId, Perfil perfilAtor, Long supermercadoIdAtor) {
        if (foraDoEscopoDoAtor(supermercadoId, supermercadoIdAtor)) {
            throw new QrCodeNaoEncontradoException();
        }

        QrCode qrCode = qrCodeRepository.buscarPorIdESupermercado(id, supermercadoId)
                .orElseThrow(QrCodeNaoEncontradoException::new);

        if (!qrCode.estaAtivo()) {
            return qrCode;
        }

        String antes = qrCode.resumoParaAuditoria();
        Instant agora = Instant.now();
        qrCode.desativar(agora);
        QrCode salvo = qrCodeRepository.salvar(qrCode);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, supermercadoId, "QRCODE_DESATIVADO", "QrCode", salvo.id(),
                antes, salvo.resumoParaAuditoria(), agora
        ));

        return salvo;
    }

    private boolean foraDoEscopoDoAtor(Long supermercadoId, Long supermercadoIdAtor) {
        return !Objects.equals(supermercadoId, supermercadoIdAtor);
    }
}
