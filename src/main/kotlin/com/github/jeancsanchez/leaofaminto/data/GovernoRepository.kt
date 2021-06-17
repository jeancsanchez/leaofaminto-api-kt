package com.github.jeancsanchez.leaofaminto.data

import com.github.jeancsanchez.leaofaminto.domain.model.Governo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * @author @jeancsanchez
 * @created 16/06/2021
 * Jesus loves you.
 */

@Repository
interface GovernoRepository : JpaRepository<Governo, Long> {

    fun findTop1ByNomeIgnoreCase(nome: String): Governo?
}