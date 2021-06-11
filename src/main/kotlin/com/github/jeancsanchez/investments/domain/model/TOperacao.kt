package com.github.jeancsanchez.investments.domain.model

import com.github.jeancsanchez.investments.view.formatToStringBR
import com.github.jeancsanchez.investments.view.round
import java.time.LocalDate
import javax.persistence.*

/**
 * @author @jeancsanchez
 * @created 15/05/2021
 * Jesus loves you.
 */

@Entity
data class TOperacao(
    @Id @GeneratedValue var id: Long? = null,

    @ManyToOne
    var papel: Papel,

    @ManyToOne
    var corretora: TCorretora,

    @Enumerated(EnumType.STRING)
    var tipoDaOperacao: TipoOperacao,

    @Enumerated(EnumType.STRING)
    var tipoDaAcao: TipoAcao,

    var tipoDeLote: String,
    var data: LocalDate = LocalDate.now(),
    var preco: Double,
    var quantidade: Int
) {
    var valorTotal: Double = 0.0
        private set
        get() = quantidade * preco

    var hashId: String = ""
        private set
        get() = data.formatToStringBR()
            .plus(papel.codigo)
            .plus(corretora.id)
            .plus(tipoDaOperacao.id)
            .plus(tipoDaAcao.id)
            .plus(tipoDeLote)
            .plus(preco.round())
            .plus(quantidade)

    override fun toString(): String {
        return "${data.formatToStringBR()} - ${tipoDaOperacao.id} - ${papel.codigo} - $quantidade - $preco"
    }
}