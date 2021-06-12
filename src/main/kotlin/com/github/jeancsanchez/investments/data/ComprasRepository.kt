package com.github.jeancsanchez.investments.data

import com.github.jeancsanchez.investments.domain.model.TipoOperacao
import com.github.jeancsanchez.investments.domain.novos.Compra
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