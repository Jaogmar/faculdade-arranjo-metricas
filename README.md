# ArranjoOrdenado - Experimento Cientifico

Projeto Java (Maven) para implementar e avaliar a estrutura de dados `ArranjoOrdenado` para inteiros, com suporte a ordenacao crescente e decrescente.

## Diagrama de Classes (UML)

```mermaid
classDiagram
    %% Enums
    class EstrategiaInsercaoEnum {
        <<enumeration>>
        LINEAR
        BINARIA
    }

    class OperacaoExperimentoEnum {
        <<enumeration>>
        INSERCAO
        EXCLUSAO
    }

    class TipoEntradaEnum {
        <<enumeration>>
        CRESCENTE
        DECRESCENTE
        ALEATORIA
    }

    class TipoOrdenacaoEnum {
        <<enumeration>>
        CRESCENTE
        DECRESCENTE
    }

    %% Models
    class ArranjoOrdenadoModel {
        -int[] dados
        -int tamanho
        -TipoOrdenacaoEnum tipoOrdenacao
        -EstrategiaInsercaoEnum estrategiaInsercao
        +inserir(int valor): void
        +excluir(int valor): boolean
        +buscar(int valor): boolean
        +tamanho(): int
        +capacidade(): int
        +estaVazio(): boolean
        +estaCheio(): boolean
        +toArray(): int[]
        +getTipoOrdenacao(): TipoOrdenacaoEnum
    }

    class CenarioExperimentoModel {
        <<record>>
        +OperacaoExperimentoEnum operacao
        +TipoOrdenacaoEnum ordenacao
        +TipoEntradaEnum entrada
        +EstrategiaInsercaoEnum estrategiaInsercao
        +id(): String
    }

    %% DTOs
    class LinhaBrutaDto {
        <<record>>
        +int execucao
        +CenarioExperimentoModel cenario
        +long tempoNs
        +long seed
    }

    class ResultadoResumoDto {
        <<record>>
        +CenarioExperimentoModel cenario
        +long media
        +double desvioPadrao
        +int execucoes
        +mediaMaisMenosDesvio(): String
    }

    class ResultadoWilcoxonDto {
        <<record>>
        +OperacaoExperimentoEnum operacao
        +TipoOrdenacaoEnum ordenacao
        +TipoEntradaEnum entrada
        +double estatistica
        +double pValor
        +int tamanhoAmostra
    }

    class WilcoxonResultadoDto {
        <<record>>
        +double estatistica
        +double pValor
        +int tamanhoAmostra
    }

    %% Services
    class EstatisticasService {
        <<service>>
        +media(long[]): long
        +desvioPadrao(long[]): double
        +wilcoxonPareado(long[], long[]): WilcoxonResultadoDto
        ~toDoubleArray(long[]): double[]
    }

    class GeradorEntradasService {
        <<service>>
        +gerar(TipoEntradaEnum, int, Random): int[]
    }

    class ExportadorResultadosService {
        <<service>>
        +exportarBruto(Path, List): void
        +exportarResumoCsv(Path, List): void
        +exportarWilcoxonCsv(Path, List): void
        +exportarTabelaMarkdown(Path, Map): void
    }

    class ExperimentoInsercaoExclusaoService {
        <<service>>
        +main(String[]): void
        -executarCenario(...): long[]
        -gerarResumos(Map): List
        -gerarWilcoxon(Map, Config): List
    }

    %% Relacionamentos
    ArranjoOrdenadoModel --> TipoOrdenacaoEnum
    ArranjoOrdenadoModel --> EstrategiaInsercaoEnum

    CenarioExperimentoModel --> OperacaoExperimentoEnum
    CenarioExperimentoModel --> TipoOrdenacaoEnum
    CenarioExperimentoModel --> TipoEntradaEnum
    CenarioExperimentoModel --> EstrategiaInsercaoEnum

    LinhaBrutaDto --> CenarioExperimentoModel
    ResultadoResumoDto --> CenarioExperimentoModel
    ResultadoWilcoxonDto --> OperacaoExperimentoEnum
    ResultadoWilcoxonDto --> TipoOrdenacaoEnum
    ResultadoWilcoxonDto --> TipoEntradaEnum

    EstatisticasService --> WilcoxonResultadoDto
    GeradorEntradasService --> TipoEntradaEnum
    ExportadorResultadosService --> LinhaBrutaDto
    ExportadorResultadosService --> ResultadoResumoDto
    ExportadorResultadosService --> ResultadoWilcoxonDto

    ExperimentoInsercaoExclusaoService --> GeradorEntradasService
    ExperimentoInsercaoExclusaoService --> ExportadorResultadosService
    ExperimentoInsercaoExclusaoService --> EstatisticasService
    ExperimentoInsercaoExclusaoService --> ArranjoOrdenadoModel
    ExperimentoInsercaoExclusaoService --> CenarioExperimentoModel
    ExperimentoInsercaoExclusaoService --> LinhaBrutaDto
    ExperimentoInsercaoExclusaoService --> ResultadoResumoDto
    ExperimentoInsercaoExclusaoService --> ResultadoWilcoxonDto
```

## Como executar
### 1. Rodar testes unitarios
```bash
mvn clean test
```

### 2. Rodar experimento completo (oficial)
```bash
mvn exec:java
```

Parametros padrao do experimento:
- capacidade: `100000`
- execucoes por cenario: `100`
- comparacao linear x binaria: habilitada

### 3. Rodar experimento reduzido (validacao rapida)
```bash
mvn exec:java -Dexec.args="--capacidade=10000 --execucoes=10"
```

### 4. Desabilitar comparacao binaria (somente algoritmo linear)
```bash
mvn exec:java -Dexec.args="--sem-comparacao-binaria"
```

## Arquivos gerados em `resultados/`
- `tempos_brutos.csv`: tempos de cada execucao.
- `resumo.csv`: media e desvio padrao por cenario.
- `tabela_resultados.md`: tabela no formato `media +/- desvio`.
- `wilcoxon.csv`: estatistica e p-valor do teste de Wilcoxon.

## Tabela esperada (formato)
Exemplo de tabulacao:

| Insercao | Ordenacao crescente | Ordenacao decrescente |
|---|---|---|
| Insercao em maneira crescente | 10 +/- 0.3434 | 10 +/- 0.3434 |
| Insercao em maneira decrescente | 10 +/- 0.3434 | 10 +/- 0.3434 |
| Insercao em maneira aleatoria | 10 +/- 0.3434 | 10 +/- 0.3434 |
