package br.edu.faculdade.arranjo.services;

import br.edu.faculdade.arranjo.enums.TipoEntradaEnum;

import java.util.Random;

public class GeradorEntradasService {
    public int[] gerar(TipoEntradaEnum tipoEntrada, int quantidade, Random random) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade nao pode ser negativa.");
        }

        int[] valores = new int[quantidade];

        switch (tipoEntrada) {
            case CRESCENTE -> {
                for (int i = 0; i < quantidade; i++) {
                    valores[i] = i;
                }
            }
            case DECRESCENTE -> {
                for (int i = 0; i < quantidade; i++) {
                    valores[i] = quantidade - 1 - i;
                }
            }
            case ALEATORIA -> {
                for (int i = 0; i < quantidade; i++) {
                    valores[i] = random.nextInt(quantidade * 10);
                }
            }
            default -> throw new IllegalStateException("Tipo de entrada nao suportado: " + tipoEntrada);
        }

        return valores;
    }
}
