package com.github.jeancsanchez.leaofaminto.domain

import com.github.jeancsanchez.leaofaminto.domain.model.ClearCorretora
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author @jeancsanchez
 * @created 05/07/2021
 * Jesus loves you.
 */

class ClearCorretoraTest {

    @Test
    fun `Nao cobra taxa em lucro day trade`() {
//        val clearCorretora = ClearCorretora()
//        val resultado = clearCorretora.taxarLucroDayTrade(1000.0)
//        assertEquals(0.0, resultado)
    }

    @Test
    fun `Nao cobra taxa em lucro em swing trade`() {
//        val clearCorretora = ClearCorretora()
//        val resultado = clearCorretora.taxarLucroSwingTrade(1000.0)
//        assertEquals(0.0, resultado)
    }

    @Test
    fun `Nao cobra taxa em lucro com FIIs`() {
//        val clearCorretora = ClearCorretora()
//        val resultado = clearCorretora.taxarLucroFII(1000.0)
//        assertEquals(0.0, resultado)
    }

    @Test
    fun `Nao cobra taxa sobre operacao de compra`() {
        val clearCorretora = ClearCorretora()
        val venda = FakeFactory.getCompras().first()

        val resultado = clearCorretora.taxarOperacao(venda)

        assertEquals(0.0, resultado)
    }

    @Test
    fun `Nao cobra taxa sobre operacao de venda`() {
        val clearCorretora = ClearCorretora()
        val venda = FakeFactory.getVendas().first()

        val resultado = clearCorretora.taxarOperacao(venda)

        assertEquals(0.0, resultado)
    }
}