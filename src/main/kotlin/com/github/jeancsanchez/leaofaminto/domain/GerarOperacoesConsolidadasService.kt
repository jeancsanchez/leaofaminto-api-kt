package com.github.jeancsanchez.leaofaminto.domain

import com.github.jeancsanchez.leaofaminto.data.ComprasRepository
import com.github.jeancsanchez.leaofaminto.data.VendasRepository
import com.github.jeancsanchez.leaofaminto.domain.model.Compra
import com.github.jeancsanchez.leaofaminto.view.dto.ConsolidadoDTO
import com.github.jeancsanchez.leaofaminto.view.dto.OperacaoConsolidadaDTO
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
class GerarOperacoesConsolidadasService(
    @Autowired private val comprasRepository: ComprasRepository,
    @Autowired private val vendasRepository: VendasRepository,
) : IDomainService<Unit, ConsolidadoDTO> {

    override fun execute(param: Unit): ConsolidadoDTO {
        val items = comprasRepository.findAll()
            .toMutableList()
            .replaceSIMHToJSLG()
            .groupBy { it.ativo }
            .map { map ->
                val valorCompras = map.value
                    .sumByDouble { it.valorTotal }

                val quantidadeCompras = map.value
                    .sumByDouble { it.quantidade }

                val quantidadeVendas = vendasRepository.findAllByAtivoCodigo(map.key.codigo)
                    .sumByDouble { it.quantidade }

                val quantidade = quantidadeCompras - quantidadeVendas
                val precoMedio = if (quantidade > 0) {
                    valorCompras / quantidadeCompras
                } else {
                    0.0
                }
                val precoTotal = quantidade * precoMedio

                OperacaoConsolidadaDTO(
                    ativo = map.key,
                    quantidadeTotal = quantidade,
                    precoMedio = BigDecimal(precoMedio).setScale(2, RoundingMode.HALF_EVEN).toDouble(),
                    totalInvestido = BigDecimal(precoTotal).setScale(2, RoundingMode.HALF_EVEN).toDouble()
                )
            }

        return ConsolidadoDTO(
            totalInvestido = items.sumByDouble { it.totalInvestido },
            items = items
                .filter { it.quantidadeTotal > 0 }
                .sortedByDescending { it.totalInvestido }
        )
    }

    private fun MutableList<Compra>.replaceSIMHToJSLG(): List<Compra> {
        replaceAll { compra ->
            if (compra.ativo.codigo == "JSLG3") {
                compra.ativo.codigo = "SIMH3"
                compra
            } else {
                compra
            }
        }
        return this
    }
}