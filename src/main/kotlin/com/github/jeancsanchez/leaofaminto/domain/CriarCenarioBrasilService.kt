package com.github.jeancsanchez.leaofaminto.domain

import com.github.jeancsanchez.leaofaminto.data.BolsaRepository
import com.github.jeancsanchez.leaofaminto.data.CorretoraRepository
import com.github.jeancsanchez.leaofaminto.data.GovernoRepository
import com.github.jeancsanchez.leaofaminto.domain.model.B3
import com.github.jeancsanchez.leaofaminto.domain.model.ClearCorretora
import com.github.jeancsanchez.leaofaminto.domain.model.GovernoBR
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @author @jeancsanchez
 * @created 16/06/2021
 * Jesus loves you.
 */

@Service
class CriarCenarioBrasilService(
    @Autowired private val governoRepository: GovernoRepository,
    @Autowired private val bolsaRepository: BolsaRepository,
    @Autowired private val corretoraRepository: CorretoraRepository
) : IDomainService<Unit, Unit> {

    override fun execute(param: Unit) {
        val governoBR =
            governoRepository.findTop1ByNomePaisIgnoreCase("Brasil") ?: governoRepository.save(GovernoBR("Brasil"))
        val bolsaB3 = bolsaRepository.findTop1ByNomeIgnoreCase("b3") ?: bolsaRepository.save(B3("B3"))
        val clearCorretora = corretoraRepository.findTop1ByNomeIgnoreCase("Clear") ?: corretoraRepository.save(
            ClearCorretora()
        )

        governoBR.adicionarBolsa(bolsaB3)
        bolsaB3.adicionarCorretora(clearCorretora)

        governoRepository.save(governoBR)
        bolsaRepository.save(bolsaB3)
    }
}