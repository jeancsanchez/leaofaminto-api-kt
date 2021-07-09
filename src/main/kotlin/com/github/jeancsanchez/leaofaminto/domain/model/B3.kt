package com.github.jeancsanchez.leaofaminto.domain.model

import com.github.jeancsanchez.leaofaminto.view.round
import javax.persistence.Entity

/**
 * @author @jeancsanchez
 * @created 16/06/2021
 * Jesus loves you.
 */

@Entity
class B3 : Bolsa("B3", GovernoBR()), ITaxador {

    private object SwingTrade {
        const val TAXA_NEGOCIACAO = (0.0050 / 100) // 0,0050%
        const val TAXA_LIQUIDACAO = (0.0250 / 100) // 0,0250%
        const val TAXA_EMOLUMENTOS = TAXA_NEGOCIACAO + TAXA_LIQUIDACAO
    }

    private object DayTrade {
        const val TAXA_NEGOCIACAO = (0.0050 / 100) // 0,0050%
        const val TAXA_LIQUIDACAO = (0.0180 / 100) // 0,0180%
        const val TAXA_EMOLUMENTOS_PRIMEIRA_FAIXA = TAXA_NEGOCIACAO + TAXA_LIQUIDACAO
        const val TAXA_EMOLUMENTOS_SEGUNDA_FAIXA = (0.0225 / 100) // 0,0225%
    }

    override fun taxarOperacao(operacao: Operacao): Double {
        if (operacao is Compra) {
            val taxa = (operacao.valorTotal * SwingTrade.TAXA_EMOLUMENTOS).round()
            operacao.acrescentarTaxa(taxa)
            return taxa
        }

        if (operacao is Venda) {
            val taxa = when (operacao.tipoTrade) {
                Venda.TipoTrade.SWING_TRADE -> (operacao.valorTotal * SwingTrade.TAXA_EMOLUMENTOS).round()
                else -> {
                    if (operacao.valorTotal <= 1000000) {
                        (operacao.valorTotal * DayTrade.TAXA_EMOLUMENTOS_PRIMEIRA_FAIXA).round()
                    } else {
                        (operacao.valorTotal * DayTrade.TAXA_EMOLUMENTOS_SEGUNDA_FAIXA).round()
                    }
                }
            }

            operacao.descontarTaxa(taxa)
            return taxa
        }

        return 0.0
    }

    override fun taxarLucroSwingTrade(lucro: Double): Double {
        return 0.0
    }

    override fun taxarLucroDayTrade(lucro: Double): Double {
        return 0.0
    }

    override fun taxarLucroFII(lucro: Double): Double {
        return 0.0
    }
}