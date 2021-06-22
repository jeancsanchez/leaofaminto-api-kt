package com.github.jeancsanchez.leaofaminto.domain

import com.github.jeancsanchez.leaofaminto.data.OperacaoRepository
import com.github.jeancsanchez.leaofaminto.domain.model.Compra
import com.github.jeancsanchez.leaofaminto.domain.model.TipoDeAtivo
import com.github.jeancsanchez.leaofaminto.domain.model.Venda
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
        val primeiroDiaDoMes = YearMonth.from(param).atDay(1)
        val ultimoDiaDoMes = YearMonth.from(param).atEndOfMonth()
        var impostoAPagar = 0.0

        val operacoesDoMes = operacaoRepository.findAll()
            .filter { it.data >= primeiroDiaDoMes && it.data <= ultimoDiaDoMes }
            .filter { it.ativo.tipoDeAtivo == TipoDeAtivo.ACAO }

        if (operacoesDoMes.isNotEmpty()) {
            val corretora = operacoesDoMes.first().corretora
            val bolsa = operacoesDoMes.first().corretora.bolsa
            val governo = operacoesDoMes.first().corretora.bolsa.governo

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
                            val taxasOperacionais =
                                corretora.taxarLucroDayTrade(resultado) + bolsa.taxarLucroDayTrade(resultado)
                            val lucroLiquido = resultado - taxasOperacionais
                            val dedoDuro = governo.recolherDedoDuroDayTrade(resultado)
                            val impostoDevido = governo.taxarLucroDayTrade(lucroLiquido)
                            impostoAPagar += impostoDevido - dedoDuro
                        }
                    }
                }
        }

        return impostoAPagar
    }
}