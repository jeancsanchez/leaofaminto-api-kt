package com.github.jeancsanchez.leaofaminto.domain.model

/**
 * @author @jeancsanchez
 * @created 16/06/2021
 * Jesus loves you.
 */

interface ITaxador {

    fun taxarOperacao(operacao: Operacao): Double

    fun taxarLucroSwingTrade(lucro: Double): Double

    fun taxarLucroDayTrade(lucro: Double): Double
}