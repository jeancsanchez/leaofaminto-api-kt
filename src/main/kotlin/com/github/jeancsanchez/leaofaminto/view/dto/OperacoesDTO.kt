package com.github.jeancsanchez.leaofaminto.view.dto

import com.github.jeancsanchez.leaofaminto.domain.model.Compra
import com.github.jeancsanchez.leaofaminto.domain.model.Venda

/**
 * @author @jeancsanchez
 * @created 15/01/2022
 * Jesus loves you.
 */

data class OperacoesDTO(
    val compras: List<Compra>,
    val vendas: List<Venda>,
)