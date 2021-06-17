package com.github.jeancsanchez.leaofaminto.domain.model

/**
 * @author @jeancsanchez
 * @created 16/06/2021
 * Jesus loves you.
 */

class B3 : Bolsa(nome = "B3"), ITaxador {
    override fun taxarOperacao(operacao: Operacao): Double? {
        if (operacao is Venda) {
            val taxa = operacao.valorTotal * 0.000250
            operacao.aplicarTaxa(taxa)
            return taxa
        }

        return null
    }
}