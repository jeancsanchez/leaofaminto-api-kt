package com.github.jeancsanchez.leaofaminto.data

import com.github.jeancsanchez.leaofaminto.domain.model.Operacao
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/**
 * @author @jeancsanchez
 * @created 15/05/2021
 * Jesus loves you.
 */

@Repository
interface OperacaoRepository : JpaRepository<Operacao, String> {

    /**
     * Pega a primeira operação ordenado pelo Id
     */
    fun findTopByOrderByIdDesc(): Operacao?

    /**
     * Pega todas as operações pelo código do ativo
     * @param codigo codigo do ativo na operação
     */
    fun findAllByAtivoCodigo(codigo: String): List<Operacao>

    /**
     * Deleta todas as operações pelo nome da corretora
     * @param nome da corretora
     */
    @Transactional
    fun deleteAllInBatchByCorretoraNomeIgnoreCase(nome: String)
}