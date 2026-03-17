package br.edu.faculdade.arranjo.model;

import br.edu.faculdade.arranjo.enums.EstrategiaInsercaoEnum;
import br.edu.faculdade.arranjo.enums.TipoOrdenacaoEnum;

import java.util.Arrays;

public class ArranjoOrdenadoModel {
    private final int[] dados;
    private int tamanho;
    private final TipoOrdenacaoEnum tipoOrdenacao;
    private final EstrategiaInsercaoEnum estrategiaInsercao;

    public ArranjoOrdenadoModel(int capacidade, TipoOrdenacaoEnum tipoOrdenacao) {
        this(capacidade, tipoOrdenacao, EstrategiaInsercaoEnum.LINEAR);
    }

    public ArranjoOrdenadoModel(int capacidade, TipoOrdenacaoEnum tipoOrdenacao, EstrategiaInsercaoEnum estrategiaInsercao) {
        if (capacidade <= 0) {
            throw new IllegalArgumentException("Capacidade deve ser positiva.");
        }
        this.dados = new int[capacidade];
        this.tamanho = 0;
        this.tipoOrdenacao = tipoOrdenacao;
        this.estrategiaInsercao = estrategiaInsercao;
    }

    public void inserir(int valor) {
        if (estaCheio()) {
            throw new IllegalStateException("Arranjo cheio.");
        }

        int indiceInsercao = localizarIndiceInsercao(valor);
        if (tamanho - indiceInsercao >= 0) {
            System.arraycopy(dados, indiceInsercao, dados, indiceInsercao + 1, tamanho - indiceInsercao);
        }
        dados[indiceInsercao] = valor;
        tamanho++;
    }

    public boolean excluir(int valor) {
        int indice = buscarIndicePrimeiraOcorrencia(valor);
        if (indice < 0) {
            return false;
        }

        if (tamanho - 1 - indice >= 0) {
            System.arraycopy(dados, indice + 1, dados, indice, tamanho - 1 - indice);
        }
        tamanho--;
        return true;
    }

    public boolean buscar(int valor) {
        return buscarIndicePrimeiraOcorrencia(valor) >= 0;
    }

    public int tamanho() {
        return tamanho;
    }

    public int capacidade() {
        return dados.length;
    }

    public boolean estaVazio() {
        return tamanho == 0;
    }

    public boolean estaCheio() {
        return tamanho == dados.length;
    }

    public int[] toArray() {
        return Arrays.copyOf(dados, tamanho);
    }

    public TipoOrdenacaoEnum getTipoOrdenacao() {
        return tipoOrdenacao;
    }

    public EstrategiaInsercaoEnum getEstrategiaInsercao() {
        return estrategiaInsercao;
    }

    private int localizarIndiceInsercao(int valor) {
        if (estrategiaInsercao == EstrategiaInsercaoEnum.BINARIA) {
            return localizarIndiceInsercaoBinaria(valor);
        }
        return localizarIndiceInsercaoLinear(valor);
    }

    private int localizarIndiceInsercaoLinear(int valor) {
        int i = 0;
        while (i < tamanho && comparar(dados[i], valor) <= 0) {
            i++;
        }
        return i;
    }

    private int localizarIndiceInsercaoBinaria(int valor) {
        int inicio = 0;
        int fim = tamanho;

        // Upper bound: retorna a primeira posicao cujo elemento eh maior que o valor na ordem escolhida.
        while (inicio < fim) {
            int meio = inicio + (fim - inicio) / 2;
            if (comparar(dados[meio], valor) <= 0) {
                inicio = meio + 1;
            } else {
                fim = meio;
            }
        }

        return inicio;
    }

    private int buscarIndicePrimeiraOcorrencia(int valor) {
        int posicao = buscaBinariaQualquerOcorrencia(valor);
        if (posicao < 0) {
            return -1;
        }

        while (posicao > 0 && dados[posicao - 1] == valor) {
            posicao--;
        }

        return posicao;
    }

    private int buscaBinariaQualquerOcorrencia(int valor) {
        int inicio = 0;
        int fim = tamanho - 1;

        while (inicio <= fim) {
            int meio = inicio + (fim - inicio) / 2;
            int comparacao = comparar(dados[meio], valor);

            if (comparacao == 0) {
                return meio;
            }

            if (comparacao < 0) {
                inicio = meio + 1;
            } else {
                fim = meio - 1;
            }
        }

        return -1;
    }

    // Compara valores respeitando a ordenacao escolhida.
    private int comparar(int a, int b) {
        if (tipoOrdenacao == TipoOrdenacaoEnum.CRESCENTE) {
            return Integer.compare(a, b);
        }
        return Integer.compare(b, a);
    }
}
