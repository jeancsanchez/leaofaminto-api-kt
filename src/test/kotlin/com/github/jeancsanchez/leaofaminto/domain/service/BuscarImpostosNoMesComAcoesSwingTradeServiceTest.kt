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

@Suppress("DANGEROUS_CHARACTERS")
internal class BuscarImpostosNoMesComAcoesSwingTradeServiceTest {

    @Mock
    lateinit var comprasRepository: ComprasRepository

    @Mock
    lateinit var vendasRepository: VendasRepository

    @Mock
    lateinit var impostoRepository: ImpostoRepository

    @InjectMocks
    private lateinit var buscarImpostosNoMesComAcoesSwingTradeService: BuscarImpostosNoMesComAcoesSwingTradeService

    private val governo = mock<Governo>()
    private val bolsa = mock<Bolsa>().also { it.governo = governo }
    private val corretora = mock<Corretora>().also { it.bolsa = bolsa }

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        whenever(impostoRepository.save(any<Imposto>())).thenAnswer { it.arguments.first() }
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
    fun `Swing trade (Caso 1) - operacoes de venda acima de R$ 20 mil no mes, geram imposto de 15% sobre o lucro do mes`() {
        val today = LocalDate.of(2021, 1, 1)
        val someDaysAfter = today.plusDays(15)
        whenever(governo.recolherDedoDuroSwingTrade(any())).thenAnswer { 2.25 }
        whenever(governo.taxarLucroSwingTrade(any())).thenAnswer { 375.0 }
        whenever(bolsa.taxarLucroDayTrade(any())).thenAnswer { 0.0 }
        whenever(corretora.taxarLucroSwingTrade(any())).thenAnswer { 0.0 }
        whenever(comprasRepository.findAllByAtivoCodigo(any())).thenAnswer {
            listOf(
                // Compra: 42.500
                Compra(
                    ativo = Ativo(codigo = "BPAC11", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = corretora,
                    quantidade = 500,
                    preco = 85.0,
                    data = today,
                ),
            )
        }

        whenever(vendasRepository.findAll()).thenAnswer {
            listOf(
                // Venda: 45.000
                Venda(
                    ativo = Ativo(codigo = "BPAC11", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = corretora,
                    quantidade = 500,
                    preco = 90.0,
                    data = someDaysAfter,
                )
            )
        }

        val impostos = buscarImpostosNoMesComAcoesSwingTradeService.execute(today)
        TestCase.assertEquals(1, impostos.impostos?.size)
        TestCase.assertEquals(372.75, impostos.total)
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
                    corretora = ClearCorretora(),
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
                    corretora = ClearCorretora(),
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
                    corretora = ClearCorretora(),
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
                    corretora = ClearCorretora(),
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