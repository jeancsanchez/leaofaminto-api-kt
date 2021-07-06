package com.github.jeancsanchez.leaofaminto.domain.model

/**
 * @author @jeancsanchez
 * @created 16/06/2021
 * Jesus loves you.
 */

interface ITaxador {

    /**
     * Taxa a operação
     * @param operacao operação a ser taxada
     */
    fun taxarOperacao(operacao: Operacao): Double

    /**
     * Taxa lucro de operações swing trade
     * @param lucro valor do lucro
     */
    fun taxarLucroSwingTrade(lucro: Double): Double

    /**
     * Taxa lucro de operações day trade
     * @param lucro valor do lucro
     */
    fun taxarLucroDayTrade(lucro: Double): Double

    /**
     * taxa lucro de operações com Fundos Imobiliários
     * @param lucro valor do lucro.
     */
    fun taxarLucroFII(lucro: Double): Double
}