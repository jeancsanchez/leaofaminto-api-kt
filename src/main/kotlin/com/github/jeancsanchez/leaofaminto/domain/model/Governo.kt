package com.github.jeancsanchez.leaofaminto.domain.model

import javax.persistence.*

/**
 * @author @jeancsanchez
 * @created 10/06/2021
 * Jesus loves you.
 */

@Entity
class Governo(
    @Id
    @GeneratedValue
    var id: Long,

    @OneToMany
    var bolsas: List<Bolsa>,

    @OneToMany
    var corretoras: List<Corretora>,

    @Enumerated(EnumType.STRING)
    var paisDeOrigem: Pais = Pais.BR
) {

    fun taxarOperacao(operacao: Operacao): Imposto? {
        return null
    }

    enum class Pais {
        BR, EUA
    }
}