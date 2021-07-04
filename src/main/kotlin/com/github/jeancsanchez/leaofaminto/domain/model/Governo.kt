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

    abstract fun recolherDedoDuroDayTrade(valor: Double): Double

    abstract fun recolherDedoDuroSwingTrade(valor: Double): Double
}