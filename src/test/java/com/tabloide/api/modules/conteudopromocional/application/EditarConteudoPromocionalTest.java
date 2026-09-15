package com.tabloide.api.modules.conteudopromocional.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.conteudopromocional.domain.ConteudoPromocional;
import com.tabloide.api.modules.conteudopromocional.domain.ConteudoPromocionalRepository;
import com.tabloide.api.modules.conteudopromocional.domain.EstadoConteudoPromocional;
import com.tabloide.api.modules.conteudopromocional.domain.TipoConteudoPromocional;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.ConteudoPromocionalNaoEncontradoException;
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.VersaoConteudoPromocionalDesatualizadaException;
import com.tabloide.api.modules.loja.domain.EstadoLoja;
import com.tabloide.api.modules.loja.domain.Loja;
import com.tabloide.api.modules.loja.domain.LojaRepository;
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
class EditarConteudoPromocionalTest {

    private static final Long SUPERMERCADO_ID = 1L;
    private static final Long CONTEUDO_ID = 5L;
    private static final Long LOJA_ID = 20L;

    @Mock
    private ConteudoPromocionalRepository conteudoPromocionalRepository;

    @Mock
    private LojaRepository lojaRepository;

    @Mock
    private SupermercadoRepository supermercadoRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private EditarConteudoPromocional editarConteudoPromocional;

    @BeforeEach
    void configurar() {
        editarConteudoPromocional = new EditarConteudoPromocional(
                conteudoPromocionalRepository, lojaRepository, supermercadoRepository, auditoriaRepository);
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

    private static ConteudoPromocional existente() {
        Instant agora = Instant.now();
        return new ConteudoPromocional(
                CONTEUDO_ID, SUPERMERCADO_ID, TipoConteudoPromocional.MENSAGEM, "Título antigo", "Texto antigo", null, null,
                Set.of(LOJA_ID), agora, agora.plus(1, ChronoUnit.DAYS), 0, EstadoConteudoPromocional.VIGENTE, 0L, agora, agora
        );
    }

    private static DadosConteudoPromocional dadosEditados() {
        Instant agora = Instant.now();
        return new DadosConteudoPromocional(
                null, "Título novo", "Texto novo", null, null, Set.of(LOJA_ID), agora, agora.plus(2, ChronoUnit.DAYS)
        );
    }

    @Test
    void deveRejeitarQuandoNaoEncontrado() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(conteudoPromocionalRepository.buscarPorIdESupermercado(CONTEUDO_ID, SUPERMERCADO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> editarConteudoPromocional.executar(
                SUPERMERCADO_ID, CONTEUDO_ID, 0L, dadosEditados(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(ConteudoPromocionalNaoEncontradoException.class);
    }

    @Test
    void deveRejeitarQuandoVersaoDesatualizada() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(conteudoPromocionalRepository.buscarPorIdESupermercado(CONTEUDO_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(existente()));

        assertThatThrownBy(() -> editarConteudoPromocional.executar(
                SUPERMERCADO_ID, CONTEUDO_ID, 999L, dadosEditados(), 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(VersaoConteudoPromocionalDesatualizadaException.class);
    }

    @Test
    void deveEditarQuandoDadosValidos() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(conteudoPromocionalRepository.buscarPorIdESupermercado(CONTEUDO_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(existente()));
        when(lojaRepository.buscarPorIdESupermercado(LOJA_ID, SUPERMERCADO_ID)).thenReturn(Optional.of(lojaNoSupermercado()));
        when(conteudoPromocionalRepository.salvar(any(ConteudoPromocional.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        ConteudoPromocional resultado = editarConteudoPromocional.executar(
                SUPERMERCADO_ID, CONTEUDO_ID, 0L, dadosEditados(), 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.titulo()).isEqualTo("Título novo");
        assertThat(resultado.texto()).isEqualTo("Texto novo");
    }
}
