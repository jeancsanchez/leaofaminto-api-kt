package com.github.jeancsanchez.investments.data

import com.github.jeancsanchez.investments.domain.model.Papel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * @author @jeancsanchez
 * @created 15/05/2021
 * Jesus loves you.
 */

@Repository
interface PapelRepository : JpaRepository<Papel, String> {

    fun findTop1ByNomeIgnoreCase(nome: String): Papel?


}