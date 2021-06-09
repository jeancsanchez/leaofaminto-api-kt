package com.github.jeancsanchez.investments.domain

import com.github.jeancsanchez.investments.data.OperacaoRepository
import com.github.jeancsanchez.investments.domain.model.LeaoService
import com.github.jeancsanchez.investments.domain.model.Papel
import com.github.jeancsanchez.investments.domain.model.TipoAcao
import com.github.jeancsanchez.investments.domain.model.TipoOperacao
import junit.framework.TestCase.assertEquals
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import java.time.LocalDate

/**
 * @author @jeancsanchez
 * @created 21/05/2021
 * Jesus loves you.
 */

@RunWith(MockitoJUnitRunner::class)
class LeaoServiceTest {

    @Mock
    lateinit var operacaoRepository: OperacaoRepository

    @InjectMocks
    private lateinit var leaoService: LeaoService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `Swing trade - operacoes de venda de ate R$ 20 mil no mes, nao gera imposto`() {
        val today = LocalDate.of(2021, 1, 1)
        val tomorrow = today.plusDays(1)
        whenever(operacaoRepository.findAll()).thenAnswer {
            listOf(
                // Compra: 10.000
                FakeFactory.createOperacao().copy(
                    papel = Papel(codigo = "ITSA4"),
                    quantidade = 200,
                    preco = 50.0,
                    data = today,
                    tipoDaOperacao = TipoOperacao.COMPRA
                ),

                // Venda: 20000
                FakeFactory.createOperacao().copy(
                    papel = Papel(codigo = "ITSA4"),
                    quantidade = 200,
                    preco = 100.0,
                    data = tomorrow,
                    tipoDaOperacao = TipoOperacao.VENDA
                )
            )
        }

        val impostos = leaoService.pegarImpostosNoMesComAcoesSwingTrade(today)
        assertEquals(0.0, impostos)
    }

    @Test
    @Suppress("DANGEROUS_CHARACTERS")
    fun `Swing trade - operacoes de venda acima de R$ 20 mil no mes, gera imposto de 15% sobre o lucro do mes`() {
        val today = LocalDate.of(2021, 1, 1)
        val tomorrow = today.plusDays(1)
        whenever(operacaoRepository.findAll()).thenAnswer {
            listOf(
                // Compra: 10.000
                FakeFactory.createOperacao().copy(
                    papel = Papel(codigo = "ITSA4"),
                    quantidade = 200,
                    preco = 50.0,
                    data = today,
                    tipoDaOperacao = TipoOperacao.COMPRA
                ),

                // Venda: 20.080
                FakeFactory.createOperacao().copy(
                    papel = Papel(codigo = "ITSA4"),
                    quantidade = 200,
                    preco = 100.40,
                    data = tomorrow,
                    tipoDaOperacao = TipoOperacao.VENDA
                )
            )
        }

        val impostos = leaoService.pegarImpostosNoMesComAcoesSwingTrade(today)
        assertEquals(3012.00, impostos)
    }

    @Test
    @Suppress("DANGEROUS_CHARACTERS")
    fun `Day trade - qualquer lucro no dia com acoes gera imposto de 20% sobre o lucro do mes`() {
        val today = LocalDate.of(2021, 1, 1)
        val tomorrow = today.plusDays(1)
        whenever(operacaoRepository.findAll()).thenAnswer {
            listOf(
                FakeFactory.createOperacao().copy(
                    papel = Papel(codigo = "ITSA4"),
                    quantidade = 10,
                    preco = 100.0,
                    data = today,
                    tipoDaOperacao = TipoOperacao.COMPRA
                ),
                FakeFactory.createOperacao().copy(
                    papel = Papel(codigo = "ITSA4"),
                    quantidade = 10,
                    preco = 200.0,
                    data = today,
                    tipoDaOperacao = TipoOperacao.VENDA
                ),
                FakeFactory.createOperacao().copy(
                    papel = Papel(codigo = "ITUB3"),
                    quantidade = 10,
                    preco = 100.0,
                    data = tomorrow,
                    tipoDaOperacao = TipoOperacao.COMPRA
                ),
                FakeFactory.createOperacao().copy(
                    papel = Papel(codigo = "ITUB3"),
                    quantidade = 10,
                    preco = 200.0,
                    data = tomorrow,
                    tipoDaOperacao = TipoOperacao.VENDA
                )
            )
        }

        val impostos = leaoService.pegarImpostosNoMesComAcoesDayTrade(today)
        assertEquals(399.98, impostos)
    }

