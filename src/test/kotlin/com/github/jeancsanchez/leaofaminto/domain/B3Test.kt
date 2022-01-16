package com.github.jeancsanchez.leaofaminto.domain

import com.github.jeancsanchez.leaofaminto.domain.model.Compra
import com.github.jeancsanchez.leaofaminto.domain.model.TipoDeAtivo
import com.github.jeancsanchez.leaofaminto.domain.model.Venda
import com.github.jeancsanchez.leaofaminto.domain.model.bolsas.B3
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

/**
 * @author @jeancsanchez
 * @created 05/07/2021
 * Jesus loves you.
 */

class B3Test {

    @Test
    fun `Acoes - Nao cobra taxa em lucro day trade`() {
        val b3 = B3()
        val venda = mock<Venda>().also {
            whenever(it.ativo).thenAnswer { FakeFactory.getVendas().first().ativo }
            whenever(it.ativo.tipoDeAtivo).thenAnswer { TipoDeAtivo.ACAO }
            whenever(it.tipoTrade).thenAnswer { Venda.TipoTrade.SWING_TRADE }
        }

        val resultado = b3.taxarLucroVenda(venda, 1000.0)

        assertEquals(0.0, resultado)
    }

    @Test
    fun `Acoes - Nao cobra taxa em lucro em swing trade`() {
        val b3 = B3()
        val venda = mock<Venda>().also {
            whenever(it.ativo).thenAnswer { FakeFactory.getVendas().first().ativo }
            whenever(it.ativo.tipoDeAtivo).thenAnswer { TipoDeAtivo.ACAO }
            whenever(it.tipoTrade).thenAnswer { Venda.TipoTrade.SWING_TRADE }
        }

        val resultado = b3.taxarLucroVenda(venda, 1000.0)

        assertEquals(0.0, resultado)
    }

    @Test
    fun `FIIs - Nao cobra taxa em lucro day trade`() {
        val b3 = B3()
        val venda = mock<Venda>().also {
            whenever(it.ativo).thenAnswer { FakeFactory.getVendas().first().ativo }
            whenever(it.ativo.tipoDeAtivo).thenAnswer { TipoDeAtivo.FII }
            whenever(it.tipoTrade).thenAnswer { Venda.TipoTrade.DAY_TRADE }
        }

        val resultado = b3.taxarLucroVenda(venda, 1000.0)

        assertEquals(0.0, resultado)
    }

    @Test
    fun `FIIs - Nao cobra taxa em lucro swing trade`() {
        val b3 = B3()
        val venda = mock<Venda>().also {
            whenever(it.ativo).thenAnswer { FakeFactory.getVendas().first().ativo }
            whenever(it.ativo.tipoDeAtivo).thenAnswer { TipoDeAtivo.FII }
            whenever(it.tipoTrade).thenAnswer { Venda.TipoTrade.SWING_TRADE }
        }

        val resultado = b3.taxarLucroVenda(venda, 1000.0)

        assertEquals(0.0, resultado)
    }

    /**
     * Exemplo retirado de: https://comoinvestir.thecap.com.br/emolumentos/
     * Você compra 200 ações por R$ 15,00 cada, e no dia seguinte as vende por R$ 17,00;
     * O valor da compra foi de R$ 3.000,00, então, a uma taxa de 0,0300%, o valor dos será de R$ 0,90;
     * Já, o valor da venda foi de R$ 3.400,00, então, a uma taxa de 0,0300%, o valor será de R$ 1,02.
     */
    @Test
    fun `Compra - Cobra emolumentos sobre operacao de compra`() {
        val b3 = B3()
        val compra = mock<Compra>().also {
            whenever(it.preco).thenAnswer { 15.0 }
            whenever(it.quantidade).thenAnswer { 200 }
            whenever(it.valorTotal).thenAnswer { 3000.0 }
        }

        val resultado = b3.taxarOperacao(compra)

        verify(compra, times(1)).acrescentarTaxa(eq(0.90))
        assertEquals(0.90, resultado)
    }

    /**
     * Exemplo retirado de: https://comoinvestir.thecap.com.br/emolumentos/
     * Você compra 200 ações por R$ 15,00 cada, e no dia seguinte as vende por R$ 17,00;
     * O valor da compra foi de R$ 3.000,00, então, a uma taxa de 0,0300%, o valor dos será de R$ 0,90;
     * Já, o valor da venda foi de R$ 3.400,00, então, a uma taxa de 0,0300%, o valor será de R$ 1,02.
     */
    @Test
    fun `Venda - Cobra emolumentos sobre operacao de venda swing trade`() {
        val b3 = B3()
        val venda = mock<Venda>().also {
            whenever(it.preco).thenAnswer { 17.0 }
            whenever(it.quantidade).thenAnswer { 200 }
            whenever(it.valorTotal).thenAnswer { 3400.0 }
            whenever(it.tipoTrade).thenAnswer { Venda.TipoTrade.SWING_TRADE }
        }

        val resultado = b3.taxarOperacao(venda)

        verify(venda, times(1)).descontarTaxa(eq(1.02))
        assertEquals(1.02, resultado)
    }

    /**
     * Venda de 1.000 ações por R$ 1.000 em uma operação de day trade.
     * Emolumentos da faixa de 0 a R$ 1.000.000 é 0,0230%
     */
    @Test
    fun `Compra - Cobra emolumentos sobre operacao de compra day trade`() {
        val b3 = B3()
        val venda = mock<Venda>().also {
            whenever(it.preco).thenAnswer { 1000.0 }
            whenever(it.quantidade).thenAnswer { 1000 }
            whenever(it.valorTotal).thenAnswer { 1000000.0 }
            whenever(it.tipoTrade).thenAnswer { Venda.TipoTrade.DAY_TRADE }
        }

        val resultado = b3.taxarOperacao(venda)

        verify(venda, times(1)).descontarTaxa(eq(230.0))
        assertEquals(230.0, resultado)
    }
}