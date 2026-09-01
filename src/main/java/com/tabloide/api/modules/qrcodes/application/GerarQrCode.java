package com.tabloide.api.modules.qrcodes.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.loja.domain.Loja;
import com.tabloide.api.modules.loja.domain.LojaRepository;
import com.tabloide.api.modules.loja.domain.exceptions.LojaNaoEncontradaException;
import com.tabloide.api.modules.qrcodes.domain.QrCode;
import com.tabloide.api.modules.qrcodes.domain.QrCodeRepository;
import com.tabloide.api.modules.qrcodes.domain.exceptions.LojaIndisponivelParaQrCodeException;
import com.tabloide.api.modules.qrcodes.domain.exceptions.NomeDeQrCodeJaCadastradoException;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GerarQrCode {

    private final QrCodeRepository qrCodeRepository;
    private final LojaRepository lojaRepository;
    private final AuditoriaRepository auditoriaRepository;

    public GerarQrCode(
            QrCodeRepository qrCodeRepository,
            LojaRepository lojaRepository,
            AuditoriaRepository auditoriaRepository
    ) {
        this.qrCodeRepository = qrCodeRepository;
        this.lojaRepository = lojaRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public QrCode executar(Long supermercadoId, Long lojaId, DadosQrCode dados, Long atorId, Perfil perfilAtor, Long supermercadoIdAtor) {
        if (foraDoEscopoDoAtor(supermercadoId, supermercadoIdAtor)) {
            throw new LojaNaoEncontradaException();
        }

        Loja loja = lojaRepository.buscarPorIdESupermercado(lojaId, supermercadoId)
                .orElseThrow(LojaNaoEncontradaException::new);
        if (!loja.estaAtiva()) {
            throw new LojaIndisponivelParaQrCodeException();
        }

        String nomeNormalizado = QrCode.normalizarNome(dados.nome());
        if (qrCodeRepository.existeNomeNormalizadoNoSupermercado(nomeNormalizado, supermercadoId)) {
            throw new NomeDeQrCodeJaCadastradoException();
        }

        Instant agora = Instant.now();
        QrCode qrCode = QrCode.gerar(supermercadoId, lojaId, dados.nome(), agora);
        QrCode salvo = qrCodeRepository.salvar(qrCode);

        auditoriaRepository.registrar(new RegistroAuditoria(
                null, atorId, perfilAtor, supermercadoId, "QRCODE_CADASTRADO", "QrCode", salvo.id(),
                null, salvo.resumoParaAuditoria(), agora
        ));

        return salvo;
    }

    private boolean foraDoEscopoDoAtor(Long supermercadoId, Long supermercadoIdAtor) {
        return !Objects.equals(supermercadoId, supermercadoIdAtor);
    }
}
