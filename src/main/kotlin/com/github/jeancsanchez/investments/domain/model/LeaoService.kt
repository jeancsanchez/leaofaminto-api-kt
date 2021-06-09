package com.github.jeancsanchez.investments.domain.model

import com.github.jeancsanchez.investments.data.OperacaoRepository
import com.github.jeancsanchez.investments.view.round
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.YearMonth

/**
 * Essa classe é um service que contém todas as lógicas relacionadas a impostos.
 * Todas as informações foram retiradas do blog do Bastter
 * no seguinte link: https://bastter.com/mercado/forum/794001
 *
 * @author @jeancsanchez
 * @created 31/05/2021
 * Jesus loves you.
 */

@Service
class LeaoService(
    @Autowired private val operacaoRepository: OperacaoRepository
) {
    fun pegarLucroLiquidoNoMesComFIIs(mes: LocalDate): Double {
        val firstDay = YearMonth.from(mes).atDay(1)
        val lastDay = YearMonth.from(mes).atEndOfMonth()

        val vendasNoMes = operacaoRepository.findAll()
            .filter { it.data >= firstDay && it.data <= lastDay }
            .filter { it.tipoDaAcao == TipoAcao.FUNDO_IMOBILIARIO }
            .filter { it.tipoDaOperacao == TipoOperacao.VENDA }

        if (vendasNoMes.isNotEmpty()) {
            val compras = vendasNoMes
                .flatMap {
                    operacaoRepository.findAllByPapelCodigoAndTipoDaOperacao(
                        it.papel.codigo,
                        TipoOperacao.COMPRA
                    )
                }

            return vendasNoMes.sumByDouble { it.valorTotal } - compras.sumByDouble { it.valorTotal }
        }

        return 0.0
    }

    fun pegarImpostosNoMesComFIIs(mes: LocalDate): Double {
        val lucroDoMes = pegarLucroLiquidoNoMesComFIIs(mes)
        if (lucroDoMes > 0) {
            return (lucroDoMes * 0.20)
        }
        return lucroDoMes
    }

    fun pegarImpostosNoMesComAcoesDayTrade(mes: LocalDate): Double {
        val firstDay = YearMonth.from(mes).atDay(1)
        val lastDay = YearMonth.from(mes).atEndOfMonth()
        var impostoAPagar = 0.0

        val operacoesDoMes = operacaoRepository.findAll()
            .filter { it.data >= firstDay && it.data <= lastDay }
            .filter { it.tipoDaAcao !== TipoAcao.FUNDO_IMOBILIARIO }

        if (operacoesDoMes.isNotEmpty()) {
            operacoesDoMes
                .groupBy { it.data }
                .forEach { (_, operacoes) ->
                    val compras = operacoes
                        .filter { it.tipoDaOperacao == TipoOperacao.COMPRA }
                        .groupBy { it.papel.codigo }
                        .mapValues { map ->
                            map.value.sumByDouble { it.valorTotal }
                        }

                    val vendas = operacoes
                        .filter { it.tipoDaOperacao == TipoOperacao.VENDA }
                        .groupBy { it.papel.codigo }
                        .mapValues { map ->
                            map.value.sumByDouble { it.valorTotal }
                        }

                    compras.entries.zip(vendas.entries) { compra, venda ->
                        val resultado = venda.value - compra.value
                        if (resultado > 0) {
                            impostoAPagar += (resultado * 0.20) - 0.01
                        }
                    }
                }
        }

        return impostoAPagar
    }

    fun pegarImpostosNoMesComAcoesSwingTrade(mes: LocalDate): Double {
        val firstDay = YearMonth.from(mes).atDay(1)
        val lastDay = YearMonth.from(mes).atEndOfMonth()
        var lucros = 0.0
        var prejuizos = 0.0
        var totalDeVendas = 0.0

        val operacoesDoMes = operacaoRepository.findAll()
            .filter { it.data >= firstDay && it.data <= lastDay }
            .filter { it.tipoDaAcao !== TipoAcao.FUNDO_IMOBILIARIO }

        if (operacoesDoMes.isNotEmpty()) {
            val compras = operacoesDoMes
                .filter { it.tipoDaOperacao == TipoOperacao.COMPRA }
                .groupBy { it.papel.codigo }
                .mapValues { map ->
                    map.value.sumByDouble { it.valorTotal }
                }

            val vendas = operacoesDoMes
                .filter { it.tipoDaOperacao == TipoOperacao.VENDA }
                .groupBy { it.papel.codigo }
                .mapValues { map ->
                    map.value.sumByDouble { it.valorTotal }
                }

            compras.entries.zip(vendas.entries) { compra, venda ->
                if (venda.value > 0) {
                    totalDeVendas += venda.value

                    val resultado = venda.value - compra.value
                    if (resultado > 0) {
                        lucros += resultado
                    } else if (resultado < 0) {
                        prejuizos += resultado
                    }
                }
            }
        }

        if (totalDeVendas > 20000) {
            if (prejuizos > 0) {
//            Se vamos deduzir este prejuízo da base de cálculo do IR,
//            ele passa ser OBRIGATÓRIO a informar na declaração,
//            na ficha Renda variável/Operações comuns.
                totalDeVendas -= prejuizos
            }

            // TODO: Precisa remover os custos antes
            return ((totalDeVendas * 0.15) - 0.00005).round()
        }

        return 0.0
    }
}