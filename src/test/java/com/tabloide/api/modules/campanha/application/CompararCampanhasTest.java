package com.tabloide.api.modules.campanha.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.campanha.domain.Campanha;
import com.tabloide.api.modules.campanha.domain.CampanhaRepository;
import com.tabloide.api.modules.campanha.domain.EstadoCampanha;
import com.tabloide.api.modules.campanha.domain.exceptions.CampanhaNaoEncontradaException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompararCampanhasTest {

    private static final Long SUPERMERCADO_ID = 1L;

    @Mock
    private CampanhaRepository campanhaRepository;

    private CompararCampanhas compararCampanhas;

    @BeforeEach
    void configurar() {
        compararCampanhas = new CompararCampanhas(campanhaRepository);
    }

    @Test
    void deveRejeitarQuandoForaDoEscopoDoAtor() {
        assertThatThrownBy(() -> compararCampanhas.executar(SUPERMERCADO_ID, Set.of(1L, 2L), 2L))
                .isInstanceOf(CampanhaNaoEncontradaException.class);
    }

    @Test
    void deveRetornarCampanhasDoSupermercado() {
        Instant agora = Instant.now();
        Campanha campanhaUm = new Campanha(1L, SUPERMERCADO_ID, "Campanha Um", null, Set.of(10L), Set.of(20L),
                agora, agora.plus(1, ChronoUnit.DAYS), EstadoCampanha.VIGENTE, 0L, agora, agora);
        Campanha campanhaDois = new Campanha(2L, SUPERMERCADO_ID, "Campanha Dois", null, Set.of(10L, 11L), Set.of(),
                agora, agora.plus(1, ChronoUnit.DAYS), EstadoCampanha.RASCUNHO, 0L, agora, agora);
        when(campanhaRepository.buscarPorIdsESupermercado(Set.of(1L, 2L), SUPERMERCADO_ID)).thenReturn(List.of(campanhaUm, campanhaDois));

        List<Campanha> resultado = compararCampanhas.executar(SUPERMERCADO_ID, Set.of(1L, 2L), SUPERMERCADO_ID);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).quantidadeOfertas()).isEqualTo(1);
        assertThat(resultado.get(1).quantidadeLojas()).isEqualTo(2);
    }
}
