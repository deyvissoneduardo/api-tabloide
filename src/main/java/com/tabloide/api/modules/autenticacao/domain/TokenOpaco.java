package com.tabloide.api.modules.autenticacao.domain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

public record TokenOpaco(String valorBruto, String hash) {

    private static final SecureRandom GERADOR_ALEATORIO = new SecureRandom();

    public static TokenOpaco gerar() {
        byte[] bytesAleatorios = new byte[32];
        GERADOR_ALEATORIO.nextBytes(bytesAleatorios);
        String valorBruto = Base64.getUrlEncoder().withoutPadding().encodeToString(bytesAleatorios);
        return new TokenOpaco(valorBruto, hash(valorBruto));
    }

    public static String hash(String valorBruto) {
        try {
            MessageDigest digestor = MessageDigest.getInstance("SHA-256");
            byte[] digest = digestor.digest(valorBruto.getBytes());
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 indisponível na JVM", e);
        }
    }
}
