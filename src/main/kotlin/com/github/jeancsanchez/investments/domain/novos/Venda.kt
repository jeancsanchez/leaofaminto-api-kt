package com.github.jeancsanchez.investments.domain.novos

import com.github.jeancsanchez.investments.domain.model.Corretora
import java.lang.IllegalStateException
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
    quantidade: Int
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

    enum class TipoTrade {
        DAY_TRADE, SWING_TRADE
    }
}