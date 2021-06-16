package com.github.jeancsanchez.investments.domain

import com.github.jeancsanchez.investments.data.ComprasRepository
import com.github.jeancsanchez.investments.data.OperacaoRepository
import com.github.jeancsanchez.investments.data.VendasRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

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
    @Autowired private val operacaoRepository: OperacaoRepository,
    @Autowired private val comprasRepository: ComprasRepository,
    @Autowired private val vendasRepository: VendasRepository,

    )