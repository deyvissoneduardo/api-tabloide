package com.tabloide.api.modules.campanha.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.campanha.domain.Campanha;
import com.tabloide.api.modules.campanha.domain.CampanhaRepository;
import com.tabloide.api.modules.campanha.domain.EstadoCampanha;
import com.tabloide.api.modules.campanha.domain.exceptions.CampanhaNaoEncontradaException;
import com.tabloide.api.modules.campanha.domain.exceptions.TransicaoEstadoCampanhaInvalidaException;
import com.tabloide.api.modules.campanha.domain.exceptions.VersaoCampanhaDesatualizadaException;
import com.tabloide.api.modules.loja.domain.EstadoLoja;
import com.tabloide.api.modules.loja.domain.Loja;
import com.tabloide.api.modules.loja.domain.LojaRepository;
import com.tabloide.api.modules.oferta.domain.OfertaRepository;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EditarCampanhaTest {

    private static final Long SUPERMERCADO_ID = 1L;
    private static final Long CAMPANHA_ID = 5L;
    private static final Long LOJA_ID = 10L;

    @Mock
    private CampanhaRepository campanhaRepository;

    @Mock
    private LojaRepository lojaRepository;

    @Mock
    private OfertaRepository ofertaRepository;

    @Mock
    private SupermercadoRepository supermercadoRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private EditarCampanha editarCampanha;

    @BeforeEach
    void configurar() {
        editarCampanha = new EditarCampanha(campanhaRepository, lojaRepository, ofertaRepository, supermercadoRepository, auditoriaRepository);
    }

    private static Supermercado supermercadoAtivo() {
        Instant agora = Instant.now();
        return new Supermercado(SUPERMERCADO_ID, null, "Razão", "Fantasia", "e@e.com", "119999", null, null, null, null,
                EstadoSupermercado.ATIVO, 0L, agora, agora);
    }

    private static Loja lojaNoSupermercado() {
        Instant agora = Instant.now();
        return new Loja(LOJA_ID, SUPERMERCADO_ID, "Loja Centro", "loja centro", null, null, EstadoLoja.ATIVA, 0L, agora, agora);
    }

    private static Campanha campanhaRascunho() {
        Instant agora = Instant.now();
        return new Campanha(CAMPANHA_ID, SUPERMERCADO_ID, "Campanha", null, Set.of(LOJA_ID), Set.of(),
                agora, agora.plus(1, ChronoUnit.DAYS), EstadoCampanha.RASCUNHO, 0L, agora, agora);
    }

    private static DadosCampanha novosDados() {
        Instant agora = Instant.now();
        return new DadosCampanha("Campanha Editada", "Nova descrição", Set.of(LOJA_ID), Set.of(), agora, agora.plus(2, ChronoUnit.DAYS), false);
    }

    @Test
    void deveRejeitarQuandoForaDoEscopoDoAtor() {
        assertThatThrownBy(() -> editarCampanha.executar(SUPERMERCADO_ID, CAMPANHA_ID, 0L, novosDados(), 1L, Perfil.DONO, 2L))
                .isInstanceOf(CampanhaNaoEncontradaException.class);
    }

    @Test
    void deveRejeitarQuandoNaoEncontrada() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(campanhaRepository.buscarPorIdESupermercado(CAMPANHA_ID, SUPERMERCADO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> editarCampanha.executar(SUPERMERCADO_ID, CAMPANHA_ID, 0L, novosDados(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(CampanhaNaoEncontradaException.class);
    }

    @Test
    void deveRejeitarQuandoVersaoDesatualizada() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(campanhaRepository.buscarPorIdESupermercado(CAMPANHA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(campanhaRascunho()));

        assertThatThrownBy(() -> editarCampanha.executar(SUPERMERCADO_ID, CAMPANHA_ID, 99L, novosDados(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(VersaoCampanhaDesatualizadaException.class);
    }

    @Test
    void deveRejeitarQuandoFinalizada() {
        Instant agora = Instant.now();
        Campanha cancelada = new Campanha(CAMPANHA_ID, SUPERMERCADO_ID, "Campanha", null, Set.of(LOJA_ID), Set.of(),
                agora, agora.plus(1, ChronoUnit.DAYS), EstadoCampanha.CANCELADA, 0L, agora, agora);
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(campanhaRepository.buscarPorIdESupermercado(CAMPANHA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(cancelada));
        when(lojaRepository.buscarPorIdESupermercado(LOJA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(lojaNoSupermercado()));

        assertThatThrownBy(() -> editarCampanha.executar(SUPERMERCADO_ID, CAMPANHA_ID, 0L, novosDados(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(TransicaoEstadoCampanhaInvalidaException.class);
    }

    @Test
    void deveEditarERegistrarAuditoria() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(campanhaRepository.buscarPorIdESupermercado(CAMPANHA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(campanhaRascunho()));
        when(lojaRepository.buscarPorIdESupermercado(LOJA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(lojaNoSupermercado()));
        when(campanhaRepository.salvar(any(Campanha.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Campanha resultado = editarCampanha.executar(SUPERMERCADO_ID, CAMPANHA_ID, 0L, novosDados(), 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.nome()).isEqualTo("Campanha Editada");
        org.mockito.Mockito.verify(auditoriaRepository).registrar(any());
    }
}
