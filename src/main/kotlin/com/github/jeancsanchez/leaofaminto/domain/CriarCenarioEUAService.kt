package com.github.jeancsanchez.leaofaminto.domain

import com.github.jeancsanchez.leaofaminto.data.BolsaRepository
import com.github.jeancsanchez.leaofaminto.data.CorretoraRepository
import com.github.jeancsanchez.leaofaminto.data.GovernoRepository
import com.github.jeancsanchez.leaofaminto.domain.model.bolsas.BolsaEUA
import com.github.jeancsanchez.leaofaminto.domain.model.corretoras.Passfolio
import com.github.jeancsanchez.leaofaminto.domain.model.governos.GovernoEUA
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @author @jeancsanchez
 * @created 16/06/2021
 * Jesus loves you.
 */

@Service
class CriarCenarioEUAService(
    @Autowired private val governoRepository: GovernoRepository,
    @Autowired private val bolsaRepository: BolsaRepository,
    @Autowired private val corretoraRepository: CorretoraRepository
) : IDomainService<Unit, Unit> {

    override fun execute(param: Unit) {
        val governoAmericano = GovernoEUA().run {
            governoRepository.findTop1ByNomePaisIgnoreCase(nomePais) ?: governoRepository.save(this)
        }

        val bolsaAmericana = BolsaEUA().also { bolsa ->
            bolsaRepository.findTop1ByNomeIgnoreCase(bolsa.nome) ?: let {
                bolsa.governo = governoAmericano
                bolsaRepository.save(bolsa)
            }
        }

        Passfolio().also { clear ->
            corretoraRepository.findTop1ByNomeIgnoreCase(clear.nome) ?: let {
                clear.bolsa = bolsaAmericana
                corretoraRepository.save(clear)
            }
        }
    }
}