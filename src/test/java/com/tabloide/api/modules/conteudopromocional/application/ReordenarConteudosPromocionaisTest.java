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
import com.tabloide.api.modules.conteudopromocional.domain.exceptions.PosicoesConteudoPromocionalInvalidasException;
import com.tabloide.api.modules.supermercado.domain.EstadoSupermercado;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReordenarConteudosPromocionaisTest {

    private static final Long SUPERMERCADO_ID = 1L;

    @Mock
    private ConteudoPromocionalRepository conteudoPromocionalRepository;

    @Mock
    private SupermercadoRepository supermercadoRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private ReordenarConteudosPromocionais reordenarConteudosPromocionais;

    @BeforeEach
    void configurar() {
        reordenarConteudosPromocionais = new ReordenarConteudosPromocionais(conteudoPromocionalRepository, supermercadoRepository, auditoriaRepository);
    }

    private static Supermercado supermercadoAtivo() {
        Instant agora = Instant.now();
        return new Supermercado(SUPERMERCADO_ID, null, "Razão", "Fantasia", "e@e.com", "119999", null, null, null, null,
                EstadoSupermercado.ATIVO, 0L, agora, agora);
    }

    // Simula um registro já persistido (com id), como o repositório devolveria.
    private static ConteudoPromocional comId(Long id, int posicaoAtual) {
        Instant agora = Instant.now();
        return new ConteudoPromocional(
                id, SUPERMERCADO_ID, TipoConteudoPromocional.MENSAGEM, "Título " + id, "Texto", null, null,
                Set.of(100L), agora, agora.plus(1, ChronoUnit.DAYS), posicaoAtual, EstadoConteudoPromocional.VIGENTE,
                0L, agora, agora
        );
    }

    @Test
    void deveRejeitarQuandoListaNaoContemTodosOsIds() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(conteudoPromocionalRepository.listarPorSupermercadoOrdenados(SUPERMERCADO_ID))
                .thenReturn(List.of(comId(1L, 0), comId(2L, 1)));

        assertThatThrownBy(() -> reordenarConteudosPromocionais.executar(SUPERMERCADO_ID, List.of(1L), 9L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(PosicoesConteudoPromocionalInvalidasException.class);
    }

    @Test
    void deveRejeitarQuandoHaIdDuplicado() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(conteudoPromocionalRepository.listarPorSupermercadoOrdenados(SUPERMERCADO_ID))
                .thenReturn(List.of(comId(1L, 0), comId(2L, 1)));

        assertThatThrownBy(() -> reordenarConteudosPromocionais.executar(SUPERMERCADO_ID, List.of(1L, 1L), 9L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(PosicoesConteudoPromocionalInvalidasException.class);
    }

    @Test
    void deveReordenarAtribuindoPosicaoPorIndice() {
        when(supermercadoRepository.buscarPorId(SUPERMERCADO_ID)).thenReturn(Optional.of(supermercadoAtivo()));
        when(conteudoPromocionalRepository.listarPorSupermercadoOrdenados(SUPERMERCADO_ID))
                .thenReturn(List.of(comId(1L, 0), comId(2L, 1)));
        when(conteudoPromocionalRepository.salvar(any(ConteudoPromocional.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        List<ConteudoPromocional> resultado = reordenarConteudosPromocionais.executar(SUPERMERCADO_ID, List.of(2L, 1L), 9L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.get(0).id()).isEqualTo(2L);
        assertThat(resultado.get(0).posicao()).isEqualTo(0);
        assertThat(resultado.get(1).id()).isEqualTo(1L);
        assertThat(resultado.get(1).posicao()).isEqualTo(1);
    }
}
