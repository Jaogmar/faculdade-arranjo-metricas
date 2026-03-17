# ArranjoOrdenado - Experimento Cientifico

Projeto Java (Maven) para implementar e avaliar a estrutura de dados `ArranjoOrdenado` para inteiros, com suporte a ordenacao crescente e decrescente.

## Requisitos atendidos
- Implementacao da classe `ArranjoOrdenadoModel` para inteiros.
- Ordenacao crescente e decrescente.
- Testes unitarios com JUnit 5.
- Experimento de insercao e exclusao com 100 execucoes por cenario.
- Calculo de media e desvio padrao.
- Extra: comparacao de algoritmos de insercao (linear x binaria).
- Extra: teste de Wilcoxon pareado.
- Geracao de resultados tabulados em CSV e Markdown.

## Estrutura do projeto
- `src/main/java/br/edu/faculdade/arranjo/model`: modelos de dominio (`...Model`).
- `src/main/java/br/edu/faculdade/arranjo/services`: regras de negocio/execucao (`...Service`).
- `src/main/java/br/edu/faculdade/arranjo/enums`: enumeracoes (`...Enum`).
- `src/main/java/br/edu/faculdade/arranjo/dto`: estruturas de transferencia (`...Dto`).
- `src/test/java/...`: testes unitarios.
- `resultados/`: saidas do experimento.
- `apresentacao/roteiro.md`: roteiro para os slides.

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

## UML simplificada da classe principal
```text
+--------------------------------------------------+
| ArranjoOrdenadoModel                             |
+--------------------------------------------------+
| - dados: int[]                                   |
| - tamanho: int                                   |
| - tipoOrdenacao: TipoOrdenacaoEnum               |
| - estrategiaInsercao: EstrategiaInsercaoEnum     |
+--------------------------------------------------+
| + inserir(valor: int): void                      |
| + excluir(valor: int): boolean                   |
| + buscar(valor: int): boolean                    |
| + tamanho(): int                                 |
| + capacidade(): int                              |
| + estaVazio(): boolean                           |
| + estaCheio(): boolean                           |
| + toArray(): int[]                               |
+--------------------------------------------------+
```

## Checklist de entrega
- [ ] Publicar repositorio no GitHub.
- [ ] Incluir codigo da estrutura e experimento.
- [ ] Incluir testes unitarios.
- [ ] Incluir resultados em `resultados/`.
- [ ] Incluir apresentacao (ou arquivo final de slides).
- [ ] Entregar link do repositorio na ferramenta.
