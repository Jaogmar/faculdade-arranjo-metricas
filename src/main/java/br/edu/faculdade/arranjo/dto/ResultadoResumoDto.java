package br.edu.faculdade.arranjo.dto;

import br.edu.faculdade.arranjo.model.CenarioExperimentoModel;

public record ResultadoResumoDto(
    CenarioExperimentoModel cenario,
        long media,
        double desvioPadrao,
        int execucoes
) {
    public String mediaMaisMenosDesvio() {
        return String.format("%d +/- %.4f", media, desvioPadrao);
    }
}
