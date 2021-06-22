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
        val governoBR = GovernoBR().run {
            governoRepository.findTop1ByNomePaisIgnoreCase(nomePais) ?: governoRepository.save(this)
        }

        val bolsaB3 = B3().also { b3 ->
            bolsaRepository.findTop1ByNomeIgnoreCase(b3.nome) ?: let {
                b3.governo = governoBR
                bolsaRepository.save(b3)
            }
        }

        ClearCorretora().also { clear ->
            corretoraRepository.findTop1ByNomeIgnoreCase(clear.nome) ?: let {
                clear.bolsa = bolsaB3
                corretoraRepository.save(clear)
            }
        }
    }
}