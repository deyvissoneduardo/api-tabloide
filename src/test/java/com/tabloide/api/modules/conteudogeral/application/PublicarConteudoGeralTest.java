package com.tabloide.api.modules.conteudogeral.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.conteudogeral.domain.ConteudoGeral;
import com.tabloide.api.modules.conteudogeral.domain.ConteudoGeralRepository;
import com.tabloide.api.modules.conteudogeral.domain.EstadoConteudoGeral;
import com.tabloide.api.modules.conteudogeral.domain.TipoConteudoGeral;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublicarConteudoGeralTest {

    private static final Long ID_NOVO = 1L;
    private static final Long ID_ANTERIOR = 2L;

    @Mock
    private ConteudoGeralRepository conteudoGeralRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private PublicarConteudoGeral publicarConteudoGeral;

    @BeforeEach
    void configurar() {
        publicarConteudoGeral = new PublicarConteudoGeral(conteudoGeralRepository, auditoriaRepository);
    }

    private static ConteudoGeral rascunho(Long id) {
        Instant agora = Instant.now();
        return new ConteudoGeral(id, TipoConteudoGeral.TERMOS_USO, "Termos", "Corpo", EstadoConteudoGeral.RASCUNHO, 0L, agora, agora, null);
    }

    private static ConteudoGeral publicado(Long id) {
        Instant agora = Instant.now();
        return new ConteudoGeral(id, TipoConteudoGeral.TERMOS_USO, "Termos antigos", "Corpo antigo", EstadoConteudoGeral.PUBLICADO, 0L, agora, agora, agora);
    }

    @Test
    void devePublicarSemArquivarQuandoNaoHaPublicadoAnterior() {
        when(conteudoGeralRepository.buscarPorId(ID_NOVO)).thenReturn(Optional.of(rascunho(ID_NOVO)));
        when(conteudoGeralRepository.buscarPublicadoPorTipo(TipoConteudoGeral.TERMOS_USO)).thenReturn(Optional.empty());
        when(conteudoGeralRepository.salvar(any(ConteudoGeral.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        ConteudoGeral resultado = publicarConteudoGeral.executar(ID_NOVO, 0L, 1L, Perfil.SUPER_ADMIN);

        assertThat(resultado.estado()).isEqualTo(EstadoConteudoGeral.PUBLICADO);
        verify(auditoriaRepository, times(1)).registrar(any());
    }

    @Test
    void devePublicarEArquivarPublicadoAnteriorDoMesmoTipo() {
        when(conteudoGeralRepository.buscarPorId(ID_NOVO)).thenReturn(Optional.of(rascunho(ID_NOVO)));
        when(conteudoGeralRepository.buscarPublicadoPorTipo(TipoConteudoGeral.TERMOS_USO)).thenReturn(Optional.of(publicado(ID_ANTERIOR)));
        when(conteudoGeralRepository.salvar(any(ConteudoGeral.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        ConteudoGeral resultado = publicarConteudoGeral.executar(ID_NOVO, 0L, 1L, Perfil.SUPER_ADMIN);

        assertThat(resultado.estado()).isEqualTo(EstadoConteudoGeral.PUBLICADO);
        verify(conteudoGeralRepository, times(2)).salvar(any(ConteudoGeral.class));
        verify(auditoriaRepository, times(2)).registrar(any());
    }
}
