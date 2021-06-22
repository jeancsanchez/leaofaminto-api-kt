package com.github.jeancsanchez.leaofaminto.domain.service

import com.github.jeancsanchez.leaofaminto.data.OperacaoRepository
import com.github.jeancsanchez.leaofaminto.domain.BuscarImpostosNoMesComAcoesDayTradeService
import com.github.jeancsanchez.leaofaminto.domain.model.*
import junit.framework.TestCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate

/**
 * @author @jeancsanchez
 * @created 15/06/2021
 * Jesus loves you.
 */

class BuscarImpostosNoMesComAcoesDayTradeServiceTest {

    @Mock
    lateinit var operacaoRepository: OperacaoRepository

    @InjectMocks
    private lateinit var buscarImpostosNoMesComAcoesDayTradeService: BuscarImpostosNoMesComAcoesDayTradeService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    /**
     * Exemplo retirado de: https://www.btgpactualdigital.com/blog/coluna-do-assessor/o-que-e-darf-como-gerar-e-calcular
     * O investidor compra 1.000 ações da Petrobras (PETR4) a R$ 25,00 e as vende no mesmo dia a R$ 26,00.
     * Compra 1.000 x R$ 25,00 = R$ 25.000,00
     * Venda 1.000 x R$26,00 = R$ 26.000,00
     * Considerando taxas operacionais
     * Taxa de corretagem: R$1,00
     * Emolumentos B3:0,10
     * Lucro da operação = (Valor de Venda – Valor de compra) – Taxas operacionais
     * Lucro da operação = (R$26.000,00 – R$25.000,00) – R$1,10 = R$998,90
     * IRRF (1%) = R$9,98
     * DARF (20% – menos o valor do IRRF) = R$189,80
     */
    @Test
    @Suppress("DANGEROUS_CHARACTERS")
    fun `Day trade (Caso 1)- qualquer lucro no dia com acoes gera imposto de 20% sobre o lucro do mes`() {
        val today = LocalDate.of(2021, 1, 1)
        val governo = mock<Governo>()
        val bolsa = mock<Bolsa>().also { it.governo = governo }
        val corretora = mock<Corretora>().also { it.bolsa = bolsa }

        whenever(governo.recolherDedoDuroDayTrade(any())).thenAnswer { 9.98 }
        whenever(governo.taxarLucroDayTrade(any())).thenAnswer { 199.78 }
        whenever(bolsa.taxarLucroDayTrade(any())).thenAnswer { 0.10 }
        whenever(corretora.taxarLucroDayTrade(any())).thenAnswer { 1.0 }
        whenever(operacaoRepository.findAll()).thenAnswer {
            listOf(
                Compra(
                    ativo = Ativo(codigo = "PETR4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = corretora,
                    quantidade = 1000,
                    preco = 25.0,
                    data = today,
                ),
                Venda(
                    ativo = Ativo(codigo = "PETR4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = corretora,
                    quantidade = 1000,
                    preco = 26.0,
                    data = today
                ),
            )
        }

        val impostos = buscarImpostosNoMesComAcoesDayTradeService.execute(today)
        TestCase.assertEquals(189.80, impostos)
    }

    /**
     * Exemplo retirado de: https://ajuda.easynvest.com.br/hc/pt-br/articles/115005063213-Como-funciona-IR-para-Day-Trade-
     * Comprei e Vendi 1.000 ações da empresa ABC ao preço de R$ 30,00 com lucro total da operação de R$ 200,00.
     * Passo 1 - operação
     * Valor da operação = 1.000 x 30 = R$ 30.000,00
     * Lucro apurado na operação = R$ 200,00
     * Imposto retido na fonte: R$ 200,00 x 1% = R$ 2,00
     * Passo 2 - imposto devido
     * Lucro apurado na operação (hipótese) = R$ 200,00
     * Imposto à pagar = lucro apurado x imposto = valor a ser pago
     * Imposto à pagar = R$ 200 x 20% = R$ 40,00
     * Passo 3 - DARF
     * Valor à ser pago na DARF = Imposto à pagar - Imposto retido na fonte
     * Valor à ser pago na DARF = R$40,00 - R$ 2,00 = R$ 38,00
     */
    @Test
    @Suppress("DANGEROUS_CHARACTERS")
    fun `Day trade (Caso 2) - qualquer lucro no dia com acoes gera imposto de 20% sobre o lucro do mes`() {
        val today = LocalDate.of(2021, 1, 1)
        val governo = mock<Governo>()
        val bolsa = mock<Bolsa>().also { it.governo = governo }
        val corretora = mock<Corretora>().also { it.bolsa = bolsa }

        whenever(governo.recolherDedoDuroDayTrade(any())).thenAnswer { 2.0 }
        whenever(governo.taxarLucroDayTrade(any())).thenAnswer { 40.0 }
        whenever(bolsa.taxarLucroDayTrade(any())).thenAnswer { 0.0 }
        whenever(corretora.taxarLucroDayTrade(any())).thenAnswer { 0.0 }
        whenever(operacaoRepository.findAll()).thenAnswer {
            listOf(
                Compra(
                    ativo = Ativo(codigo = "PETR4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = corretora,
                    quantidade = 1000,
                    preco = 9.80,
                    data = today,
                ),
                Venda(
                    ativo = Ativo(codigo = "PETR4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = corretora,
                    quantidade = 1000,
                    preco = 9.90,
                    data = today
                ),
            )
        }

        val impostos = buscarImpostosNoMesComAcoesDayTradeService.execute(today)
        TestCase.assertEquals(38.0, impostos)
    }

    /**
     * Duas operacoes de day trade em corretoras diferentes.
     * Corretora A:
     *      Compra 10 PETR4 x R$ 70 = 700
     *      Venda 10 PETR4 x R$ 76 = 760
     *      Lucro = R$ 60
     *      Imposto retido na fonte A: R$ 60 x 1% = R$ 0.60
     *      Emolumentos Compra: R$ 700 * 0,0230% = R$ 0.161
     *      Emolumentos Venda: R$ 760 * 0,0230% = R$ 0.1748
     *      Lucro liquido = R$ 60 - R$ 0.3358 = R$ 59.6642
     *      Valor Darf = R$ 60 * 20% = R$ 11.93284 --> 12.0
     *
     * Corretora B:
     *      Compra 10 ABC x R$ 70 = 700
     *      Venda 10 ABC x R$ 76 = 760
     *      Lucro = R$ 60
     *      Imposto retido na fonte A: R$ 60 x 1% = R$ 0.60
     *      Emolumentos Compra: R$ 700 * 0,0230% = R$ 0.161
     *      Emolumentos Venda: R$ 760 * 0,0230% = R$ 0.1748
     *      Lucro liquido = R$ 60 - R$ 0.3358 = R$ 59.6642
     *      Valor Darf = R$ 60 * 20% = R$ 11.93284 --> 12.0
     *
     * Valor total Darf = R$ 23.86568 --> R$ 24.0
     */
    @Test
    @Suppress("DANGEROUS_CHARACTERS")
    fun `Day trade (Caso 3) - qualquer lucro no dia com acoes gera imposto de 20% sobre o lucro do mes`() {
        val today = LocalDate.of(2021, 1, 1)
        val governo = mock<Governo>()
        val bolsa = mock<Bolsa>().also { it.governo = governo }
        val corretora1 = mock<Corretora>().also { it.bolsa = bolsa }
        val corretora2 = mock<Corretora>().also { it.bolsa = bolsa }

        whenever(governo.recolherDedoDuroDayTrade(any())).thenAnswer { 0.60 }
        whenever(bolsa.taxarOperacao(any<Compra>())).thenAnswer { 0.161 }
        whenever(bolsa.taxarOperacao(any<Venda>())).thenAnswer { 0.1748 }
        whenever(bolsa.taxarLucroDayTrade(any())).thenAnswer { 0.0 }
        whenever(corretora1.taxarLucroDayTrade(any())).thenAnswer { 0.0 }
        whenever(corretora2.taxarLucroDayTrade(any())).thenAnswer { 0.0 }
        whenever(operacaoRepository.findAll()).thenAnswer {
            listOf(
                Compra(
                    ativo = Ativo(codigo = "PETR4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = corretora1,
                    quantidade = 10,
                    preco = 70.0,
                    data = today,
                ),
                Venda(
                    ativo = Ativo(codigo = "PETR4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = corretora1,
                    quantidade = 10,
                    preco = 76.0,
                    data = today
                ),
                Compra(
                    ativo = Ativo(codigo = "ABC", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = corretora2,
                    quantidade = 10,
                    preco = 70.0,
                    data = today,
                ),
                Venda(
                    ativo = Ativo(codigo = "ABC", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = corretora2,
                    quantidade = 10,
                    preco = 76.0,
                    data = today
                ),
            )
        }

        val impostos = buscarImpostosNoMesComAcoesDayTradeService.execute(today)
        TestCase.assertEquals(38.0, impostos)
    }

    @Test
    @Suppress("DANGEROUS_CHARACTERS")
    fun `Day trade em corretoras diferentes nao eh considerado para fins de impostos`() {
        val today = LocalDate.of(2021, 1, 1)
        val governo = mock<Governo>()
        val bolsa = mock<Bolsa>().also { it.governo = governo }
        val corretora1 = mock<Corretora>().also { it.bolsa = bolsa }
        val corretora2 = mock<Corretora>().also { it.bolsa = bolsa }

        whenever(governo.recolherDedoDuroDayTrade(any())).thenAnswer { 9.98 }
        whenever(governo.taxarLucroDayTrade(any())).thenAnswer { 199.78 }
        whenever(bolsa.taxarLucroDayTrade(any())).thenAnswer { 0.10 }
        whenever(corretora1.taxarLucroDayTrade(any())).thenAnswer { 1.0 }
        whenever(corretora2.taxarLucroDayTrade(any())).thenAnswer { 1.0 }
        whenever(operacaoRepository.findAll()).thenAnswer {
            listOf(
                Compra(
                    ativo = Ativo(codigo = "PETR4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = corretora1,
                    quantidade = 1000,
                    preco = 25.0,
                    data = today,
                ),
                Venda(
                    ativo = Ativo(codigo = "PETR4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = corretora2,
                    quantidade = 1000,
                    preco = 26.0,
                    data = today
                ),
            )
        }

        val impostos = buscarImpostosNoMesComAcoesDayTradeService.execute(today)
        TestCase.assertEquals(0.0, impostos)
    }

    @Test
    @Suppress("DANGEROUS_CHARACTERS")
    fun `Operacoes em diferentes dias nao eh considerado Day Trade`() {
        val today = LocalDate.of(2021, 1, 1)
        val tomorrow = today.plusDays(1)
        val governo = mock<Governo>()
        val bolsa = mock<Bolsa>().also { it.governo = governo }
        val corretora = mock<Corretora>().also { it.bolsa = bolsa }

        whenever(governo.recolherDedoDuroDayTrade(any())).thenAnswer { 9.98 }
        whenever(governo.taxarLucroDayTrade(any())).thenAnswer { 199.78 }
        whenever(bolsa.taxarLucroDayTrade(any())).thenAnswer { 0.10 }
        whenever(corretora.taxarLucroDayTrade(any())).thenAnswer { 1.0 }
        whenever(operacaoRepository.findAll()).thenAnswer {
            listOf(
                Compra(
                    ativo = Ativo(codigo = "PETR4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = corretora,
                    quantidade = 1000,
                    preco = 25.0,
                    data = today,
                ),
                Venda(
                    ativo = Ativo(codigo = "PETR4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = corretora,
                    quantidade = 1000,
                    preco = 26.0,
                    data = tomorrow
                ),
            )
        }

        val impostos = buscarImpostosNoMesComAcoesDayTradeService.execute(today)
        TestCase.assertEquals(0.0, impostos)
    }
}