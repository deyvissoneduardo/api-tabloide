package com.tabloide.api.modules.conteudogeral.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.conteudogeral.domain.ConteudoGeral;
import com.tabloide.api.modules.conteudogeral.domain.ConteudoGeralRepository;
import com.tabloide.api.modules.conteudogeral.domain.EstadoConteudoGeral;
import com.tabloide.api.modules.conteudogeral.domain.TipoConteudoGeral;
import com.tabloide.api.modules.conteudogeral.domain.exceptions.TransicaoEstadoConteudoGeralInvalidaException;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArquivarConteudoGeralTest {

    private static final Long ID = 1L;

    @Mock
    private ConteudoGeralRepository conteudoGeralRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private ArquivarConteudoGeral arquivarConteudoGeral;

    @BeforeEach
    void configurar() {
        arquivarConteudoGeral = new ArquivarConteudoGeral(conteudoGeralRepository, auditoriaRepository);
    }

    @Test
    void deveArquivarQuandoPublicado() {
        Instant agora = Instant.now();
        ConteudoGeral publicado = new ConteudoGeral(ID, TipoConteudoGeral.TERMOS_USO, "Termos", "Corpo", EstadoConteudoGeral.PUBLICADO, 0L, agora, agora, agora);
        when(conteudoGeralRepository.buscarPorId(ID)).thenReturn(Optional.of(publicado));
        when(conteudoGeralRepository.salvar(any(ConteudoGeral.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        ConteudoGeral resultado = arquivarConteudoGeral.executar(ID, 0L, 1L, Perfil.SUPER_ADMIN);

        assertThat(resultado.estado()).isEqualTo(EstadoConteudoGeral.ARQUIVADO);
    }

    @Test
    void naoDeveArquivarQuandoRascunho() {
        Instant agora = Instant.now();
        ConteudoGeral rascunho = new ConteudoGeral(ID, TipoConteudoGeral.TERMOS_USO, "Termos", "Corpo", EstadoConteudoGeral.RASCUNHO, 0L, agora, agora, null);
        when(conteudoGeralRepository.buscarPorId(ID)).thenReturn(Optional.of(rascunho));

        assertThatThrownBy(() -> arquivarConteudoGeral.executar(ID, 0L, 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(TransicaoEstadoConteudoGeralInvalidaException.class);
    }
}
