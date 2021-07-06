package com.github.jeancsanchez.leaofaminto.data

import com.github.jeancsanchez.leaofaminto.domain.model.Corretora
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * @author @jeancsanchez
 * @created 15/05/2021
 * Jesus loves you.
 */

@Repository
interface CorretoraRepository : JpaRepository<Corretora, Long> {

    /**
     * Pega primeira corretora pelo nome
     * @param nome nome da corretora
     */
    fun findTop1ByNomeIgnoreCase(nome: String): Corretora?
}