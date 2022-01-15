package com.github.jeancsanchez.leaofaminto.view.dto

/**
 * @author @jeancsanchez
 * @created 21/05/2021
 * Jesus loves you.
 */

data class ConsolidadoDTO(
    val items: List<OperacaoConsolidadaDTO>,
    val totalInvestido: Double
)