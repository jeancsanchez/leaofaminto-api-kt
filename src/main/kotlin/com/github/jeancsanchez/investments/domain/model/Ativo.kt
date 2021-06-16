package com.github.jeancsanchez.investments.domain.model

import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

/**
 * @author @jeancsanchez
 * @created 10/06/2021
 * Jesus loves you.
 */

@Entity
data class Ativo(
    @Id
    var codigo: String = "",
    var nome: String = "",
    var cnpj: String = "",

    @Enumerated(EnumType.STRING)
    var tipoDeAtivo: TipoDeAtivo = TipoDeAtivo.ACAO
)