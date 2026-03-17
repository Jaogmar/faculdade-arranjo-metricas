package br.edu.faculdade.arranjo.services;

import br.edu.faculdade.arranjo.dto.LinhaBrutaDto;
import br.edu.faculdade.arranjo.dto.ResultadoResumoDto;
import br.edu.faculdade.arranjo.dto.ResultadoWilcoxonDto;
import br.edu.faculdade.arranjo.dto.WilcoxonResultadoDto;
import br.edu.faculdade.arranjo.enums.EstrategiaInsercaoEnum;
import br.edu.faculdade.arranjo.enums.OperacaoExperimentoEnum;
import br.edu.faculdade.arranjo.enums.TipoEntradaEnum;
import br.edu.faculdade.arranjo.enums.TipoOrdenacaoEnum;
import br.edu.faculdade.arranjo.model.ArranjoOrdenadoModel;
import br.edu.faculdade.arranjo.model.CenarioExperimentoModel;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ExperimentoInsercaoExclusaoService {
    private static final int CAPACIDADE_PADRAO = 100_000;
    private static final int EXECUCOES_PADRAO = 100;
    private static final long SEED_BASE = 20260316L;

    public static void main(String[] args) throws IOException {
        Config config = Config.fromArgs(args);

        System.out.println("Iniciando experimento com parametros:");
        System.out.println("capacidade=" + config.capacidade());
        System.out.println("execucoes=" + config.execucoes());
        System.out.println("seedBase=" + config.seedBase());
        System.out.println("incluirComparacaoBinaria=" + config.incluirComparacaoBinaria());

        GeradorEntradasService geradorEntradas = new GeradorEntradasService();
        ExportadorResultadosService exportador = new ExportadorResultadosService();

        List<LinhaBrutaDto> linhasBrutas = new ArrayList<>();
        Map<CenarioExperimentoModel, long[]> amostrasPorCenario = new HashMap<>();

        EnumSet<EstrategiaInsercaoEnum> estrategias = config.incluirComparacaoBinaria()
                ? EnumSet.of(EstrategiaInsercaoEnum.LINEAR, EstrategiaInsercaoEnum.BINARIA)
                : EnumSet.of(EstrategiaInsercaoEnum.LINEAR);

        for (OperacaoExperimentoEnum operacao : OperacaoExperimentoEnum.values()) {
            for (TipoOrdenacaoEnum ordenacao : TipoOrdenacaoEnum.values()) {
                for (TipoEntradaEnum entrada : TipoEntradaEnum.values()) {
                    for (EstrategiaInsercaoEnum estrategia : estrategias) {
                        CenarioExperimentoModel cenario = new CenarioExperimentoModel(operacao, ordenacao, entrada, estrategia);
                        long[] amostra = executarCenario(config, cenario, geradorEntradas, linhasBrutas);
                        amostrasPorCenario.put(cenario, amostra);
                        System.out.println("Finalizado cenario: " + cenario.id());
                    }
                }
            }
        }

        List<ResultadoResumoDto> resumos = gerarResumos(amostrasPorCenario);
        Map<CenarioExperimentoModel, ResultadoResumoDto> mapaResumos = indexarResumos(resumos);
        List<ResultadoWilcoxonDto> wilcoxons = gerarWilcoxon(amostrasPorCenario, config);

        Path pastaSaida = Path.of("resultados");
        exportador.exportarBruto(pastaSaida.resolve("tempos_brutos.csv"), linhasBrutas);
        exportador.exportarResumoCsv(pastaSaida.resolve("resumo.csv"), resumos);
        exportador.exportarTabelaMarkdown(pastaSaida.resolve("tabela_resultados.md"), mapaResumos);
        exportador.exportarWilcoxonCsv(pastaSaida.resolve("wilcoxon.csv"), wilcoxons);

        System.out.println("Arquivos gerados em: " + pastaSaida.toAbsolutePath());
    }

    private static long[] executarCenario(
            Config config,
            CenarioExperimentoModel cenario,
            GeradorEntradasService geradorEntradas,
            List<LinhaBrutaDto> linhasBrutas
    ) {
        long[] tempos = new long[config.execucoes()];

        for (int execucao = 0; execucao < config.execucoes(); execucao++) {
            long seed = config.seedBase() + (long) execucao * 997 + cenario.id().hashCode();
            Random random = new Random(seed);

            int[] valores = geradorEntradas.gerar(cenario.entrada(), config.capacidade(), random);

            long t1 = System.nanoTime();
            if (cenario.operacao() == OperacaoExperimentoEnum.INSERCAO) {
                executarInsercao(config.capacidade(), cenario.ordenacao(), cenario.estrategiaInsercao(), valores);
            } else {
                executarExclusao(config.capacidade(), cenario.ordenacao(), cenario.estrategiaInsercao(), valores);
            }
            long t2 = System.nanoTime();

            long total = t2 - t1;
            tempos[execucao] = total;
            linhasBrutas.add(new LinhaBrutaDto(execucao + 1, cenario, total, seed));
        }

        return tempos;
    }

    private static void executarInsercao(
            int capacidade,
            TipoOrdenacaoEnum ordenacao,
            EstrategiaInsercaoEnum estrategiaInsercao,
            int[] valores
    ) {
        ArranjoOrdenadoModel arranjo = new ArranjoOrdenadoModel(capacidade, ordenacao, estrategiaInsercao);
        for (int valor : valores) {
            arranjo.inserir(valor);
        }
    }

    private static void executarExclusao(
            int capacidade,
            TipoOrdenacaoEnum ordenacao,
            EstrategiaInsercaoEnum estrategiaInsercao,
            int[] valores
    ) {
        ArranjoOrdenadoModel arranjo = new ArranjoOrdenadoModel(capacidade, ordenacao, estrategiaInsercao);
        for (int valor : valores) {
            arranjo.inserir(valor);
        }

        // Exclui exatamente os valores inseridos para evitar excecoes em cenarios aleatorios.
        for (int valor : valores) {
            boolean removido = arranjo.excluir(valor);
            if (!removido) {
                throw new IllegalStateException("Falha ao excluir valor previamente inserido: " + valor);
            }
        }
    }

    private static List<ResultadoResumoDto> gerarResumos(Map<CenarioExperimentoModel, long[]> amostrasPorCenario) {
        List<ResultadoResumoDto> resumos = new ArrayList<>();
        for (Map.Entry<CenarioExperimentoModel, long[]> entrada : amostrasPorCenario.entrySet()) {
            long media = EstatisticasService.media(entrada.getValue());
            double desvioPadrao = EstatisticasService.desvioPadrao(entrada.getValue());
            resumos.add(new ResultadoResumoDto(entrada.getKey(), media, desvioPadrao, entrada.getValue().length));
        }
        return resumos;
    }

    private static Map<CenarioExperimentoModel, ResultadoResumoDto> indexarResumos(List<ResultadoResumoDto> resumos) {
        Map<CenarioExperimentoModel, ResultadoResumoDto> mapa = new HashMap<>();
        for (ResultadoResumoDto resumo : resumos) {
            mapa.put(resumo.cenario(), resumo);
        }
        return mapa;
    }

    private static List<ResultadoWilcoxonDto> gerarWilcoxon(
            Map<CenarioExperimentoModel, long[]> amostrasPorCenario,
            Config config
    ) {
        List<ResultadoWilcoxonDto> resultados = new ArrayList<>();
        if (!config.incluirComparacaoBinaria()) {
            return resultados;
        }

        for (OperacaoExperimentoEnum operacao : OperacaoExperimentoEnum.values()) {
            for (TipoOrdenacaoEnum ordenacao : TipoOrdenacaoEnum.values()) {
                for (TipoEntradaEnum entrada : TipoEntradaEnum.values()) {
                    CenarioExperimentoModel linear = new CenarioExperimentoModel(operacao, ordenacao, entrada, EstrategiaInsercaoEnum.LINEAR);
                    CenarioExperimentoModel binaria = new CenarioExperimentoModel(operacao, ordenacao, entrada, EstrategiaInsercaoEnum.BINARIA);

                    long[] amostraLinear = amostrasPorCenario.get(linear);
                    long[] amostraBinaria = amostrasPorCenario.get(binaria);
                    if (amostraLinear == null || amostraBinaria == null) {
                        continue;
                    }

                        WilcoxonResultadoDto teste = EstatisticasService.wilcoxonPareado(amostraLinear, amostraBinaria);
                        resultados.add(new ResultadoWilcoxonDto(
                            operacao,
                            ordenacao,
                            entrada,
                            teste.estatistica(),
                            teste.pValor(),
                            teste.tamanhoAmostra()
                    ));
                }
            }
        }

        return resultados;
    }

    public record Config(int capacidade, int execucoes, long seedBase, boolean incluirComparacaoBinaria) {
        static Config fromArgs(String[] args) {
            int capacidade = CAPACIDADE_PADRAO;
            int execucoes = EXECUCOES_PADRAO;
            long seed = SEED_BASE;
            boolean incluirComparacaoBinaria = true;

            for (String arg : args) {
                if (arg.startsWith("--capacidade=")) {
                    capacidade = Integer.parseInt(arg.substring("--capacidade=".length()));
                } else if (arg.startsWith("--execucoes=")) {
                    execucoes = Integer.parseInt(arg.substring("--execucoes=".length()));
                } else if (arg.startsWith("--seed=")) {
                    seed = Long.parseLong(arg.substring("--seed=".length()));
                } else if (arg.equals("--sem-comparacao-binaria")) {
                    incluirComparacaoBinaria = false;
                }
            }

            if (capacidade <= 0) {
                throw new IllegalArgumentException("Capacidade deve ser positiva.");
            }
            if (execucoes <= 0) {
                throw new IllegalArgumentException("Execucoes devem ser positivas.");
            }

            return new Config(capacidade, execucoes, seed, incluirComparacaoBinaria);
        }
    }
}
