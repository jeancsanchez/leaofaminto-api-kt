package com.github.jeancsanchez.investments.domain

import com.github.jeancsanchez.investments.data.ComprasRepository
import com.github.jeancsanchez.investments.data.VendasRepository
import com.github.jeancsanchez.investments.domain.model.Compra
import com.github.jeancsanchez.investments.domain.model.dto.ConsolidadoDTO
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
class GerarOperacoesConsolidadasService(
    @Autowired private val comprasRepository: ComprasRepository,
    @Autowired private val vendasRepository: VendasRepository,
) : IDomainService<Unit, ConsolidadoDTO> {

    override fun execute(param: Unit): ConsolidadoDTO {
        val items = comprasRepository.findAll()
            .toMutableList()
            .handleJSLG()
            .groupBy { it.ativo.codigo }
            .map { map ->
                val valorCompras = map.value
                    .sumByDouble { it.valorTotal }

                val quantidadeCompras = map.value
                    .sumBy { it.quantidade }

                val quantidadeVendas = vendasRepository.findAllByAtivoCodigo(map.key)
                    .sumBy { it.quantidade }

                val quantidade = quantidadeCompras - quantidadeVendas
                val precoMedio = if (quantidade > 0) {
                    valorCompras / quantidadeCompras
                } else {
                    0.0
                }
                val precoTotal = quantidade * precoMedio

                OperacaoConsolidadaDTO(
                    codigoAtivo = map.key,
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
    private fun MutableList<Compra>.handleJSLG(): List<Compra> {
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