package com.github.jeancsanchez.leaofaminto.data

import com.github.jeancsanchez.leaofaminto.domain.model.Ativo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * @author @jeancsanchez
 * @created 15/05/2021
 * Jesus loves you.
 */

@Repository
interface AtivoRepository : JpaRepository<Ativo, String> {

    /**
     * Pega o primeiro ativo pelo nome.
     * @param nome nome do ativo.
     */
    fun findTop1ByNomeIgnoreCase(nome: String): Ativo?

    /**
     * Pega o primeiro ativo pelo codigo.
     * @param codigo nome do ativo.
     */
    fun findTop1ByCodigoIgnoreCase(codigo: String): Ativo?
}