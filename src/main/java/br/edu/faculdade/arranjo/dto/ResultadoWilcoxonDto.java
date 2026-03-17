package br.edu.faculdade.arranjo.dto;

import br.edu.faculdade.arranjo.enums.OperacaoExperimentoEnum;
import br.edu.faculdade.arranjo.enums.TipoEntradaEnum;
import br.edu.faculdade.arranjo.enums.TipoOrdenacaoEnum;

public record ResultadoWilcoxonDto(
        OperacaoExperimentoEnum operacao,
        TipoOrdenacaoEnum ordenacao,
        TipoEntradaEnum entrada,
        double estatistica,
        double pValor,
        int tamanhoAmostra
) {
}
