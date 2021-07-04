package com.github.jeancsanchez.leaofaminto.domain.model

import javax.persistence.Entity

/**
 * @author @jeancsanchez
 * @created 16/06/2021
 * Jesus loves you.
 */
@Entity
class GovernoBR : Governo("Brasil") {
    override fun recolherDedoDuroDayTrade(valor: Double): Double {
        return 0.0
    }

    override fun recolherDedoDuroSwingTrade(valor: Double): Double {
        return 0.0
    }

    override fun taxarOperacao(operacao: Operacao): Double {
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