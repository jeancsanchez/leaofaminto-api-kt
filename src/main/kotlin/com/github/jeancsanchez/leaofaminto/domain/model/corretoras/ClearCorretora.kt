package com.github.jeancsanchez.leaofaminto.domain.model.corretoras

import com.github.jeancsanchez.leaofaminto.domain.model.Operacao
import com.github.jeancsanchez.leaofaminto.domain.model.Venda
import com.github.jeancsanchez.leaofaminto.domain.model.bolsas.B3
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
class ClearCorretora : Corretora(nome = "Clear", cnpj = "02.332.886/0011-78", bolsa = B3()) {
    override fun taxarOperacao(operacao: Operacao): Double {
        return 0.0
    }

    override fun taxarLucroVenda(venda: Venda, lucro: Double): Double {
        return 0.0
    }
}