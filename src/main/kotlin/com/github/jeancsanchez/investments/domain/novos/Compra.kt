package com.github.jeancsanchez.investments.domain.novos

import com.github.jeancsanchez.investments.domain.model.Corretora
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.OneToMany
import javax.persistence.OneToOne

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