package br.edu.faculdade.arranjo.services;

import br.edu.faculdade.arranjo.dto.LinhaBrutaDto;
import br.edu.faculdade.arranjo.dto.ResultadoResumoDto;
import br.edu.faculdade.arranjo.dto.ResultadoWilcoxonDto;
import br.edu.faculdade.arranjo.enums.EstrategiaInsercaoEnum;
import br.edu.faculdade.arranjo.enums.OperacaoExperimentoEnum;
import br.edu.faculdade.arranjo.enums.TipoEntradaEnum;
import br.edu.faculdade.arranjo.enums.TipoOrdenacaoEnum;
import br.edu.faculdade.arranjo.model.CenarioExperimentoModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ExportadorResultadosService {
    private static final String CABECALHO_BRUTO = "execucao,operacao,ordenacao,entrada,estrategia,tempo_ns,seed\n";

    public void exportarBruto(Path arquivo, List<LinhaBrutaDto> linhas) throws IOException {
        Files.createDirectories(arquivo.getParent());

        StringBuilder sb = new StringBuilder(CABECALHO_BRUTO);
        for (LinhaBrutaDto linha : linhas) {
            sb.append(linha.execucao()).append(',')
              .append(linha.cenario().operacao()).append(',')
              .append(linha.cenario().ordenacao()).append(',')
              .append(linha.cenario().entrada()).append(',')
              .append(linha.cenario().estrategiaInsercao()).append(',')
              .append(linha.tempoNs()).append(',')
              .append(linha.seed())
              .append('\n');
        }

        Files.writeString(
                arquivo,
                sb.toString(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
    }

    public void exportarResumoCsv(Path arquivo, List<ResultadoResumoDto> resumos) throws IOException {
        Files.createDirectories(arquivo.getParent());

        StringBuilder sb = new StringBuilder("operacao,ordenacao,entrada,estrategia,media_ns,desvio_padrao_ns,execucoes\n");
        List<ResultadoResumoDto> ordenado = new ArrayList<>(resumos);
        ordenado.sort(comparadorResumo());

        for (ResultadoResumoDto resumo : ordenado) {
            CenarioExperimentoModel cenario = resumo.cenario();
            sb.append(cenario.operacao()).append(',')
              .append(cenario.ordenacao()).append(',')
              .append(cenario.entrada()).append(',')
              .append(cenario.estrategiaInsercao()).append(',')
              .append(resumo.media()).append(',')
              .append(String.format("%.4f", resumo.desvioPadrao())).append(',')
              .append(resumo.execucoes()).append('\n');
        }

        Files.writeString(
                arquivo,
                sb.toString(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
    }

    public void exportarWilcoxonCsv(Path arquivo, List<ResultadoWilcoxonDto> resultados) throws IOException {
        Files.createDirectories(arquivo.getParent());

        StringBuilder sb = new StringBuilder("operacao,ordenacao,entrada,estatistica,p_valor,tamanho_amostra\n");
        for (ResultadoWilcoxonDto resultado : resultados) {
            sb.append(resultado.operacao()).append(',')
              .append(resultado.ordenacao()).append(',')
              .append(resultado.entrada()).append(',')
              .append(String.format("%.4f", resultado.estatistica())).append(',')
              .append(String.format("%.8f", resultado.pValor())).append(',')
              .append(resultado.tamanhoAmostra())
              .append('\n');
        }

        Files.writeString(
                arquivo,
                sb.toString(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
    }

    public void exportarTabelaMarkdown(Path arquivo, Map<CenarioExperimentoModel, ResultadoResumoDto> mapaResumos) throws IOException {
        Files.createDirectories(arquivo.getParent());

        StringBuilder sb = new StringBuilder();
        sb.append("# Tabela de Resultados\n\n");
        construirTabelaOperacao(sb, "Insercao", OperacaoExperimentoEnum.INSERCAO, mapaResumos);
        sb.append("\n");
        construirTabelaOperacao(sb, "Exclusao", OperacaoExperimentoEnum.EXCLUSAO, mapaResumos);

        Files.writeString(
                arquivo,
                sb.toString(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
    }

    private void construirTabelaOperacao(
            StringBuilder sb,
            String titulo,
                OperacaoExperimentoEnum operacao,
                Map<CenarioExperimentoModel, ResultadoResumoDto> mapaResumos
    ) {
        sb.append("## ").append(titulo).append("\n\n");
        sb.append("| Entrada | Ordenacao crescente | Ordenacao decrescente |\n");
        sb.append("|---|---|---|\n");

        for (TipoEntradaEnum entrada : TipoEntradaEnum.values()) {
            ResultadoResumoDto crescente = mapaResumos.get(new CenarioExperimentoModel(
                    operacao,
                TipoOrdenacaoEnum.CRESCENTE,
                    entrada,
                EstrategiaInsercaoEnum.LINEAR
            ));
            ResultadoResumoDto decrescente = mapaResumos.get(new CenarioExperimentoModel(
                    operacao,
                TipoOrdenacaoEnum.DECRESCENTE,
                    entrada,
                EstrategiaInsercaoEnum.LINEAR
            ));

            String valorCrescente = crescente != null ? crescente.mediaMaisMenosDesvio() : "-";
            String valorDecrescente = decrescente != null ? decrescente.mediaMaisMenosDesvio() : "-";

            sb.append("| ").append(descreverEntrada(entrada)).append(" | ")
              .append(valorCrescente).append(" | ")
              .append(valorDecrescente).append(" |\n");
        }
    }

    private String descreverEntrada(TipoEntradaEnum entrada) {
        return switch (entrada) {
            case CRESCENTE -> "Insercao em maneira crescente";
            case DECRESCENTE -> "Insercao em maneira decrescente";
            case ALEATORIA -> "Insercao em maneira aleatoria";
        };
    }

    private Comparator<ResultadoResumoDto> comparadorResumo() {
        return Comparator
                .comparing((ResultadoResumoDto r) -> r.cenario().operacao().name())
                .thenComparing(r -> r.cenario().ordenacao().name())
                .thenComparing(r -> r.cenario().entrada().name())
                .thenComparing(r -> r.cenario().estrategiaInsercao().name());
    }
}
