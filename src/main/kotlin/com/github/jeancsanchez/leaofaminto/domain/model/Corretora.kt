package com.github.jeancsanchez.leaofaminto.domain.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne

/**
 * @author @jeancsanchez
 * @created 10/06/2021
 * Jesus loves you.
 */

@Entity
abstract class Corretora(
    @Id @GeneratedValue open var id: Long? = null,
    open var cnpj: String = "",
    open var nome: String = "",
    @ManyToOne open var bolsa: Bolsa
) : ITaxador {

    override fun toString(): String {
        return nome
    }
}