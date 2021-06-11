package com.github.jeancsanchez.investments.domain

import com.github.jeancsanchez.investments.domain.model.*
import java.time.LocalDate

/**
 * @author @jeancsanchez
 * @created 21/05/2021
 * Jesus loves you.
 */

object FakeFactory {

    fun createOperacao(): TOperacao {
        return TOperacao(
            papel = Papel(codigo = "ITS4"),
            quantidade = 1,
            tipoDaOperacao = TipoOperacao.COMPRA,
            tipoDaAcao = TipoAcao.ORDINARIA,
            corretora = TCorretora(nome = "Clear"),
            tipoDeLote = TipoDeLote.LOTE_DE_100,
            data = LocalDate.now(),
            preco = 10.0
        )
    }

    fun getOperacoes(): List<TOperacao> {
        return listOf(
            createOperacao().copy(
                papel = Papel(codigo = "JSLG3"),
                quantidade = 50,
                preco = 18.50,
                tipoDaOperacao = TipoOperacao.COMPRA
            ),
            createOperacao().copy(
                papel = Papel(codigo = "SIMH3"),
                quantidade = 13,
                preco = 29.29,
                tipoDaOperacao = TipoOperacao.VENDA
            ),
            createOperacao().copy(
                papel = Papel(codigo = "SIMH3"),
                quantidade = 17,
                preco = 29.29,
                tipoDaOperacao = TipoOperacao.VENDA
            ),
            createOperacao().copy(
                papel = Papel(codigo = "CEAB3"),
                quantidade = 40,
                preco = 11.94,
                tipoDaOperacao = TipoOperacao.COMPRA
            ),
            createOperacao().copy(
                papel = Papel(codigo = "CEAB3F"),
                quantidade = 75,
                preco = 13.41,
                tipoDaOperacao = TipoOperacao.COMPRA
            ),
            createOperacao().copy(
                papel = Papel(codigo = "CEAB3"),
                quantidade = 3,
                preco = 11.99,
                tipoDaOperacao = TipoOperacao.COMPRA
            ),
            createOperacao().copy(
                papel = Papel(codigo = "CEAB3"),
                quantidade = 68,
                preco = 13.73,
                tipoDaOperacao = TipoOperacao.VENDA
            )
        )
    }
}