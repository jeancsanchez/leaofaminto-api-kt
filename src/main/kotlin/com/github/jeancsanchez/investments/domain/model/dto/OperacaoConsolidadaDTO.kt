package com.github.jeancsanchez.investments.domain.model.dto

/**
 * @author @jeancsanchez
 * @created 21/05/2021
 * Jesus loves you.
 */

data class OperacaoConsolidadaDTO(
    val codigoAtivo: String,
    val quantidadeTotal: Int,
    val precoMedio: Double,
    val totalInvestido: Double
)