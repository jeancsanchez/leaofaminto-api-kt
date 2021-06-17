package com.github.jeancsanchez.leaofaminto.data

import com.github.jeancsanchez.leaofaminto.domain.model.Operacao
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * @author @jeancsanchez
 * @created 15/05/2021
 * Jesus loves you.
 */

@Repository
interface OperacaoRepository : JpaRepository<Operacao, String> {

    fun findTopByOrderByIdDesc(): Operacao?

    fun findAllByAtivoCodigo(codigo: String): List<Operacao>
}