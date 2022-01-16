package com.github.jeancsanchez.leaofaminto.view.dto

/**
 * @author @jeancsanchez
 * @created 21/05/2021
 * Jesus loves you.
 */

data class OperacaoConsolidadaDTO(
    val codigoAtivo: String,
    val quantidadeTotal: Double,
    val precoMedio: Double,
    val totalInvestido: Double
)