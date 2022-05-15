package com.github.jeancsanchez.leaofaminto.domain.model.governos

import com.github.jeancsanchez.leaofaminto.domain.model.Operacao
import com.github.jeancsanchez.leaofaminto.domain.model.Venda
import javax.persistence.Entity

/**
 * @author @jeancsanchez
 * @created 16/06/2021
 * Jesus loves you.
 */
@Entity
class GovernoEUA : Governo("Estados Unidos") {

    override fun taxarLucroVenda(venda: Venda, lucro: Double): Double {
        return 0.0
    }

    override fun taxarOperacao(operacao: Operacao): Double {
        return 0.0
    }

    override fun recolherDedoDuro(valor: Double, tipoTrade: Venda.TipoTrade): Double {
        return 0.0
    }
}