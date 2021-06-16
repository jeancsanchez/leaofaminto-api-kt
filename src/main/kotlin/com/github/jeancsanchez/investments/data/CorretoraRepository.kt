package com.github.jeancsanchez.investments.data

import com.github.jeancsanchez.investments.domain.model.Corretora
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * @author @jeancsanchez
 * @created 15/05/2021
 * Jesus loves you.
 */

@Repository
interface CorretoraRepository : JpaRepository<Corretora, Long> {

    fun findTop1ByNomeIgnoreCase(nome: String): Corretora?
}