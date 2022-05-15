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

    /**
     * Pega a primeira operação de venda ordenado pelo Id.
     */
    fun findTopByOrderByIdDesc(): Venda?

    /**
     * Pega todas as operações de venda pelo código do ativo
     * @param codigo código do ativo
     */
    fun findAllByAtivoCodigo(codigo: String): List<Venda>
}