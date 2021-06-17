package com.github.jeancsanchez.leaofaminto.domain.service

import com.github.jeancsanchez.leaofaminto.data.ComprasRepository
import com.github.jeancsanchez.leaofaminto.data.ImpostoRepository
import com.github.jeancsanchez.leaofaminto.data.VendasRepository
import com.github.jeancsanchez.leaofaminto.domain.BuscarImpostosNoMesComAcoesSwingTradeService
import com.github.jeancsanchez.leaofaminto.domain.model.*
import junit.framework.TestCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import java.time.LocalDate

/**
 * @author @jeancsanchez
 * @created 15/06/2021
 * Jesus loves you.
 */

internal class BuscarImpostosNoMesComAcoesSwingTradeServiceTest {

    @Mock
    lateinit var comprasRepository: ComprasRepository

    @Mock
    lateinit var vendasRepository: VendasRepository

    @Mock
    lateinit var impostoRepository: ImpostoRepository

    @InjectMocks
    private lateinit var buscarImpostosNoMesComAcoesSwingTradeService: BuscarImpostosNoMesComAcoesSwingTradeService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        whenever(impostoRepository.save(any<Imposto>())).thenAnswer { it.arguments.first() }
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

        val impostos = buscarImpostosNoMesComAcoesSwingTradeService.execute(today)
        TestCase.assertEquals(0, impostos.impostos?.size)
        TestCase.assertEquals(0.0, impostos.total)
    }

    @Test
    @Suppress("DANGEROUS_CHARACTERS")
    fun `Swing trade - operacoes de venda acima de R$ 20 mil no mes, gera imposto de 15% sobre o lucro do mes`() {
        val today = LocalDate.of(2021, 1, 1)
        val tomorrow = today.plusDays(1)
        whenever(comprasRepository.findAllByAtivoCodigo(any())).thenAnswer { invocation ->
            listOf(
                // Compra: 20000
                Compra(
                    ativo = Ativo(codigo = "ITSA4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 200,
                    preco = 100.0,
                    data = today
                ),
            ).filter { it.ativo.codigo == invocation.arguments.first() }
        }

        whenever(vendasRepository.findAll()).thenAnswer {
            listOf(
                // Venda: 20.080 - Lucro de R$ 80,00
                Venda(
                    ativo = Ativo(codigo = "ITSA4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 200,
                    preco = 100.40,
                    data = tomorrow
                )
            )
        }

        val impostos = buscarImpostosNoMesComAcoesSwingTradeService.execute(today)
        TestCase.assertEquals(12.0, impostos.total)
    }

    @Test
    fun `Swing trade - Imposto a recolher menor que 10 reais nao precisa gerar Imposto, mas acumula o valor`() {
        val today = LocalDate.of(2021, 1, 1)
        val tomorrow = today.plusDays(1)
        whenever(impostoRepository.findTop1ByDataReferenciaAndValor(any(), any())).thenAnswer {
            null
        }

        whenever(comprasRepository.findAllByAtivoCodigo(any())).thenAnswer { invocation ->
            listOf(
                // Compra: 20000
                Compra(
                    ativo = Ativo(codigo = "ITSA4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 100,
                    preco = 200.0,
                    data = today
                ),
            ).filter { it.ativo.codigo == invocation.arguments.first() }
        }

        whenever(vendasRepository.findAll()).thenAnswer {
            listOf(
                // Venda: 20008 - Lucro de R$ 8,00
                Venda(
                    ativo = Ativo(codigo = "ITSA4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 100,
                    preco = 200.08,
                    data = tomorrow
                )
            )
        }

        val impostos = buscarImpostosNoMesComAcoesSwingTradeService.execute(today)
        verify(impostoRepository, atLeast(1)).save(argThat {
            operacoes.isNotEmpty()
                    && valor == 1.2
                    && estaPago == false
        })

        TestCase.assertEquals(0, impostos.impostos?.size)
        TestCase.assertEquals(0.0, impostos.total)
    }

    @Test
    fun `Swing trade - Imposto acumulado igual ou maior que 10 reais gera Darf`() {

    }
}