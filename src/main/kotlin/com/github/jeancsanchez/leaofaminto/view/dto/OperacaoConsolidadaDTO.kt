package com.github.jeancsanchez.leaofaminto.view.dto

import com.github.jeancsanchez.leaofaminto.domain.model.Ativo

/**
 * @author @jeancsanchez
 * @created 21/05/2021
 * Jesus loves you.
 */

data class OperacaoConsolidadaDTO(
    val ativo: Ativo,
    val quantidadeTotal: Double,
    val precoMedio: Double,
    val totalInvestido: Double
)