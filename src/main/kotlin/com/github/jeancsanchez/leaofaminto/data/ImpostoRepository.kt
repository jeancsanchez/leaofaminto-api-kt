package com.github.jeancsanchez.leaofaminto.data

import com.github.jeancsanchez.leaofaminto.domain.model.Imposto
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

    /**
     * Pega o primeiro imposto pela data de referencia e valor
     * @param dataReferencia Data de referência
     * @param valor valor do imposto
     */
    fun findTop1ByDataReferenciaAndValor(
        dataReferencia: LocalDate,
        valor: Double
    ): Imposto?

    /**
     * Busca todos os impostos.
     * @param estaPago se o imposto foi pago ou não
     */
    fun findAllByEstaPago(estaPago: Boolean): List<Imposto>
}