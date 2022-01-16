package com.github.jeancsanchez.leaofaminto.data

import com.github.jeancsanchez.leaofaminto.domain.model.governos.Governo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * @author @jeancsanchez
 * @created 16/06/2021
 * Jesus loves you.
 */

@Repository
interface GovernoRepository : JpaRepository<Governo, Long> {

    /**
     * Pega o primeiro país pelo nome.
     * @param nome nome do país.
     */
    fun findTop1ByNomePaisIgnoreCase(nome: String): Governo?
}