    @Test
    @Suppress("DANGEROUS_CHARACTERS")
    fun `FIIs - Day Trade ou nao, gera imposto de 20% sobre o lucro do mes`() {
        val today = LocalDate.of(2021, 2, 1)
        whenever(operacaoRepository.findAll()).thenAnswer {
            listOf(
                FakeFactory.createOperacao().copy(
                    papel = Papel(codigo = "XPLG11"),
                    quantidade = 10,
                    preco = 100.0,
                    data = today.minusMonths(1),
                    tipoDaAcao = TipoAcao.FUNDO_IMOBILIARIO,
                    tipoDaOperacao = TipoOperacao.COMPRA
                ),
                FakeFactory.createOperacao().copy(
                    papel = Papel(codigo = "XPLG11"),
                    quantidade = 2,
                    preco = 200.0,
                    tipoDaAcao = TipoAcao.FUNDO_IMOBILIARIO,
                    data = today.minusMonths(1),
                    tipoDaOperacao = TipoOperacao.VENDA
                ),
                // Somente esse entra na contagem
                FakeFactory.createOperacao().copy(
                    papel = Papel(codigo = "XPLG11"),
                    quantidade = 4,
                    preco = 210.0,
                    data = today,
                    tipoDaAcao = TipoAcao.FUNDO_IMOBILIARIO,
                    tipoDaOperacao = TipoOperacao.VENDA
                ),
                FakeFactory.createOperacao().copy(
                    papel = Papel(codigo = "XPLG11"),
                    quantidade = 2,
                    preco = 230.0,
                    data = today.plusDays(30),
                    tipoDaAcao = TipoAcao.FUNDO_IMOBILIARIO,
                    tipoDaOperacao = TipoOperacao.VENDA
                ),
            )
        }

        val impostos = leaoService.pegarImpostosNoMesComFIIs(today)
        assertEquals(168.0, impostos)
    }


    @Test
    @Suppress("DANGEROUS_CHARACTERS")
    fun `Trazer lucros com FIIs do mes`() {
        val today = LocalDate.of(2021, 2, 1)
        whenever(operacaoRepository.findAll()).thenAnswer {
            listOf(
                FakeFactory.createOperacao().copy(
                    papel = Papel(codigo = "XPLG11"),
                    quantidade = 10,
                    preco = 100.0,
                    data = today.minusMonths(1),
                    tipoDaAcao = TipoAcao.FUNDO_IMOBILIARIO,
                    tipoDaOperacao = TipoOperacao.COMPRA
                ),
                FakeFactory.createOperacao().copy(
                    papel = Papel(codigo = "XPLG11"),
                    quantidade = 2,
                    preco = 200.0,
                    data = today.minusMonths(1),
                    tipoDaAcao = TipoAcao.FUNDO_IMOBILIARIO,
                    tipoDaOperacao = TipoOperacao.VENDA
                ),
                // Somente esse entra na contagem
                FakeFactory.createOperacao().copy(
                    papel = Papel(codigo = "XPLG11"),
                    quantidade = 4,
                    preco = 210.0,
                    data = today,
                    tipoDaAcao = TipoAcao.FUNDO_IMOBILIARIO,
                    tipoDaOperacao = TipoOperacao.VENDA
                ),
                FakeFactory.createOperacao().copy(
                    papel = Papel(codigo = "XPLG11"),
                    quantidade = 2,
                    preco = 230.0,
                    data = today.plusDays(30),
                    tipoDaAcao = TipoAcao.FUNDO_IMOBILIARIO,
                    tipoDaOperacao = TipoOperacao.VENDA
                ),
            )
        }

        val lucroLiquido = leaoService.pegarLucroLiquidoNoMesComFIIs(today)
        assertThat(lucroLiquido, equalTo(840.0))
    }

    @Test
    fun `Swing trade - Imposto a recolher menor que 10 reais nao precisa gerar Darf, mas acumula`() {

    }

    @Test
    fun `Swing trade - Imposto acumulado igual ou maior que 10 reais gera Darf`() {

    }
}