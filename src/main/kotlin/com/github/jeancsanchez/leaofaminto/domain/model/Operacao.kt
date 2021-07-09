package com.github.jeancsanchez.leaofaminto.domain.model

import com.github.jeancsanchez.leaofaminto.view.formatToStringBR
import com.github.jeancsanchez.leaofaminto.view.round
import java.time.LocalDate
import javax.persistence.*

/**
 * @author @jeancsanchez
 * @created 10/06/2021
 * Jesus loves you.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
open class Operacao(

    @ManyToOne
    var ativo: Ativo,

    @ManyToOne
    var corretora: Corretora,

    var data: LocalDate = LocalDate.now(),
    var preco: Double,
    var quantidade: Int
) {
    @Id
    @GeneratedValue
    var id: Long? = null

    var valorTotal: Double = 0.0
        protected set
        get() = quantidade * preco

    var hashId: String = ""
        private set
        get() = data.formatToStringBR()
            .plus(ativo.codigo)
            .plus(corretora.id)
            .plus(ativo.tipoDeAtivo.name)
            .plus(preco.round())
            .plus(quantidade)

    override fun toString(): String {
        return "${data.formatToStringBR()} - ${javaClass.simpleName} - ${ativo.codigo} - $quantidade - $preco"
    }
}