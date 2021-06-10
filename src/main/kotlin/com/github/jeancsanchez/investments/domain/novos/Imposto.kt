package com.github.jeancsanchez.investments.domain.novos

import java.time.LocalDate
import javax.persistence.Entity

/**
 * @author @jeancsanchez
 * @created 10/06/2021
 * Jesus loves you.
 */

@Entity
class Imposto(
    var dataReferencia: LocalDate,
    var valor: Double
)