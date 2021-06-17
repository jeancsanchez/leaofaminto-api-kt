package com.github.jeancsanchez.leaofaminto.domain.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany

/**
 * @author @jeancsanchez
 * @created 10/06/2021
 * Jesus loves you.
 */

@Entity
open class Governo(
    val nomePais: String
) : ITaxador {
    @Id
    @GeneratedValue
    var id: Long? = null

    @OneToMany
    var bolsas: List<Bolsa> = emptyList()
        private set


    fun adicionarBolsa(bolsa: Bolsa) {
        if (!bolsas.contains(bolsa)) {
            bolsas = bolsas
                .toMutableList()
                .also { it.add(bolsa) }
                .toList()
        }
    }

    override fun taxarOperacao(operacao: Operacao): Double? {
        return null
    }
}