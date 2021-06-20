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
abstract class Corretora(
    @Id @GeneratedValue var id: Long? = null,
    var cnpj: String = "",
    var nome: String = ""
) : ITaxador {
    override fun toString(): String {
        return nome
    }
}