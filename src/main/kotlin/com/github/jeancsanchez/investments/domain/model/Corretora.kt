package com.github.jeancsanchez.investments.domain.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

/**
 * @author @jeancsanchez
 * @created 15/05/2021
 * Jesus loves you.
 */

@Entity
class Corretora(
    @Id @GeneratedValue var id: Long? = null,
    var cnpj: String = "",
    var nome: String = ""
) {
    override fun toString(): String {
        return nome
    }
}