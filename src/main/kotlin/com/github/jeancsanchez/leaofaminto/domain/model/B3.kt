package com.github.jeancsanchez.leaofaminto.domain.model

import javax.persistence.Entity

/**
 * @author @jeancsanchez
 * @created 16/06/2021
 * Jesus loves you.
 */

@Entity
class B3(
    nome: String
) : Bolsa(nome), ITaxador {
    override fun taxarOperacao(operacao: Operacao): Double? {
        if (operacao is Venda) {
            val taxa = operacao.valorTotal * 0.000250
            operacao.aplicarTaxa(taxa)
            return taxa
        }

        return null
    }
}