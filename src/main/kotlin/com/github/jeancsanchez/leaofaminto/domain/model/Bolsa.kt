package com.github.jeancsanchez.leaofaminto.domain.model

import javax.persistence.*

/**
 * @author @jeancsanchez
 * @created 10/06/2021
 * Jesus loves you.
 */

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class Bolsa(
    @Id var nome: String
) : ITaxador {
    @OneToMany(targetEntity = Corretora::class)
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