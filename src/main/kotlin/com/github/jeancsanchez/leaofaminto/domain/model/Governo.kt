package com.github.jeancsanchez.leaofaminto.domain.model

import javax.persistence.*

/**
 * @author @jeancsanchez
 * @created 10/06/2021
 * Jesus loves you.
 */

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class Governo(
    val nomePais: String
) : ITaxador {
    @Id
    @GeneratedValue
    var id: Long? = null

    @OneToMany(targetEntity = Bolsa::class)
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

    override fun toString(): String {
        return nomePais
    }
}