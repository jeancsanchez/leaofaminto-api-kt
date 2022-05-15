package com.github.jeancsanchez.leaofaminto.domain.model

import com.github.jeancsanchez.leaofaminto.domain.model.corretoras.Corretora
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

/**
 * @author @jeancsanchez
 * @created 10/06/2021
 * Jesus loves you.
 */

@Entity
class Venda(
    ativo: Ativo,
    corretora: Corretora,
    data: LocalDate = LocalDate.now(),
    preco: Double,
    quantidade: Double
) : Operacao(ativo, corretora, data, preco, quantidade) {

    @Enumerated(EnumType.STRING)
    var tipoTrade: TipoTrade = TipoTrade.SWING_TRADE
        private set

    fun gerarTipoTrade(compra: Compra) {
        if (compra.ativo !== ativo) {
            throw IllegalStateException("Ativo de compra e venda diferentes.")
        }

        if (data.isEqual(compra.data)) {
            tipoTrade = TipoTrade.DAY_TRADE
            return
        }

        tipoTrade = TipoTrade.SWING_TRADE
    }

    /**
     * Desconta a taxa informada do valor total da operação
     * @param valor valor a ser taxado.
     */
    fun descontarTaxa(valor: Double) {
        valorTotal -= valor
    }

    enum class TipoTrade {
        DAY_TRADE, SWING_TRADE
    }
}