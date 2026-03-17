package br.edu.faculdade.arranjo.model;

import br.edu.faculdade.arranjo.enums.EstrategiaInsercaoEnum;
import br.edu.faculdade.arranjo.enums.TipoOrdenacaoEnum;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ArranjoOrdenadoModelTest {

    @Test
    void deveInserirEmOrdemCrescenteComEntradaCrescente() {
        ArranjoOrdenadoModel arranjo = new ArranjoOrdenadoModel(10, TipoOrdenacaoEnum.CRESCENTE);

        for (int i = 0; i < 10; i++) {
            arranjo.inserir(i);
        }

        assertArrayEquals(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, arranjo.toArray());
    }

    @Test
    void deveInserirEmOrdemCrescenteComEntradaDecrescente() {
        ArranjoOrdenadoModel arranjo = new ArranjoOrdenadoModel(10, TipoOrdenacaoEnum.CRESCENTE);

        for (int i = 9; i >= 0; i--) {
            arranjo.inserir(i);
        }

        assertArrayEquals(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, arranjo.toArray());
    }

    @Test
    void deveInserirEmOrdemDecrescenteComEntradaCrescente() {
        ArranjoOrdenadoModel arranjo = new ArranjoOrdenadoModel(10, TipoOrdenacaoEnum.DECRESCENTE);

        for (int i = 0; i < 10; i++) {
            arranjo.inserir(i);
        }

        assertArrayEquals(new int[]{9, 8, 7, 6, 5, 4, 3, 2, 1, 0}, arranjo.toArray());
    }

    @Test
    void deveInserirEmOrdemDecrescenteComEntradaAleatoria() {
        ArranjoOrdenadoModel arranjo = new ArranjoOrdenadoModel(20, TipoOrdenacaoEnum.DECRESCENTE);
        Random random = new Random(42);

        for (int i = 0; i < 20; i++) {
            arranjo.inserir(random.nextInt(100));
        }

        int[] vetor = arranjo.toArray();
        for (int i = 1; i < vetor.length; i++) {
            assertTrue(vetor[i - 1] >= vetor[i]);
        }
    }

    @Test
    void deveExcluirTodosElementos() {
        ArranjoOrdenadoModel arranjo = new ArranjoOrdenadoModel(5, TipoOrdenacaoEnum.CRESCENTE);
        arranjo.inserir(10);
        arranjo.inserir(20);
        arranjo.inserir(30);

        assertTrue(arranjo.excluir(20));
        assertTrue(arranjo.excluir(10));
        assertTrue(arranjo.excluir(30));

        assertTrue(arranjo.estaVazio());
        assertEquals(0, arranjo.tamanho());
    }

    @Test
    void deveRetornarFalseAoExcluirInexistente() {
        ArranjoOrdenadoModel arranjo = new ArranjoOrdenadoModel(3, TipoOrdenacaoEnum.CRESCENTE);
        arranjo.inserir(1);
        arranjo.inserir(2);

        assertFalse(arranjo.excluir(3));
        assertArrayEquals(new int[]{1, 2}, arranjo.toArray());
    }

    @Test
    void deveLancarExcecaoQuandoCheio() {
        ArranjoOrdenadoModel arranjo = new ArranjoOrdenadoModel(2, TipoOrdenacaoEnum.CRESCENTE);
        arranjo.inserir(1);
        arranjo.inserir(2);

        assertThrows(IllegalStateException.class, () -> arranjo.inserir(3));
    }

    @Test
    void deveBuscarElementoExistenteENaoExistente() {
        ArranjoOrdenadoModel arranjo = new ArranjoOrdenadoModel(5, TipoOrdenacaoEnum.CRESCENTE);
        arranjo.inserir(4);
        arranjo.inserir(2);
        arranjo.inserir(9);

        assertTrue(arranjo.buscar(4));
        assertFalse(arranjo.buscar(7));
    }

    @Test
    void deveGerarMesmoConteudoParaLinearEBinaria() {
        int[] valores = {7, 1, 5, 9, 2, 8, 3, 6, 4};

        ArranjoOrdenadoModel linear = new ArranjoOrdenadoModel(20, TipoOrdenacaoEnum.CRESCENTE, EstrategiaInsercaoEnum.LINEAR);
        ArranjoOrdenadoModel binaria = new ArranjoOrdenadoModel(20, TipoOrdenacaoEnum.CRESCENTE, EstrategiaInsercaoEnum.BINARIA);

        for (int valor : valores) {
            linear.inserir(valor);
            binaria.inserir(valor);
        }

        assertArrayEquals(linear.toArray(), binaria.toArray());
    }

    @Test
    void deveRemoverPrimeiraOcorrenciaQuandoHaDuplicados() {
        ArranjoOrdenadoModel arranjo = new ArranjoOrdenadoModel(10, TipoOrdenacaoEnum.CRESCENTE);
        arranjo.inserir(5);
        arranjo.inserir(5);
        arranjo.inserir(5);

        assertTrue(arranjo.excluir(5));
        assertArrayEquals(new int[]{5, 5}, arranjo.toArray());
    }
}
