package com.github.jeancsanchez.leaofaminto.data

import com.github.jeancsanchez.leaofaminto.domain.model.Venda
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * @author @jeancsanchez
 * @created 15/05/2021
 * Jesus loves you.
 */

@Repository
interface VendasRepository : JpaRepository<Venda, String> {

    fun findTopByOrderByIdDesc(): Venda?

    fun findAllByAtivoCodigo(codigo: String): List<Venda>
}