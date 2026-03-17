package br.edu.faculdade.arranjo.model;

import br.edu.faculdade.arranjo.enums.EstrategiaInsercaoEnum;
import br.edu.faculdade.arranjo.enums.OperacaoExperimentoEnum;
import br.edu.faculdade.arranjo.enums.TipoEntradaEnum;
import br.edu.faculdade.arranjo.enums.TipoOrdenacaoEnum;

public record CenarioExperimentoModel(
        OperacaoExperimentoEnum operacao,
        TipoOrdenacaoEnum ordenacao,
        TipoEntradaEnum entrada,
        EstrategiaInsercaoEnum estrategiaInsercao
) {
    public String id() {
        return operacao + "__" + ordenacao + "__" + entrada + "__" + estrategiaInsercao;
    }
}
