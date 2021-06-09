package com.github.jeancsanchez.investments.data

import com.github.jeancsanchez.investments.domain.model.Operacao
import com.github.jeancsanchez.investments.domain.model.TipoOperacao
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

    fun findAllByPapelCodigo(codigo: String): List<Operacao>

    fun findAllByPapelCodigoAndTipoDaOperacao(codigo: String, tipoOperacao: TipoOperacao): List<Operacao>
}