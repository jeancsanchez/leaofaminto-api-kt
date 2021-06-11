package com.github.jeancsanchez.investments.data

import com.github.jeancsanchez.investments.domain.model.TOperacao
import com.github.jeancsanchez.investments.domain.model.TipoOperacao
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * @author @jeancsanchez
 * @created 15/05/2021
 * Jesus loves you.
 */

@Repository
interface OperacaoRepository : JpaRepository<TOperacao, String> {

    fun findTopByOrderByIdDesc(): TOperacao?

    fun findAllByPapelCodigo(codigo: String): List<TOperacao>

    fun findAllByPapelCodigoAndTipoDaOperacao(codigo: String, tipoOperacao: TipoOperacao): List<TOperacao>
}