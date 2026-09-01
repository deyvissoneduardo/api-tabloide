package com.tabloide.api.modules.imagem.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tabloide.api.modules.auditoria.domain.AuditoriaRepository;
import com.tabloide.api.modules.auditoria.domain.RegistroAuditoria;
import com.tabloide.api.modules.autenticacao.domain.Perfil;
import com.tabloide.api.modules.imagem.domain.FormatoImagem;
import com.tabloide.api.modules.imagem.domain.Imagem;
import com.tabloide.api.modules.imagem.domain.ImagemRepository;
import com.tabloide.api.modules.imagem.domain.TipoVinculoImagem;
import com.tabloide.api.modules.imagem.domain.exceptions.ImagemNaoEncontradaException;
import com.tabloide.api.modules.imagem.domain.exceptions.ImagemVinculadaNaoPodeSerExcluidaException;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExcluirImagemTest {

    private static final Long SUPERMERCADO_ID = 1L;

    @Mock
    private ImagemRepository imagemRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    private ExcluirImagem excluirImagem;

    @BeforeEach
    void configurar() {
        excluirImagem = new ExcluirImagem(imagemRepository, auditoriaRepository);
    }

    @Test
    void deveRejeitarQuandoNaoEncontrada() {
        when(imagemRepository.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> excluirImagem.executar(1L, 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(ImagemNaoEncontradaException.class);
    }

    @Test
    void deveRejeitarQuandoForaDoEscopoDoAtor() {
        Imagem imagem = Imagem.registrar(2L, "Banner", TipoVinculoImagem.BANNER, null, "url", FormatoImagem.JPG, 1024, Instant.now());
        when(imagemRepository.buscarPorId(1L)).thenReturn(Optional.of(imagem));

        assertThatThrownBy(() -> excluirImagem.executar(1L, 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(ImagemNaoEncontradaException.class);
    }

    @Test
    void deveRejeitarQuandoVinculada() {
        Imagem imagem = Imagem.registrar(SUPERMERCADO_ID, "Produto", TipoVinculoImagem.PRODUTO, 99L, "url", FormatoImagem.JPG, 1024, Instant.now());
        when(imagemRepository.buscarPorId(1L)).thenReturn(Optional.of(imagem));

        assertThatThrownBy(() -> excluirImagem.executar(1L, 1L, Perfil.DONO, SUPERMERCADO_ID))
                .isInstanceOf(ImagemVinculadaNaoPodeSerExcluidaException.class);

        verify(imagemRepository, never()).salvar(any());
    }

    @Test
    void deveExcluirQuandoNaoVinculada() {
        Imagem imagem = Imagem.registrar(SUPERMERCADO_ID, "Banner", TipoVinculoImagem.BANNER, null, "url", FormatoImagem.JPG, 1024, Instant.now());
        when(imagemRepository.buscarPorId(1L)).thenReturn(Optional.of(imagem));
        when(imagemRepository.salvar(any(Imagem.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Imagem resultado = excluirImagem.executar(1L, 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.estaAtiva()).isFalse();
        verify(auditoriaRepository).registrar(any(RegistroAuditoria.class));
    }

    @Test
    void deveSerIdempotenteQuandoJaExcluida() {
        Imagem imagem = Imagem.registrar(SUPERMERCADO_ID, "Banner", TipoVinculoImagem.BANNER, null, "url", FormatoImagem.JPG, 1024, Instant.now());
        imagem.excluir(Instant.now());
        when(imagemRepository.buscarPorId(1L)).thenReturn(Optional.of(imagem));

        Imagem resultado = excluirImagem.executar(1L, 1L, Perfil.DONO, SUPERMERCADO_ID);

        assertThat(resultado.estaAtiva()).isFalse();
        verify(imagemRepository, never()).salvar(any());
        verify(auditoriaRepository, never()).registrar(any());
    }
}
