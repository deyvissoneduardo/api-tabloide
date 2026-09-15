package com.tabloide.api.modules.conteudogeral.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.conteudogeral.domain.ConteudoGeral;
import com.tabloide.api.modules.conteudogeral.domain.ConteudoGeralRepository;
import com.tabloide.api.modules.conteudogeral.domain.EstadoConteudoGeral;
import com.tabloide.api.modules.conteudogeral.domain.TipoConteudoGeral;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CriarConteudoGeralTest {

    @Mock
    private ConteudoGeralRepository conteudoGeralRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private CriarConteudoGeral criarConteudoGeral;

    @BeforeEach
    void configurar() {
        criarConteudoGeral = new CriarConteudoGeral(conteudoGeralRepository, auditoriaRepository);
    }

    @Test
    void deveCriarEmRascunhoERegistrarAuditoria() {
        when(conteudoGeralRepository.salvar(any(ConteudoGeral.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        ConteudoGeral resultado = criarConteudoGeral.executar(
                new DadosConteudoGeral(TipoConteudoGeral.TERMOS_USO, "Termos", "Corpo"), 1L, Perfil.SUPER_ADMIN
        );

        assertThat(resultado.estado()).isEqualTo(EstadoConteudoGeral.RASCUNHO);
        org.mockito.Mockito.verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }
}
