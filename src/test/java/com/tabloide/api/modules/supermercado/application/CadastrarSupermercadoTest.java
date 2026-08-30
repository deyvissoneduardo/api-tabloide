package com.tabloide.api.modules.supermercado.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Cnpj;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.supermercado.domain.Endereco;
import com.tabloide.api.modules.supermercado.domain.Supermercado;
import com.tabloide.api.modules.supermercado.domain.SupermercadoRepository;
import com.tabloide.api.modules.supermercado.domain.exceptions.CnpjJaCadastradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CadastrarSupermercadoTest {

    private static final Endereco ENDERECO = new Endereco("01310-100", "Av. Paulista", "1000", "Bela Vista", "São Paulo", "SP");

    @Mock
    private SupermercadoRepository supermercadoRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private CadastrarSupermercado cadastrarSupermercado;

    @BeforeEach
    void configurar() {
        cadastrarSupermercado = new CadastrarSupermercado(supermercadoRepository, auditoriaRepository);
    }

    private static DadosSupermercado dadosValidos() {
        return new DadosSupermercado("Razão Social LTDA", "Mercado Bom Preço", "contato@mercado.com", "11999998888", ENDERECO, null, null, null);
    }

    @Test
    void deveRejeitarCnpjJaCadastrado() {
        Cnpj cnpj = new Cnpj("11222333000181");
        when(supermercadoRepository.existePorCnpj(cnpj)).thenReturn(true);

        assertThatThrownBy(() -> cadastrarSupermercado.executar(cnpj, dadosValidos(), 1L, Perfil.SUPER_ADMIN))
                .isInstanceOf(CnpjJaCadastradoException.class);

        verify(supermercadoRepository, never()).salvar(any());
        verify(auditoriaRepository, never()).registrar(any());
    }

    @Test
    void deveCadastrarERegistrarAuditoria() {
        Cnpj cnpj = new Cnpj("11222333000181");
        when(supermercadoRepository.existePorCnpj(cnpj)).thenReturn(false);
        when(supermercadoRepository.salvar(any(Supermercado.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Supermercado resultado = cadastrarSupermercado.executar(cnpj, dadosValidos(), 1L, Perfil.SUPER_ADMIN);

        assertThat(resultado.estaAtivo()).isTrue();
        assertThat(resultado.cnpj()).isEqualTo(cnpj);
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }
}
