package com.tabloide.api.modules.conteudogeral.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.conteudogeral.domain.ConteudoGeral;
import com.tabloide.api.modules.conteudogeral.domain.ConteudoGeralRepository;
import com.tabloide.api.modules.conteudogeral.domain.TipoConteudoGeral;
import com.tabloide.api.modules.conteudogeral.domain.exceptions.ConteudoGeralNaoEncontradoException;
import com.tabloide.api.modules.conteudogeral.domain.exceptions.TransicaoEstadoConteudoGeralInvalidaException;
import com.tabloide.api.modules.conteudogeral.domain.exceptions.VersaoConteudoGeralDesatualizadaException;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EditarConteudoGeralTest {

    private static final Long ID = 1L;

    @Mock
    private ConteudoGeralRepository conteudoGeralRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private EditarConteudoGeral editarConteudoGeral;

    @BeforeEach
    void configurar() {
        editarConteudoGeral = new EditarConteudoGeral(conteudoGeralRepository, auditoriaRepository);
    }

    private static ConteudoGeral rascunho() {
        Instant agora = Instant.now();
        return new ConteudoGeral(ID, TipoConteudoGeral.TERMOS_USO, "Termos", "Corpo",
                com.tabloide.api.modules.conteudogeral.domain.EstadoConteudoGeral.RASCUNHO, 0L, agora, agora, null);
    }

    @Test
    void deveRejeitarQuandoNaoEncontrado() {
        when(conteudoGeralRepository.buscarPorId(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> editarConteudoGeral.executar(ID, 0L, "Novo", "Novo", 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(ConteudoGeralNaoEncontradoException.class);
    }

    @Test
    void deveRejeitarQuandoVersaoDesatualizada() {
        when(conteudoGeralRepository.buscarPorId(ID)).thenReturn(Optional.of(rascunho()));

        assertThatThrownBy(() -> editarConteudoGeral.executar(ID, 99L, "Novo", "Novo", 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(VersaoConteudoGeralDesatualizadaException.class);

        verify(conteudoGeralRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarQuandoNaoEstaEmRascunho() {
        Instant agora = Instant.now();
        ConteudoGeral publicado = new ConteudoGeral(ID, TipoConteudoGeral.TERMOS_USO, "Termos", "Corpo",
                com.tabloide.api.modules.conteudogeral.domain.EstadoConteudoGeral.PUBLICADO, 0L, agora, agora, agora);
        when(conteudoGeralRepository.buscarPorId(ID)).thenReturn(Optional.of(publicado));

        assertThatThrownBy(() -> editarConteudoGeral.executar(ID, 0L, "Novo", "Novo", 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(TransicaoEstadoConteudoGeralInvalidaException.class);
    }

    @Test
    void deveEditarERegistrarAuditoria() {
        when(conteudoGeralRepository.buscarPorId(ID)).thenReturn(Optional.of(rascunho()));
        when(conteudoGeralRepository.salvar(any(ConteudoGeral.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        ConteudoGeral resultado = editarConteudoGeral.executar(ID, 0L, "Termos v2", "Corpo v2", 1L, Perfil.SUPER_ADMIN);

        assertThat(resultado.titulo()).isEqualTo("Termos v2");
        verify(auditoriaRepository).registrar(any());
    }
}
