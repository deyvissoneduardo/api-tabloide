package com.tabloide.api.modules.auditoria.application;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.FiltroAuditoria;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Pagina;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.autenticacao.domain.exceptions.TamanhoPaginaInvalidoException;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ListarAuditoria {

    private static final Set<Integer> TAMANHOS_PERMITIDOS = Set.of(25, 50, 100);

    private final AuditoriaRepository auditoriaRepository;

    public ListarAuditoria(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    public Pagina<RegistroAuditoria> executar(
            Perfil perfilSolicitante,
            Long supermercadoIdSolicitante,
            FiltroAuditoria filtro,
            int pagina,
            int tamanho
    ) {
        validarPaginacao(pagina, tamanho);
        return auditoriaRepository.listar(escopoDoSolicitante(perfilSolicitante, supermercadoIdSolicitante), filtro, pagina, tamanho);
    }

    private Long escopoDoSolicitante(Perfil perfilSolicitante, Long supermercadoIdSolicitante) {
        return perfilSolicitante == Perfil.SUPER_ADMIN ? null : supermercadoIdSolicitante;
    }

    private void validarPaginacao(int pagina, int tamanho) {
        if (pagina < 0 || !TAMANHOS_PERMITIDOS.contains(tamanho)) {
            throw new TamanhoPaginaInvalidoException();
        }
    }
}
