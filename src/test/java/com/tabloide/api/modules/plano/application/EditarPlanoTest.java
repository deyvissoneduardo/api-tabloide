package com.tabloide.api.modules.plano.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.plano.domain.Plano;
import com.tabloide.api.modules.plano.domain.PlanoRepository;
import com.tabloide.api.modules.plano.domain.exceptions.NomeDePlanoJaCadastradoException;
import com.tabloide.api.modules.plano.domain.exceptions.PlanoExcluidoException;
import com.tabloide.api.modules.plano.domain.exceptions.PlanoNaoEncontradoException;
import com.tabloide.api.modules.plano.domain.exceptions.VersaoDesatualizadaException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EditarPlanoTest {

    @Mock
    private PlanoRepository planoRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private EditarPlano editarPlano;

    @BeforeEach
    void configurar() {
        editarPlano = new EditarPlano(planoRepository, auditoriaRepository);
    }

    private static Plano plano(Long id, String nome, Long versao, Instant excluidoEm) {
        Instant agora = Instant.now();
        return new Plano(id, nome, Plano.normalizarNome(nome), 30, BigDecimal.valueOf(99.90), 100, null, versao, agora, agora, excluidoEm);
    }

    @Test
    void deveEditarEAuditarQuandoDadosValidos() {
        when(planoRepository.buscarPorId(1L)).thenReturn(Optional.of(plano(1L, "Básico", 0L, null)));
        when(planoRepository.existeComNomeNormalizado("premium", 1L)).thenReturn(false);
        when(planoRepository.salvar(any(Plano.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Plano resultado = editarPlano.executar(1L, 0L, new DadosPlano("Premium", 60, BigDecimal.valueOf(199.90), 500, 10), 1L, Perfil.SUPER_ADMIN);

        assertThat(resultado.nome()).isEqualTo("Premium");
        assertThat(resultado.validadeDias()).isEqualTo(60);
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }

    @Test
    void deveLancarNaoEncontradoQuandoPlanoNaoExiste() {
        when(planoRepository.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> editarPlano.executar(1L, 0L, new DadosPlano("Premium", 60, BigDecimal.valueOf(199.90), 500, null), 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(PlanoNaoEncontradoException.class);
    }

    @Test
    void deveLancarPlanoExcluidoQuandoPlanoJaFoiExcluido() {
        when(planoRepository.buscarPorId(1L)).thenReturn(Optional.of(plano(1L, "Básico", 0L, Instant.now())));

        assertThatThrownBy(() -> editarPlano.executar(1L, 0L, new DadosPlano("Premium", 60, BigDecimal.valueOf(199.90), 500, null), 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(PlanoExcluidoException.class);
    }

    @Test
    void deveLancarVersaoDesatualizadaQuandoVersaoNaoConfere() {
        when(planoRepository.buscarPorId(1L)).thenReturn(Optional.of(plano(1L, "Básico", 2L, null)));

        assertThatThrownBy(() -> editarPlano.executar(1L, 0L, new DadosPlano("Premium", 60, BigDecimal.valueOf(199.90), 500, null), 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(VersaoDesatualizadaException.class);
    }

    @Test
    void deveLancarNomeDuplicadoQuandoNovoNomeColideComOutroPlano() {
        when(planoRepository.buscarPorId(1L)).thenReturn(Optional.of(plano(1L, "Básico", 0L, null)));
        when(planoRepository.existeComNomeNormalizado("premium", 1L)).thenReturn(true);

        assertThatThrownBy(() -> editarPlano.executar(1L, 0L, new DadosPlano("Premium", 60, BigDecimal.valueOf(199.90), 500, null), 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(NomeDePlanoJaCadastradoException.class);
    }
}
