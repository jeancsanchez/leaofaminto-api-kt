package com.github.jeancsanchez.investments.domain

import com.github.jeancsanchez.investments.data.ComprasRepository
import com.github.jeancsanchez.investments.data.VendasRepository
import com.github.jeancsanchez.investments.domain.model.TipoDeAtivo
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
) : IDomainService<LocalDate, Double> {

    override fun execute(param: LocalDate): Double {
        val firstDay = YearMonth.from(param).atDay(1)
        val lastDay = YearMonth.from(param).atEndOfMonth()

        val vendasNoMes = vendasRepository.findAll()
            .filter { it.data >= firstDay && it.data <= lastDay }
            .filter { it.ativo.tipoDeAtivo == TipoDeAtivo.FII }

        if (vendasNoMes.isNotEmpty()) {
            val compras = comprasRepository.findAll()
                .filter { it.data >= firstDay && it.data <= lastDay }
                .filter { it.ativo.tipoDeAtivo == TipoDeAtivo.FII }

            return vendasNoMes.sumByDouble { it.valorTotal } - compras.sumByDouble { it.valorTotal }
        }

        return 0.0
    }
}