package com.github.jeancsanchez.leaofaminto.domain.model

import com.github.jeancsanchez.leaofaminto.domain.model.corretoras.Corretora
import java.time.LocalDate
import javax.persistence.Entity

/**
 * @author @jeancsanchez
 * @created 10/06/2021
 * Jesus loves you.
 */

@Entity
class Compra(
    ativo: Ativo,
    corretora: Corretora,
    data: LocalDate = LocalDate.now(),
    preco: Double,
    quantidade: Int
) : Operacao(ativo, corretora, data, preco, quantidade) {

    /**
     * Acrescenta a taxa informada ao valor total da operação
     * @param valor valor a ser taxado.
     */
    fun acrescentarTaxa(valor: Double) {
        valorTotal += valor
    }
}