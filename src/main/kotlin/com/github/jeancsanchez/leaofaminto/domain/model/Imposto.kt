package com.github.jeancsanchez.leaofaminto.domain.model

import java.time.LocalDate
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
class Imposto(
    @Id
    @GeneratedValue
    var id: Long? = null,

    var dataReferencia: LocalDate,

    @OneToMany(targetEntity = Operacao::class)
    var operacoes: List<Operacao> = emptyList(),

    var valor: Double,
    var estaPago: Boolean? = false
)