package com.github.jeancsanchez.leaofaminto.domain

import com.github.jeancsanchez.leaofaminto.data.OperacaoRepository
import com.github.jeancsanchez.leaofaminto.domain.model.Compra
import com.github.jeancsanchez.leaofaminto.domain.model.TipoDeAtivo
import com.github.jeancsanchez.leaofaminto.domain.model.Venda
import com.github.jeancsanchez.leaofaminto.view.round
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
        var lucroNoMes = 0.0
        var impostosAPagarNoMes = 0.0
        var taxasOperacionaisNoMes = 0.0

        val operacoesDoMes = operacaoRepository.findAll()
            .filter { it.data >= primeiroDiaDoMes && it.data <= ultimoDiaDoMes }
            .filter { it.ativo.tipoDeAtivo == TipoDeAtivo.ACAO }

        if (operacoesDoMes.isNotEmpty()) {
            operacoesDoMes
                .groupBy { it.data }
                .forEach { (_, lista) ->
                    val totalCompras = lista
                        .filterIsInstance(Compra::class.java)

                    val totalVendas = lista
                        .filterIsInstance(Venda::class.java)

                    totalCompras.zip(totalVendas) { compra, venda ->
                        if (compra.corretora == venda.corretora) {
                            val resultado = venda.valorTotal - compra.valorTotal

                            taxasOperacionaisNoMes += compra.corretora.taxarOperacao(compra)
                            taxasOperacionaisNoMes += compra.corretora.bolsa.taxarOperacao(compra)
                            taxasOperacionaisNoMes += venda.corretora.taxarOperacao(venda)
                            taxasOperacionaisNoMes += venda.corretora.bolsa.taxarOperacao(venda)

                            if (resultado > 0) {
                                val taxaCorretora = venda.corretora.taxarLucroVenda(venda, resultado)
                                val taxaBolsa = venda.corretora.bolsa.taxarLucroVenda(venda, resultado)
                                val totalCustas = taxaCorretora + taxaBolsa
                                val lucroLiquido = resultado - totalCustas

                                lucroNoMes += lucroLiquido
                                taxasOperacionaisNoMes += totalCustas
                                impostosAPagarNoMes =
                                    venda.corretora.bolsa.governo.taxarLucroVenda(venda, lucroLiquido)
                            }
                        }
                    }
                }
        }

        return impostosAPagarNoMes.round()
    }
}