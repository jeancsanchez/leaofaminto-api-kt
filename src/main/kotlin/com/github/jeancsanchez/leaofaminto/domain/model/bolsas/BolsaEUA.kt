package com.github.jeancsanchez.leaofaminto.domain.model.bolsas

import com.github.jeancsanchez.leaofaminto.domain.model.Operacao
import com.github.jeancsanchez.leaofaminto.domain.model.Venda
import com.github.jeancsanchez.leaofaminto.domain.model.base.ITaxador
import com.github.jeancsanchez.leaofaminto.domain.model.governos.GovernoEUA
import javax.persistence.Entity

/**
 * @author @jeancsanchez
 * @created 16/06/2021
 * Jesus loves you.
 */

@Entity
class BolsaEUA : Bolsa("EUA", GovernoEUA()), ITaxador {

    override fun taxarOperacao(operacao: Operacao): Double {
        return 0.0
    }

    override fun taxarLucroVenda(venda: Venda, lucro: Double): Double {
        return 0.0
    }
}