package com.github.jeancsanchez.investments.domain.novos

import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

/**
 * @author @jeancsanchez
 * @created 10/06/2021
 * Jesus loves you.
 */

@Entity
class Imposto(
    @Id
    @GeneratedValue
    var id: Long,
    
    var dataReferencia: LocalDate,
    var valor: Double
)