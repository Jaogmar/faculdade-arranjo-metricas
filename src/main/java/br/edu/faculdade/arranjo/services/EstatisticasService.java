package br.edu.faculdade.arranjo.services;

import br.edu.faculdade.arranjo.dto.WilcoxonResultadoDto;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;

public final class EstatisticasService {
    private EstatisticasService() {
    }

    public static long media(long[] valores) {
        validarVetor(valores);
        long soma = 0L;
        for (long valor : valores) {
            soma += valor;
        }
        return Math.round((double) soma / valores.length);
    }

    public static double desvioPadrao(long[] valores) {
        validarVetor(valores);
        double[] emDouble = toDoubleArray(valores);
        StandardDeviation standardDeviation = new StandardDeviation(false);
        return standardDeviation.evaluate(emDouble);
    }

    public static WilcoxonResultadoDto wilcoxonPareado(long[] amostraA, long[] amostraB) {
        validarPares(amostraA, amostraB);
        double[] a = toDoubleArray(amostraA);
        double[] b = toDoubleArray(amostraB);

        WilcoxonSignedRankTest test = new WilcoxonSignedRankTest();
        double estatistica = test.wilcoxonSignedRank(a, b);
        double pValor = test.wilcoxonSignedRankTest(a, b, false);

        return new WilcoxonResultadoDto(estatistica, pValor, amostraA.length);
    }

    public static double[] toDoubleArray(long[] valores) {
        double[] resultado = new double[valores.length];
        for (int i = 0; i < valores.length; i++) {
            resultado[i] = valores[i];
        }
        return resultado;
    }

    private static void validarVetor(long[] valores) {
        if (valores == null || valores.length == 0) {
            throw new IllegalArgumentException("A amostra precisa ter pelo menos um valor.");
        }
    }

    private static void validarPares(long[] amostraA, long[] amostraB) {
        validarVetor(amostraA);
        validarVetor(amostraB);
        if (amostraA.length != amostraB.length) {
            throw new IllegalArgumentException("As amostras devem ter o mesmo tamanho.");
        }
    }
}
