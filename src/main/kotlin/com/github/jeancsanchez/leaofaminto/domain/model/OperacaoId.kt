package com.github.jeancsanchez.leaofaminto.domain.model

import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import java.io.Serializable

/**
 * @author @jeancsanchez
 * @created 16/01/2022
 * Jesus loves you.
 */

class OperacaoId : IdentifierGenerator {

    override fun generate(session: SharedSessionContractImplementor?, obj: Any?): Serializable {
        val operacao = obj as Operacao
        return operacao.hashId
    }
}