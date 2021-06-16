package com.github.jeancsanchez.investments.data

import com.github.jeancsanchez.investments.domain.model.Imposto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

/**
 * @author @jeancsanchez
 * @created 15/06/2021
 * Jesus loves you.
 */

@Repository
interface ImpostoRepository : JpaRepository<Imposto, Long> {

    fun findTopByOrderByDataReferenciaAndValor(
        dataReferencia: LocalDate,
        valor: Double
    ): Imposto?
}