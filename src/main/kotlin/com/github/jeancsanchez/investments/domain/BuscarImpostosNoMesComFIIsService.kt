package com.github.jeancsanchez.investments.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * @author @jeancsanchez
 * @created 15/06/2021
 * Jesus loves you.
 */

@Service
class BuscarImpostosNoMesComFIIsService(
    @Autowired private val buscarLucroLiquidoNoMesComFIIsServiceService: BuscarLucroLiquidoNoMesComFIIsService
) : IDomainService<LocalDate, Double> {

    override fun execute(param: LocalDate): Double {
        val lucroDoMes = buscarLucroLiquidoNoMesComFIIsServiceService.execute(param)
        if (lucroDoMes > 0) {
            return (lucroDoMes * 0.20)
        }
        return lucroDoMes
    }
}