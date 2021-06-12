package com.github.jeancsanchez.investments.domain

import com.github.jeancsanchez.investments.data.ComprasRepository
import com.github.jeancsanchez.investments.data.VendasRepository
import com.github.jeancsanchez.investments.domain.model.LeaoService
import com.github.jeancsanchez.investments.domain.model.Papel
import com.github.jeancsanchez.investments.domain.model.TipoAcao
import com.github.jeancsanchez.investments.domain.model.TipoOperacao
import com.github.jeancsanchez.investments.domain.novos.*
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
    lateinit var comprasRepository: ComprasRepository

    @Mock
    lateinit var vendasRepository: VendasRepository

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

        whenever(comprasRepository.findAll()).thenAnswer {
            listOf(
                // Compra: 10.000
                Compra(
                    ativo = Ativo(codigo = "ITSA4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 200,
                    preco = 50.0,
                    data = today,
                ),
            )
        }

        whenever(vendasRepository.findAll()).thenAnswer {
            listOf(
                // Venda: 20000
                Venda(
                    ativo = Ativo(codigo = "ITSA4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 200,
                    preco = 100.0,
                    data = tomorrow,
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
        whenever(comprasRepository.findAll()).thenAnswer {
            listOf(
                // Compra: 10.000
                Compra(
                    ativo = Ativo(codigo = "ITSA4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 200,
                    preco = 50.0,
                    data = today
                ),
            )
        }

        whenever(vendasRepository.findAll()).thenAnswer {
            listOf(
                // Venda: 20.080
                Venda(
                    ativo = Ativo(codigo = "ITSA4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 200,
                    preco = 100.40,
                    data = tomorrow
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
        whenever(comprasRepository.findAll()).thenAnswer {
            listOf(
                Compra(
                    ativo = Ativo(codigo = "ITSA4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 10,
                    preco = 100.0,
                    data = today,
                ),
                Compra(
                    ativo = Ativo(codigo = "ITUB3", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 10,
                    preco = 100.0,
                    data = tomorrow,
                ),
            )
        }

        whenever(vendasRepository.findAll()).thenAnswer {
            listOf(
                Venda(
                    ativo = Ativo(codigo = "ITSA4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 10,
                    preco = 200.0,
                    data = today
                ),
                Venda(
                    ativo = Ativo(codigo = "ITUB3", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 10,
                    preco = 200.0,
                    data = tomorrow,
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
        whenever(comprasRepository.findAll()).thenAnswer {
            listOf(
                Compra(
                    ativo = Ativo(codigo = "XPLG11", tipoDeAtivo = TipoDeAtivo.FII),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 10,
                    preco = 100.0,
                    data = today.minusMonths(1),
                ),
            )
        }

        whenever(vendasRepository.findAll()).thenAnswer {
            listOf(
                Venda(
                    ativo = Ativo(codigo = "XPLG11", tipoDeAtivo = TipoDeAtivo.FII),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 2,
                    preco = 200.0,
                    data = today.minusMonths(1),
                ),
                // Somente esse entra na contagem
                Venda(
                    ativo = Ativo(codigo = "XPLG11", tipoDeAtivo = TipoDeAtivo.FII),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 4,
                    preco = 210.0,
                    data = today,
                ),
                Venda(
                    ativo = Ativo(codigo = "XPLG11", tipoDeAtivo = TipoDeAtivo.FII),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 2,
                    preco = 230.0,
                    data = today.plusDays(30),
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
        whenever(comprasRepository.findAll()).thenAnswer {
            listOf(
                Compra(
                    ativo = Ativo(codigo = "XPLG11", tipoDeAtivo = TipoDeAtivo.FII),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 10,
                    preco = 100.0,
                    data = today.minusMonths(1),
                )
            )
        }

        whenever(vendasRepository.findAll()).thenAnswer {
            listOf(
                Venda(
                    ativo = Ativo(codigo = "XPLG11", tipoDeAtivo = TipoDeAtivo.FII),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 2,
                    preco = 200.0,
                    data = today.minusMonths(1)
                ),
                // Somente esse entra na contagem
                Venda(
                    ativo = Ativo(codigo = "XPLG11", tipoDeAtivo = TipoDeAtivo.FII),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 4,
                    preco = 210.0,
                    data = today,
                ),
                Venda(
                    ativo = Ativo(codigo = "XPLG11", tipoDeAtivo = TipoDeAtivo.FII),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 2,
                    preco = 230.0,
                    data = today.plusDays(30)
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