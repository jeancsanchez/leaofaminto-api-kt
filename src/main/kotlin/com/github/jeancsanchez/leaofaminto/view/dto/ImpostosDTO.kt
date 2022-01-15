package com.github.jeancsanchez.leaofaminto.view.dto

import com.github.jeancsanchez.leaofaminto.domain.model.Imposto

/**
 * @author @jeancsanchez
 * @created 16/06/2021
 * Jesus loves you.
 */

data class ImpostosDTO(
    val impostos: List<Imposto>? = emptyList(),
    val total: Double? = 0.0
)