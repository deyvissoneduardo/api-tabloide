package com.tabloide.api.modules.publicacao.application;

// RN-003/RN-011 (Página pública): identidade do supermercado/loja, sem dados administrativos.
public record LojaPublica(Long supermercadoId, String nomeSupermercado, Long lojaId, String nomeLoja) {
}
