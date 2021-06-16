package com.github.jeancsanchez.investments.domain

import com.github.jeancsanchez.investments.data.OperacaoRepository
import com.github.jeancsanchez.investments.domain.model.Compra
import com.github.jeancsanchez.investments.domain.model.TipoDeAtivo
import com.github.jeancsanchez.investments.domain.model.Venda
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.YearMonth

/**
 * @author @jeancsanchez
 * @created 15/06/2021
 * Jesus loves you.
 */

@Service
class BuscarImpostosNoMesComAcoesDayTradeService(
    @Autowired private val operacaoRepository: OperacaoRepository
) : IDomainService<LocalDate, Double> {

    override fun execute(param: LocalDate): Double {
        val firstDay = YearMonth.from(param).atDay(1)
        val lastDay = YearMonth.from(param).atEndOfMonth()
        var impostoAPagar = 0.0

        val operacoesDoMes = operacaoRepository.findAll()
            .filter { it.data >= firstDay && it.data <= lastDay }
            .filter { it.ativo.tipoDeAtivo == TipoDeAtivo.ACAO }

        if (operacoesDoMes.isNotEmpty()) {
            operacoesDoMes
                .groupBy { it.data }
                .forEach { (_, lista) ->
                    val totalCompras = lista
                        .filterIsInstance(Compra::class.java)
                        .groupBy { it.ativo.codigo }
                        .mapValues { map ->
                            map.value.sumByDouble { it.valorTotal }
                        }

                    val totalVendas = lista
                        .filterIsInstance(Venda::class.java)
                        .groupBy { it.ativo.codigo }
                        .mapValues { map ->
                            map.value.sumByDouble { it.valorTotal }
                        }

                    totalCompras.entries.zip(totalVendas.entries) { compra, venda ->
                        val resultado = venda.value - compra.value
                        if (resultado > 0) {
                            impostoAPagar += (resultado * 0.20) - 0.01
                        }
                    }
                }
        }

        return impostoAPagar
    }
}