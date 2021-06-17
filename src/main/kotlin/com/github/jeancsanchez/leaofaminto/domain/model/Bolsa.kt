package com.github.jeancsanchez.leaofaminto.domain.model

import javax.persistence.Entity
import javax.persistence.OneToMany

/**
 * @author @jeancsanchez
 * @created 10/06/2021
 * Jesus loves you.
 */

@Entity
open class Bolsa(
    var nome: String
) {
    @OneToMany
    var corretoras: List<Corretora> = emptyList()
        private set

    fun adicionarCorretora(corretora: Corretora) {
        if (!corretoras.contains(corretora)) {
            corretoras = corretoras
                .toMutableList()
                .also { it.add(corretora) }
                .toList()
        }
    }
}