package com.github.jeancsanchez.leaofaminto.domain.model

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Inheritance
import javax.persistence.InheritanceType

/**
 * @author @jeancsanchez
 * @created 10/06/2021
 * Jesus loves you.
 */

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class Governo(
    @Id val nomePais: String
) : ITaxador {

    override fun toString(): String {
        return nomePais
    }

    /**
     * Recolhe o valor do dedo duro em operação day trade baseado no valor informado.
     * @param valor valor a ser taxado.
     */
    abstract fun recolherDedoDuroDayTrade(valor: Double): Double

    /**
     * Recolhe o valor do dedo duro em operação swing trade baseado no valor informado.
     * @param valor valor a ser taxado.
     */
    abstract fun recolherDedoDuroSwingTrade(valor: Double): Double
}