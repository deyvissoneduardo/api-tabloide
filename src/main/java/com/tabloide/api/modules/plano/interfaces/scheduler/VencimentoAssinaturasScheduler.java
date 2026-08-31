package com.tabloide.api.modules.plano.interfaces.scheduler;

import com.tabloide.api.modules.plano.application.ProcessarVencimentoAssinaturas;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class VencimentoAssinaturasScheduler {

    private final ProcessarVencimentoAssinaturas processarVencimentoAssinaturas;

    public VencimentoAssinaturasScheduler(ProcessarVencimentoAssinaturas processarVencimentoAssinaturas) {
        this.processarVencimentoAssinaturas = processarVencimentoAssinaturas;
    }

    @Scheduled(cron = "0 5 0 * * *", zone = "America/Sao_Paulo")
    public void executar() {
        processarVencimentoAssinaturas.executar();
    }
}
