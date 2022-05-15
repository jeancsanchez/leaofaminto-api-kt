package com.github.jeancsanchez.leaofaminto.domain

import com.github.jeancsanchez.leaofaminto.domain.model.Compra
import com.github.jeancsanchez.leaofaminto.domain.model.Venda
import com.github.jeancsanchez.leaofaminto.domain.model.base.MethodNotAllowedException
import com.github.jeancsanchez.leaofaminto.domain.model.governos.GovernoBR
import com.github.jeancsanchez.leaofaminto.view.round
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*

/**
 * @author @jeancsanchez
 * @created 05/07/2021
 * Jesus loves you.
 */

class GovernoBRTest {

    @Test
    fun `Compra - Nao cobra taxa sobre operacao de compra`() {
        val governo = GovernoBR()
        val compra = mock<Compra>().also {
            whenever(it.valorTotal).thenAnswer { 3000.0 }
        }

        val resultado = governo.taxarOperacao(compra)

        assertEquals(0.0, resultado)
        verify(compra, times(0)).acrescentarTaxa(any())
    }

    @Test
    fun `Venda - Operacao de venda deve ser informado valor do lucro`() {
        val governo = GovernoBR()
        val venda = mock<Venda>()

        assertThrows<MethodNotAllowedException> {
            governo.taxarOperacao(venda)
        }
    }

    /**
     * Exemplo retirado de: https://www.btgpactualdigital.com/blog/coluna-do-assessor/o-que-e-darf-como-gerar-e-calcular
     * O investidor compra 1.000 ações da Petrobras (PETR4) a R$ 25,00 e as vende no mesmo dia a R$ 26,00.
     * Compra 1.000 x R$ 25,00 = R$ 25.000,00
     * Venda 1.000 x R$26,00 = R$ 26.000,00
     * [...]
     * Lucro da operação = (Valor de Venda – Valor de compra) – Taxas operacionais
     * Lucro da operação = (R$26.000,00 – R$25.000,00) – R$1,10 = R$998,90
     * IRRF (1%) = R$9,98
     * DARF (20% – menos o valor do IRRF) = R$189,79
     */
    @Test
    fun `DAY Trade - Cobra dedo duro e imposto sobre venda`() {
        val governo = GovernoBR()
        val venda = mock<Venda>().also {
            whenever(it.valorTotal).thenAnswer { 26000.0 }
            whenever(it.tipoTrade).thenAnswer { Venda.TipoTrade.DAY_TRADE }
        }

        val resultado = governo.taxarLucroVenda(venda, 998.90)

        assertEquals(189.79, resultado)
        verify(venda, times(1)).descontarTaxa(189.79)
    }


    /**
     * Exemplo retira de: https://www.btgpactualdigital.com/blog/coluna-do-assessor/o-que-e-darf-como-gerar-e-calcular
     * O investidor comprou 500 ações/units do BTG Pactual (BPAC11) a R$ 85,00 e vendeu 15 dias depois a R$ 90,00.
     * Compra 500 x R$ 85,00 = R$ 42.500,00
     * Venda 500 x R$ 90,00 = R$ 45.000,00
     * Lucro da operação = (Valor de Venda – Valor de compra) – Taxas operacionais
     * Lucro = R$ 2.500,00
     * IRRF (0,005%) sobre o valor da venda = R$ 2,25
     * DARF (15%) sobre lucro – IRRF = R$ 375,00 – R$ 2,25 = R$ 372,75
     */
    @Test
    fun `Swing Trade - Cobra dedo duro e imposto sobre venda`() {
        val governo = GovernoBR()
        val venda = mock<Venda>().also {
            whenever(it.valorTotal).thenAnswer { 45000.0 }
            whenever(it.tipoTrade).thenAnswer { Venda.TipoTrade.SWING_TRADE }
        }

        val resultado = governo.taxarLucroVenda(venda, 2500.0)

        assertEquals(372.75, resultado)
        verify(venda, times(1)).descontarTaxa(372.75)
    }

    /**
     * Exemplo retirado de: https://www.btgpactualdigital.com/blog/coluna-do-assessor/o-que-e-darf-como-gerar-e-calcular
     * O investidor comprou 100 ações do Magazine Luiza (MGLU3) a R$ 25,00 e vendeu após 6 meses por R$ 30,00.
     * Lucro da operação = (Valor de Venda – Valor de compra) – Taxas operacionais
     * Compra 100 x R$ 25,00 = R$ 2.500,00
     * Venda 100 x R$ 30,00 = R$ 3.000,00
     * Lucro = R$ 500,00
     */
    @Test
    fun `Swing Trade - Insencao para vendas com valor menor ou igual a 20000`() {
        val governo = GovernoBR()
        val venda = mock<Venda>().also {
            whenever(it.valorTotal).thenAnswer { 3000.0 }
            whenever(it.tipoTrade).thenAnswer { Venda.TipoTrade.SWING_TRADE }
        }

        val resultado = governo.taxarLucroVenda(venda, 500.0)

        assertEquals(0.0, resultado)
        verify(venda, times(0)).descontarTaxa(any())
    }

    /**
     * Exemplo retirado de: https://www.btgpactualdigital.com/blog/coluna-do-assessor/o-que-e-darf-como-gerar-e-calcular
     * O investidor comprou 500 ações/units do BTG Pactual (BPAC11) a R$ 85,00 e vendeu 15 dias depois a R$ 90,00.
     * Compra 500 x R$ 85,00 = R$ 42.500,00
     * Venda 500 x R$ 90,00 = R$ 45.000,00
     * Lucro da operação = (Valor de Venda – Valor de compra) – Taxas operacionais
     * Lucro = R$ 2.500,00
     * IRRF (0,005%) sobre o valor da venda = R$ 2,25
     * DARF (15%) sobre lucro – IRRF = R$ 375,00 – R$ 2,25 = R$ 372,75
     */
    @Test
    fun `Dedo duro - venda swing trade (sobre o volume de venda)`() {
        val governo = GovernoBR()

        val resultado = governo.recolherDedoDuro(45000.0, Venda.TipoTrade.SWING_TRADE)

        assertEquals(2.25, resultado)
    }

    /**
     * Exemplo retirado de: https://www.btgpactualdigital.com/blog/coluna-do-assessor/o-que-e-darf-como-gerar-e-calcular
     * O investidor compra 1.000 ações da Petrobras (PETR4) a R$ 25,00 e as vende no mesmo dia a R$ 26,00.
     * Compra 1.000 x R$ 25,00 = R$ 25.000,00
     * Venda 1.000 x R$26,00 = R$ 26.000,00
     * [...]
     * Lucro da operação = (Valor de Venda – Valor de compra) – Taxas operacionais
     * Lucro da operação = (R$26.000,00 – R$25.000,00) – R$1,10 = R$998,90
     * IRRF (1%) = R$9,98
     * DARF (20% – menos o valor do IRRF) = R$189,79
     */
    @Test
    fun `Dedo duro - venda day trade (sobre o lucro)`() {
        val governo = GovernoBR()

        val resultado = governo.recolherDedoDuro(998.90, Venda.TipoTrade.DAY_TRADE)

        // Arredonda pra cima
        assertEquals(9.99 , resultado.round() )
    }
}