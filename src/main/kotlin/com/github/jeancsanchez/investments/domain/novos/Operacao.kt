package com.github.jeancsanchez.investments.domain.novos

import com.github.jeancsanchez.investments.domain.model.Corretora
import com.github.jeancsanchez.investments.domain.model.Papel
import com.github.jeancsanchez.investments.domain.model.TipoAcao
import com.github.jeancsanchez.investments.domain.model.TipoOperacao
import com.github.jeancsanchez.investments.view.formatToStringBR
import com.github.jeancsanchez.investments.view.round
import java.time.LocalDate
import javax.persistence.*

/**
 * @author @jeancsanchez
 * @created 10/06/2021
 * Jesus loves you.
 */

open class Operacao(
    @OneToOne
    var ativo: Ativo,

    @OneToMany
    var corretora: Corretora,

    var data: LocalDate = LocalDate.now(),
    var preco: Double,
    var quantidade: Int
) {
    @Id
    @GeneratedValue
    var id: Long? = null

    var valorTotal: Double = 0.0
        private set
        get() = quantidade * preco

    var hashId: String = ""
        private set
        get() = data.formatToStringBR()
            .plus(ativo.codigo)
            .plus(corretora.id)
            .plus(ativo.classeDeAtivo.name)
            .plus(preco.round())
            .plus(quantidade)

    override fun toString(): String {
        return "${data.formatToStringBR()} - ${javaClass.simpleName} - ${ativo.codigo} - $quantidade - $preco"
    }
}