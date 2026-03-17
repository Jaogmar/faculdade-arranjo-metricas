package br.edu.faculdade.arranjo.dto;

import br.edu.faculdade.arranjo.model.CenarioExperimentoModel;

public record LinhaBrutaDto(
        int execucao,
        CenarioExperimentoModel cenario,
        long tempoNs,
        long seed
) {
}
