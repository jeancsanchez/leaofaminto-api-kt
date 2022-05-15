package com.github.jeancsanchez.leaofaminto.domain.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToOne

/**
 * @author @jeancsanchez
 * @created 16/06/2021
 * Jesus loves you.
 */

@Entity
class Taxa(
    @Id
    @GeneratedValue
    var id: Long? = null,
    @OneToOne var operacao: Operacao,
    var valor: Double
)