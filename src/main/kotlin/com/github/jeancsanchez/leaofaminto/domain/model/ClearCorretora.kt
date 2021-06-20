package com.github.jeancsanchez.leaofaminto.domain.model

import javax.persistence.Entity
import javax.persistence.Inheritance
import javax.persistence.InheritanceType

/**
 * @author @jeancsanchez
 * @created 16/06/2021
 * Jesus loves you.
 */

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
class ClearCorretora : Corretora(nome = "Clear", cnpj = "02.332.886/0011-78") {
    override fun taxarOperacao(operacao: Operacao): Double? {
        return 0.0
    }
}