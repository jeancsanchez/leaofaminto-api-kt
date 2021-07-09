package com.github.jeancsanchez.leaofaminto.domain

import com.github.jeancsanchez.leaofaminto.data.ComprasRepository
import com.github.jeancsanchez.leaofaminto.data.VendasRepository
import com.github.jeancsanchez.leaofaminto.domain.model.Corretora
import com.github.jeancsanchez.leaofaminto.domain.model.TipoDeAtivo
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
class BuscarLucroLiquidoNoMesComFIIsService(
    @Autowired private val comprasRepository: ComprasRepository,
    @Autowired private val vendasRepository: VendasRepository
) : IDomainService<LocalDate, List<BuscarLucroLiquidoNoMesComFIIsService.LucroFIIByCorretora>> {

    data class LucroFIIByCorretora(
        val corretora: Corretora,
        val impostos: Double,
        val lucroLiquido: Double
    )

    override fun execute(param: LocalDate): List<LucroFIIByCorretora> {
        val firstDay = YearMonth.from(param).atDay(1)
        val lastDay = YearMonth.from(param).atEndOfMonth()

        val vendasNoMes = vendasRepository.findAll()
            .filter { it.data >= firstDay && it.data <= lastDay }
            .filter { it.ativo.tipoDeAtivo == TipoDeAtivo.FII }

        if (vendasNoMes.isNotEmpty()) {
            // TODO: Pra uma melhor perfomance, talvez seria melhor filtrar as compras que tiveram vendas no mês
            return comprasRepository.findAll()
                .sortedBy { it.ativo.codigo }
                .zip(vendasNoMes.sortedBy { it.ativo.codigo })
                .groupBy { it.first.corretora }
                .map { map ->
                    val corretora = map.key
                    val operacoes = map.value
                    val lucroBruto = operacoes.sumByDouble {
                        val compra = it.first
                        val venda = it.second

                        if (venda.valorTotal > compra.valorTotal) {
                            venda.valorTotal - compra.valorTotal
                        } else {
                            compra.valorTotal - (compra.valorTotal - venda.valorTotal)
                        }
                    }

//                    val imposto = corretora.bolsa.governo.taxarLucroFII(lucroBruto)
//                    val custosOperacionais =
//                        corretora.taxarLucroFII(lucroBruto) + corretora.bolsa.taxarLucroFII(lucroBruto)
//                    val lucroLiquido = lucroBruto - (imposto + custosOperacionais)

                    LucroFIIByCorretora(
                        corretora = corretora,
//                        impostos = imposto,
//                        lucroLiquido = lucroLiquido
                        impostos = 0.0,
                        lucroLiquido = 0.0
                    )
                }
        }

        return emptyList()
    }
}