package com.tabloide.api.modules.plano.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.Usuario;
import com.tabloide.api.modules.autenticacao.domain.UsuarioRepository;
import com.tabloide.api.modules.plano.domain.Assinatura;
import com.tabloide.api.modules.plano.domain.AssinaturaRepository;
import com.tabloide.api.modules.plano.domain.AvisoInterno;
import com.tabloide.api.modules.plano.domain.AvisoInternoRepository;
import com.tabloide.api.modules.plano.domain.TipoAvisoAssinatura;
import com.tabloide.api.modules.supermercado.application.BloquearSupermercado;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// US-212: RN-012 (vencimento bloqueia automaticamente o supermercado) e RN-013 (avisos internos
// aos DONOS 10/5/1 dias antes do vencimento). Job de sistema, sem ator humano.
@Component
public class ProcessarVencimentoAssinaturas {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessarVencimentoAssinaturas.class);

    private static final Map<Integer, TipoAvisoAssinatura> TIPOS_POR_DIAS_DE_ANTECEDENCIA = Map.of(
            10, TipoAvisoAssinatura.VENCE_EM_10_DIAS,
            5, TipoAvisoAssinatura.VENCE_EM_5_DIAS,
            1, TipoAvisoAssinatura.VENCE_EM_1_DIA
    );

    private final AssinaturaRepository assinaturaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AvisoInternoRepository avisoInternoRepository;
    private final BloquearSupermercado bloquearSupermercado;
    private final AuditoriaRepository auditoriaRepository;

    public ProcessarVencimentoAssinaturas(
            AssinaturaRepository assinaturaRepository,
            UsuarioRepository usuarioRepository,
            AvisoInternoRepository avisoInternoRepository,
            BloquearSupermercado bloquearSupermercado,
            AuditoriaRepository auditoriaRepository
    ) {
        this.assinaturaRepository = assinaturaRepository;
        this.usuarioRepository = usuarioRepository;
        this.avisoInternoRepository = avisoInternoRepository;
        this.bloquearSupermercado = bloquearSupermercado;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public void executar() {
        Instant agora = Instant.now();
        int vencidas = processarVencidas(agora);
        int avisos = processarAvisos(agora);
        LOG.info("Processamento de vencimento de assinaturas concluído: {} vencida(s), {} aviso(s) gerado(s)", vencidas, avisos);
    }

    private int processarVencidas(Instant agora) {
        List<Assinatura> vencendo = assinaturaRepository.listarVigentesComVencimentoAte(agora);
        for (Assinatura assinatura : vencendo) {
            String antes = assinatura.resumoParaAuditoria();
            assinatura.vencer();
            Assinatura salva = assinaturaRepository.salvar(assinatura);

            bloquearSupermercado.executar(salva.supermercadoId(), null, Perfil.SUPER_ADMIN);

            auditoriaRepository.registrar(new RegistroAuditoria(
                    null, null, Perfil.SUPER_ADMIN, salva.supermercadoId(), "ASSINATURA_VENCIDA", "Assinatura", salva.id(),
                    antes, salva.resumoParaAuditoria(), agora
            ));
        }
        return vencendo.size();
    }

    private int processarAvisos(Instant agora) {
        LocalDate hoje = agora.atZone(Assinatura.FUSO_BRASILIA).toLocalDate();
        int total = 0;
        for (Map.Entry<Integer, TipoAvisoAssinatura> entrada : TIPOS_POR_DIAS_DE_ANTECEDENCIA.entrySet()) {
            LocalDate dataAlvo = hoje.plusDays(entrada.getKey());
            Instant inicioDia = dataAlvo.atStartOfDay(Assinatura.FUSO_BRASILIA).toInstant();
            Instant fimDia = dataAlvo.plusDays(1).atStartOfDay(Assinatura.FUSO_BRASILIA).toInstant().minusNanos(1);

            for (Assinatura assinatura : assinaturaRepository.listarVigentesComVencimentoEntre(inicioDia, fimDia)) {
                total += gerarAvisosParaDonos(assinatura, entrada.getValue(), agora);
            }
        }
        return total;
    }

    private int gerarAvisosParaDonos(Assinatura assinatura, TipoAvisoAssinatura tipo, Instant agora) {
        int gerados = 0;
        for (Usuario dono : usuarioRepository.listarAtivosPorSupermercadoEPerfil(assinatura.supermercadoId(), Perfil.DONO)) {
            AvisoInterno aviso = AvisoInterno.paraVencimento(assinatura, dono.id(), tipo, agora);
            if (avisoInternoRepository.salvarSeNaoExiste(aviso)) {
                gerados++;
            }
        }
        return gerados;
    }
}
