package com.github.jeancsanchez.investments.domain

import com.github.jeancsanchez.investments.domain.model.dto.ConsolidadoDTO
import com.github.jeancsanchez.investments.data.OperacaoRepository
import com.github.jeancsanchez.investments.data.PapelRepository
import com.github.jeancsanchez.investments.domain.model.TOperacao
import com.github.jeancsanchez.investments.domain.model.TipoOperacao
import com.github.jeancsanchez.investments.view.extractPapelName
import com.github.jeancsanchez.investments.domain.model.dto.OperacaoConsolidadaDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * @author @jeancsanchez
 * @created 21/05/2021
 * Jesus loves you.
 */

@Service
class RelatorioService(
    @Autowired private val operacaoRepository: OperacaoRepository,
    @Autowired private val papelRepository: PapelRepository
) {
    fun pegarOperacoesConsolidadas(): ConsolidadoDTO {
        val items = operacaoRepository.findAll()
            .toMutableList()
            .handleJSLG()
            .groupBy { it.papel.codigo.extractPapelName() }
            .map { map ->
                val valorCompras = map.value
                    .filter { it.tipoDaOperacao == TipoOperacao.COMPRA }
                    .sumByDouble { it.valorTotal }

                val quantidadeCompras = map.value
                    .filter { it.tipoDaOperacao == TipoOperacao.COMPRA }
                    .sumBy { it.quantidade }

                val quantidadeVendas = map.value
                    .filter { it.tipoDaOperacao == TipoOperacao.VENDA }
                    .sumBy { it.quantidade }

                val quantidade = quantidadeCompras - quantidadeVendas
                val precoMedio = if (quantidade > 0) {
                    valorCompras / quantidadeCompras
                } else {
                    0.0
                }
                val precoTotal = quantidade * precoMedio

                OperacaoConsolidadaDTO(
                    papel = map.key,
                    quantidadeTotal = quantidade,
                    precoMedio = BigDecimal(precoMedio).setScale(2, RoundingMode.HALF_EVEN).toDouble(),
                    totalInvestido = BigDecimal(precoTotal).setScale(2, RoundingMode.HALF_EVEN).toDouble()
                )
            }

        return ConsolidadoDTO(
            totalInvestido = items.sumByDouble { it.totalInvestido },
            items = items.sortedByDescending { it.totalInvestido }
        )
    }

    /**
     * O papel JSLG mudou o nome para SIMH. Esse método substitui as operações
     * de JSLG por SIMH. Apenas o nome.
     */
    private fun MutableList<TOperacao>.handleJSLG(): List<TOperacao> {
        replaceAll {
            if (it.papel.codigo == "JSLG3") {
                it.copy(papel = it.papel.copy(codigo = "SIMH3"))
            } else {
                it
            }
        }
        return this
    }
}