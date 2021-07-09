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
     * Taxa lucro de operações
     * @param venda [Venda] a ser taxada
     * @param lucro valor do lucro
     */
    fun taxarLucroVenda(venda: Venda, lucro: Double): Double
}