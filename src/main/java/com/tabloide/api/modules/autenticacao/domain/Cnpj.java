package com.tabloide.api.modules.autenticacao.domain;

import java.util.Objects;

public final class Cnpj {

    private static final int[] PESOS_PRIMEIRO_DIGITO = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] PESOS_SEGUNDO_DIGITO = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private final String valor;

    public Cnpj(String informado) {
        String normalizado = normalizar(informado);
        validar(normalizado);
        this.valor = normalizado;
    }

    private static String normalizar(String informado) {
        if (informado == null) {
            throw new IllegalArgumentException("CNPJ não pode ser nulo");
        }
        return informado.replaceAll("\\D", "");
    }

    private static void validar(String cnpj) {
        if (cnpj.length() != 14) {
            throw new IllegalArgumentException("CNPJ deve conter 14 dígitos");
        }
        if (todosDigitosIguais(cnpj)) {
            throw new IllegalArgumentException("CNPJ inválido");
        }
        int primeiroDigito = calcularDigitoVerificador(cnpj.substring(0, 12), PESOS_PRIMEIRO_DIGITO);
        int segundoDigito = calcularDigitoVerificador(cnpj.substring(0, 12) + primeiroDigito, PESOS_SEGUNDO_DIGITO);
        String digitosCalculados = "" + primeiroDigito + segundoDigito;
        if (!cnpj.substring(12).equals(digitosCalculados)) {
            throw new IllegalArgumentException("CNPJ inválido");
        }
    }

    private static boolean todosDigitosIguais(String cnpj) {
        return cnpj.chars().distinct().count() == 1;
    }

    private static int calcularDigitoVerificador(String base, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < base.length(); i++) {
            soma += Character.getNumericValue(base.charAt(i)) * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    public String valor() {
        return valor;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Cnpj outro)) {
            return false;
        }
        return valor.equals(outro.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}
