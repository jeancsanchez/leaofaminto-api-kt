package com.github.jeancsanchez.investments.domain

import com.github.jeancsanchez.investments.data.ComprasRepository
import com.github.jeancsanchez.investments.data.OperacaoRepository
import com.github.jeancsanchez.investments.data.VendasRepository
import com.github.jeancsanchez.investments.domain.model.*
import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
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
        whenever(comprasRepository.findAllByAtivoCodigo(any())).thenAnswer { invocation ->
            listOf(
                // Compra: 10.000
                Compra(
                    ativo = Ativo(codigo = "ITSA4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 200,
                    preco = 50.0,
                    data = today
                ),
            ).filter { it.ativo.codigo == invocation.arguments.first() }
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
    fun `Swing trade - Imposto a recolher menor que 10 reais nao precisa gerar Darf, mas acumula`() {

    }

    @Test
    fun `Swing trade - Imposto acumulado igual ou maior que 10 reais gera Darf`() {

    }
}