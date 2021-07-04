package com.github.jeancsanchez.leaofaminto.domain

import com.github.jeancsanchez.leaofaminto.domain.model.*
import java.time.LocalDate

/**
 * @author @jeancsanchez
 * @created 21/05/2021
 * Jesus loves you.
 */

object FakeFactory {

    fun createOperacao(): Operacao {
        return Compra(
            ativo = Ativo(codigo = "ITS4"),
            quantidade = 1,
            corretora = ClearCorretora(),
            data = LocalDate.now(),
            preco = 10.0
        )
    }

    fun getCompras(): List<Compra> {
        return listOf(
            Compra(
                ativo = Ativo(codigo = "JSLG3"),
                quantidade = 50,
                preco = 18.50,
                corretora = ClearCorretora(),
                data = LocalDate.now(),
            ),
            Compra(
                ativo = Ativo(codigo = "CEAB3"),
                quantidade = 40,
                preco = 11.94,
                corretora = ClearCorretora(),
                data = LocalDate.now(),
            ),
            Compra(
                ativo = Ativo(codigo = "CEAB3"),
                quantidade = 75,
                preco = 13.41,
                corretora = ClearCorretora(),
                data = LocalDate.now(),
            ),
            Compra(
                ativo = Ativo(codigo = "CEAB3"),
                quantidade = 3,
                preco = 11.99,
                corretora = ClearCorretora(),
                data = LocalDate.now(),
            ),
        )
    }

    fun getVendas(): List<Venda> {
        return listOf(
            Venda(
                ativo = Ativo(codigo = "SIMH3"),
                quantidade = 13,
                preco = 29.29,
                corretora = ClearCorretora(),
                data = LocalDate.now(),
            ),
            Venda(
                ativo = Ativo(codigo = "SIMH3"),
                quantidade = 17,
                preco = 29.29,
                corretora = ClearCorretora(),
                data = LocalDate.now(),
            ),
            Venda(
                ativo = Ativo(codigo = "CEAB3"),
                quantidade = 68,
                preco = 13.73,
                corretora = ClearCorretora(),
                data = LocalDate.now(),
            )
        )
    }

    fun getOperacoes(): List<Operacao> {
        return listOf<Operacao>()
            .plus(getCompras())
            .plus(getVendas())
    }
}