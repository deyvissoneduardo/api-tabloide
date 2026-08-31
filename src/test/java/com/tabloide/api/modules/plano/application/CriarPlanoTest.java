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
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CriarPlanoTest {

    @Mock
    private PlanoRepository planoRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private CriarPlano criarPlano;

    @BeforeEach
    void configurar() {
        criarPlano = new CriarPlano(planoRepository, auditoriaRepository);
    }

    @Test
    void deveCriarEAuditarQuandoNomeDisponivel() {
        when(planoRepository.existeComNomeNormalizado("básico", null)).thenReturn(false);
        when(planoRepository.salvar(any(Plano.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Plano resultado = criarPlano.executar(new DadosPlano("Básico", 30, BigDecimal.valueOf(99.90), 100, null), 1L, Perfil.SUPER_ADMIN);

        assertThat(resultado.nome()).isEqualTo("Básico");
        assertThat(resultado.estaExcluido()).isFalse();
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }

    @Test
    void deveLancarNomeDuplicadoQuandoJaExistePlanoDisponivelComMesmoNomeNormalizado() {
        when(planoRepository.existeComNomeNormalizado("básico", null)).thenReturn(true);

        assertThatThrownBy(() -> criarPlano.executar(new DadosPlano("Básico", 30, BigDecimal.valueOf(99.90), 100, null), 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(NomeDePlanoJaCadastradoException.class);
    }
}
