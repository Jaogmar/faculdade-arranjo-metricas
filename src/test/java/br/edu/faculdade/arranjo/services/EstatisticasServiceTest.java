package br.edu.faculdade.arranjo.services;

import br.edu.faculdade.arranjo.dto.WilcoxonResultadoDto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstatisticasServiceTest {

    @Test
    void deveCalcularMediaCorretamente() {
        long[] valores = {10, 20, 30, 40};
        assertEquals(25, EstatisticasService.media(valores));
    }

    @Test
    void deveCalcularDesvioPadraoPopulacional() {
        long[] valores = {2, 4, 4, 4, 5, 5, 7, 9};
        double desvio = EstatisticasService.desvioPadrao(valores);
        assertEquals(2.0, desvio, 0.0001);
    }

    @Test
    void deveExecutarWilcoxonPareado() {
        long[] a = {10, 11, 12, 13, 14};
        long[] b = {11, 12, 13, 14, 15};

        WilcoxonResultadoDto resultado = EstatisticasService.wilcoxonPareado(a, b);

        assertTrue(resultado.estatistica() >= 0.0);
        assertTrue(resultado.pValor() >= 0.0 && resultado.pValor() <= 1.0);
        assertEquals(5, resultado.tamanhoAmostra());
    }

    @Test
    void deveValidarTamanhoDasAmostrasNoWilcoxon() {
        long[] a = {1, 2, 3};
        long[] b = {1, 2};

        assertThrows(IllegalArgumentException.class, () -> EstatisticasService.wilcoxonPareado(a, b));
    }

    @Test
    void deveValidarAmostraVazia() {
        long[] vazio = {};
        assertThrows(IllegalArgumentException.class, () -> EstatisticasService.media(vazio));
        assertThrows(IllegalArgumentException.class, () -> EstatisticasService.desvioPadrao(vazio));
    }
}
