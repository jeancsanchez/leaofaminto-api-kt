package com.github.jeancsanchez.leaofaminto.domain.model.governos

import com.github.jeancsanchez.leaofaminto.domain.model.Venda
import com.github.jeancsanchez.leaofaminto.domain.model.base.ITaxador
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Inheritance
import javax.persistence.InheritanceType

/**
 * @author @jeancsanchez
 * @created 10/06/2021
 * Jesus loves you.
 */

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class Governo(
    @Id open val nomePais: String
) : ITaxador {

    /**
     * Recolhe o valor do dedo duro em operação day trade baseado no valor informado.
     * @param valor valor de dedo duro a ser recolhido
     * @param tipoTrade [Venda.TipoTrade] tipo de trade.
     */
    abstract fun recolherDedoDuro(valor: Double, tipoTrade: Venda.TipoTrade): Double

    override fun toString(): String {
        return nomePais
    }
}