package com.github.jeancsanchez.leaofaminto.data

import com.github.jeancsanchez.leaofaminto.domain.model.Compra
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * @author @jeancsanchez
 * @created 15/05/2021
 * Jesus loves you.
 */

@Repository
interface ComprasRepository : JpaRepository<Compra, String> {

    fun findTopByOrderByIdDesc(): Compra?

    fun findAllByAtivoCodigo(codigo: String): List<Compra>
}