package com.tabloide.api.modules.plano.domain;

import java.time.Instant;

// RN-013: aviso interno a um DONO sobre o vencimento próximo da assinatura do seu supermercado.
public class AvisoInterno {

    private final Long id;
    private final Long assinaturaId;
    private final Long destinatarioUsuarioId;
    private final Long supermercadoId;
    private final TipoAvisoAssinatura tipo;
    private final String mensagem;
    private final Instant lidoEm;
    private final Instant criadoEm;

    public AvisoInterno(
            Long id,
            Long assinaturaId,
            Long destinatarioUsuarioId,
            Long supermercadoId,
            TipoAvisoAssinatura tipo,
            String mensagem,
            Instant lidoEm,
            Instant criadoEm
    ) {
        this.id = id;
        this.assinaturaId = assinaturaId;
        this.destinatarioUsuarioId = destinatarioUsuarioId;
        this.supermercadoId = supermercadoId;
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.lidoEm = lidoEm;
        this.criadoEm = criadoEm;
    }

    public static AvisoInterno paraVencimento(Assinatura assinatura, Long destinatarioUsuarioId, TipoAvisoAssinatura tipo, Instant agora) {
        String mensagem = "A assinatura do plano \"" + assinatura.planoNome() + "\" vence em " + assinatura.dataFim() + ".";
        return new AvisoInterno(null, assinatura.id(), destinatarioUsuarioId, assinatura.supermercadoId(), tipo, mensagem, null, agora);
    }

    public Long id() {
        return id;
    }

    public Long assinaturaId() {
        return assinaturaId;
    }

    public Long destinatarioUsuarioId() {
        return destinatarioUsuarioId;
    }

    public Long supermercadoId() {
        return supermercadoId;
    }

    public TipoAvisoAssinatura tipo() {
        return tipo;
    }

    public String mensagem() {
        return mensagem;
    }

    public Instant lidoEm() {
        return lidoEm;
    }

    public Instant criadoEm() {
        return criadoEm;
    }
}
