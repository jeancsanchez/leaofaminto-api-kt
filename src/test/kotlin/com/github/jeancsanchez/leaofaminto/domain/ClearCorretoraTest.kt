package com.github.jeancsanchez.leaofaminto.domain

import com.github.jeancsanchez.leaofaminto.domain.model.TipoDeAtivo
import com.github.jeancsanchez.leaofaminto.domain.model.Venda
import com.github.jeancsanchez.leaofaminto.domain.model.corretoras.ClearCorretora
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * @author @jeancsanchez
 * @created 05/07/2021
 * Jesus loves you.
 */

class ClearCorretoraTest {

    @Test
    fun `Acoes - Nao cobra taxa em lucro day trade`() {
        val clearCorretora = ClearCorretora()
        val venda = mock<Venda>().also {
            whenever(it.ativo).thenAnswer { FakeFactory.getVendas().first().ativo }
            whenever(it.ativo.tipoDeAtivo).thenAnswer { TipoDeAtivo.ACAO }
            whenever(it.tipoTrade).thenAnswer { Venda.TipoTrade.SWING_TRADE }
        }

        val resultado = clearCorretora.taxarLucroVenda(venda, 1000.0)

        assertEquals(0.0, resultado)
    }

    @Test
    fun `Acoes - Nao cobra taxa em lucro em swing trade`() {
        val clearCorretora = ClearCorretora()
        val venda = mock<Venda>().also {
            whenever(it.ativo).thenAnswer { FakeFactory.getVendas().first().ativo }
            whenever(it.ativo.tipoDeAtivo).thenAnswer { TipoDeAtivo.ACAO }
            whenever(it.tipoTrade).thenAnswer { Venda.TipoTrade.SWING_TRADE }
        }

        val resultado = clearCorretora.taxarLucroVenda(venda, 1000.0)

        assertEquals(0.0, resultado)
    }

    @Test
    fun `FIIs - Nao cobra taxa em lucro day trade`() {
        val clearCorretora = ClearCorretora()
        val venda = mock<Venda>().also {
            whenever(it.ativo).thenAnswer { FakeFactory.getVendas().first().ativo }
            whenever(it.ativo.tipoDeAtivo).thenAnswer { TipoDeAtivo.FII }
            whenever(it.tipoTrade).thenAnswer { Venda.TipoTrade.DAY_TRADE }
        }

        val resultado = clearCorretora.taxarLucroVenda(venda, 1000.0)

        assertEquals(0.0, resultado)
    }

    @Test
    fun `FIIs - Nao cobra taxa em lucro swing trade`() {
        val clearCorretora = ClearCorretora()
        val venda = mock<Venda>().also {
            whenever(it.ativo).thenAnswer { FakeFactory.getVendas().first().ativo }
            whenever(it.ativo.tipoDeAtivo).thenAnswer { TipoDeAtivo.FII }
            whenever(it.tipoTrade).thenAnswer { Venda.TipoTrade.SWING_TRADE }
        }

        val resultado = clearCorretora.taxarLucroVenda(venda, 1000.0)

        assertEquals(0.0, resultado)
    }

    @Test
    fun `Compra - Nao cobra taxa sobre operacao de compra`() {
        val clearCorretora = ClearCorretora()
        val venda = FakeFactory.getCompras().first()

        val resultado = clearCorretora.taxarOperacao(venda)

        assertEquals(0.0, resultado)
    }

    @Test
    fun `Venda - Nao cobra taxa sobre operacao de venda`() {
        val clearCorretora = ClearCorretora()
        val venda = FakeFactory.getVendas().first()

        val resultado = clearCorretora.taxarOperacao(venda)

        assertEquals(0.0, resultado)
    }
}