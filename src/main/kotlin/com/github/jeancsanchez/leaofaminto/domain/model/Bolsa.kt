package com.github.jeancsanchez.leaofaminto.domain.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

/**
 * @author @jeancsanchez
 * @created 10/06/2021
 * Jesus loves you.
 */

@Entity
class Bolsa(
    @Id
    @GeneratedValue
    var id: Long,

    var nome: String
) {

    fun registrarOperacao(operacao: Operacao) {

    }
}