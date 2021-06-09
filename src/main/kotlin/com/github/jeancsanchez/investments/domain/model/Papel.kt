package com.github.jeancsanchez.investments.domain.model

import javax.persistence.Entity
import javax.persistence.Id

/**
 * @author @jeancsanchez
 * @created 15/05/2021
 * Jesus loves you.
 */

@Entity
data class Papel(
    @Id var codigo: String = "",
    var nome: String = "",
    var cnpj: String = ""
) {
    override fun toString(): String {
        return codigo
    }
}