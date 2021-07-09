package com.github.jeancsanchez.leaofaminto.domain.model

import com.github.jeancsanchez.leaofaminto.view.round
import javax.persistence.Entity

/**
 * @author @jeancsanchez
 * @created 16/06/2021
 * Jesus loves you.
 */
@Entity
class GovernoBR : Governo("Brasil") {

    private object SwingTrade {
        const val TAXA_DEDO_DURO = 0.00005 // 0,005%
        const val TAXA_DARF = 0.15 // 15%
        const val LIMITE_INSENCAO = 20000.0
    }

    private object DayTrade {
        const val TAXA_DEDO_DURO = 0.01 // 1%
        const val TAXA_DARF = 0.20 // 20%
    }


    override fun taxarLucroVenda(venda: Venda, lucro: Double): Double {
        if (lucro > 0) {
            if (venda.tipoTrade == Venda.TipoTrade.SWING_TRADE && venda.valorTotal > SwingTrade.LIMITE_INSENCAO) {
                val dedoDuro = recolherDedoDuro(venda.valorTotal, venda.tipoTrade).round()
                val darf = ((lucro * SwingTrade.TAXA_DARF) - dedoDuro).round()

                venda.descontarTaxa(darf)
                return darf
            }

            if (venda.tipoTrade == Venda.TipoTrade.DAY_TRADE) {
                val dedoDuro = recolherDedoDuro(lucro, venda.tipoTrade).round()
                val darf = ((lucro * DayTrade.TAXA_DARF) - dedoDuro).round()

                venda.descontarTaxa(darf)
                return darf
            }
        }

        return 0.0
    }

    override fun taxarOperacao(operacao: Operacao): Double {
        if (operacao is Venda) {
            throw MethodNotAllowedException("Operações de venda devem ser informados pelo método taxarLucroVenda()")
        }
        return 0.0
    }


    override fun recolherDedoDuro(valor: Double, tipoTrade: Venda.TipoTrade): Double {
        if (tipoTrade == Venda.TipoTrade.SWING_TRADE) {
            return valor * SwingTrade.TAXA_DEDO_DURO
        }

        if (tipoTrade == Venda.TipoTrade.DAY_TRADE) {
            return valor * DayTrade.TAXA_DEDO_DURO
        }

        return 0.0
    }
}