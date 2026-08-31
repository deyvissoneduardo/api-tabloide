package com.tabloide.api.modules.operation.application;

import com.tabloide.api.modules.operation.domain.SeveridadeOcorrencia;

public record DadosAbrirOcorrencia(String titulo, String descricao, SeveridadeOcorrencia severidade, Long supermercadoId, Long responsavelId) {
}
