package com.github.jeancsanchez.investments.domain.novos

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
) : Operacao(ativo, corretora, data, preco, quantidade)