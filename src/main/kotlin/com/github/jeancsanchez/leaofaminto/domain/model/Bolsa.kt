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
    @Id var nome: String,
    @ManyToOne var governo: Governo
) : ITaxador