package com.github.jeancsanchez.leaofaminto.domain.model

import javax.persistence.Entity

/**
 * @author @jeancsanchez
 * @created 16/06/2021
 * Jesus loves you.
 */
@Entity
class GovernoBR(
    nomePais: String
) : Governo(nomePais) {
    override fun taxarOperacao(operacao: Operacao): Double? {
        return 0.0
    }
}