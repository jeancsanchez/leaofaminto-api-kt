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
    private val bolsa = mock<Bolsa>()
    private val corretora = mock<Corretora>()

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        whenever(bolsa.governo).thenAnswer { governo }
        whenever(corretora.bolsa).thenAnswer { bolsa }
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
//        whenever(governo.recolherDedoDuroSwingTrade(any())).thenAnswer { 2.25 }
//        whenever(governo.taxarLucroSwingTrade(any())).thenAnswer { 375.0 }
//        whenever(bolsa.taxarLucroDayTrade(any())).thenAnswer { 0.0 }
//        whenever(corretora.taxarLucroSwingTrade(any())).thenAnswer { 0.0 }
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

    /**
     * Exemplo retirado de: https://bastter.com/mercado/forum/794001
     * 500 BAST3 R$ 5.938,71
     * 505 ITSA4 R$ 5.983,80
     * 100 MQFO11 R$ 4.326,78
     * 800 BBDC3 R$ 25.248,89
     * 1.000 PETR4 R$ 9.673,28
     * Os custos serão de 0,2825% do total das operações
     *
     * b) VENDA PARCIAL DA POSIÇÃO EM UMA ÚNICA OPERAÇÃO
     * Aqui vendemos parte da posição (205 ações) e precisamos calcular o custo de aquisição das ações vendidas.
     * Uma regra de três resolve o problema:
     * 505 ITSA3  adquiridas por R$ 5.983,80
     * 205 ITSA3 adquiridas por R$ 2.429,06 desprezando a partir da terceira decimal assim como a Receita costuma fazer.
     * Venda 205 ITSA4 R$ 14,00/ação
     * 205 x R$ 14,00 = R$ 2.870,00 x 0,2825% = R$ 8,10
     * Venda R$ 2.870,00 - R$ 8,10 = R$ 2.861,90
     * Vamos agora calcular o resultado da operação:
     * Venda R$ 2.861,90 - compra R$ 2.429,06 = R$ 432,84
     * Venda menor que R$ 20 mil, lucro isento de IR. Informar o valor na ficha Rendimentos isentos e não tributáveis na linha 20.
     */
    @Test
    @Suppress("DANGEROUS_CHARACTERS")
    fun `Swing trade (Caso 2) - operacoes de venda de ate R$ 20 mil no mes, nao geram imposto`() {
        val today = LocalDate.of(2021, 1, 1)
        val someDaysAfter = today.plusDays(15)
//        whenever(governo.recolherDedoDuroSwingTrade(any())).thenAnswer { 0.0 }
//        whenever(governo.taxarLucroSwingTrade(any())).thenAnswer { 0.0 }
//        whenever(bolsa.taxarLucroDayTrade(any())).thenAnswer { 0.0 }
//        whenever(corretora.taxarLucroSwingTrade(any())).thenAnswer { 8.10 }
        whenever(comprasRepository.findAllByAtivoCodigo(any())).thenAnswer { invocation ->
            listOf(
                Compra(
                    ativo = Ativo(codigo = "BAST3", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = corretora,
                    quantidade = 500,
                    preco = 11.87,
                    data = today
                ),
                Compra(
                    ativo = Ativo(codigo = "ITSA4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = corretora,
                    quantidade = 505,
                    preco = 11.84,
                    data = today
                ),
                Compra(
                    ativo = Ativo(codigo = "MQFO11", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = corretora,
                    quantidade = 100,
                    preco = 43.26,
                    data = today
                ),
                Compra(
                    ativo = Ativo(codigo = "BBDC3", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = corretora,
                    quantidade = 800,
                    preco = 31.56,
                    data = today
                ),
                Compra(
                    ativo = Ativo(codigo = "PETR4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = corretora,
                    quantidade = 1000,
                    preco = 9.67,
                    data = today
                ),
                // Venda = R$ 2870
                // Lucro = R$ 432,84
                Venda(
                    ativo = Ativo(codigo = "ITSA4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = corretora,
                    quantidade = 205,
                    preco = 14.0,
                    data = someDaysAfter
                ),
            ).filter { it.ativo.codigo == invocation.arguments.first() }
        }

        val impostos = buscarImpostosNoMesComAcoesSwingTradeService.execute(today)
        TestCase.assertEquals(0.0, impostos.total)
    }

    /**
     * Nesse caso não há impostos remanescentes, porém há um imposto devido de R$ 1,20
     * sobre o lucro do mês atual (R$ 8,00).
     * Como o valor é menor que R$ 10,00, apenas salva o valor.
     */
    @Test
    fun `Swing trade - Imposto a recolher menor que 10 reais nao precisa gerar Imposto, mas acumula o valor`() {
        val today = LocalDate.of(2021, 1, 1)
        val tomorrow = today.plusDays(1)
//        whenever(governo.recolherDedoDuroSwingTrade(any())).thenAnswer { 0.0004 }
//        whenever(governo.taxarLucroSwingTrade(any())).thenAnswer { 1.20 }
//        whenever(bolsa.taxarLucroDayTrade(any())).thenAnswer { 0.0 }
//        whenever(corretora.taxarLucroSwingTrade(any())).thenAnswer { 0.0 }
        whenever(impostoRepository.findTop1ByDataReferenciaAndValor(any(), any())).thenAnswer { null }
        whenever(impostoRepository.findAllByEstaPago(any())).thenAnswer { emptyList<Imposto>() }
        whenever(comprasRepository.findAllByAtivoCodigo(any())).thenAnswer { invocation ->
            listOf(
                // Compra: 20000
                Compra(
                    ativo = Ativo(codigo = "ITSA4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = corretora,
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
                    corretora = corretora,
                    quantidade = 100,
                    preco = 200.08,
                    data = tomorrow
                )
            )
        }

        val impostos = buscarImpostosNoMesComAcoesSwingTradeService.execute(today)
        verify(impostoRepository).save(argThat {
            operacoes.isNotEmpty()
                    && valor == 1.20
                    && estaPago == false
        })

        TestCase.assertTrue(impostos.impostos?.isEmpty() == true)
        TestCase.assertEquals(0.0, impostos.total)
    }

    /**
     * Nesse caso há dois impostos remanescentes no valor de R$ 5,00 e o imposto devido de R$ 1,20
     * sobre o lucro do mês atual (R$ 8,00).
     * Total de impostos devidos = R$ 11,20
     */
    @Test
    fun `Swing trade - Imposto acumulado igual ou maior que 10 reais gera Darf`() {
        val today = LocalDate.of(2021, 1, 1)
        val tomorrow = today.plusDays(1)
//        whenever(governo.recolherDedoDuroSwingTrade(any())).thenAnswer { 0.0004 }
//        whenever(governo.taxarLucroSwingTrade(any())).thenAnswer { 1.20 }
//        whenever(bolsa.taxarLucroDayTrade(any())).thenAnswer { 0.0 }
//        whenever(corretora.taxarLucroSwingTrade(any())).thenAnswer { 0.0 }
        whenever(impostoRepository.findTop1ByDataReferenciaAndValor(any(), any())).thenAnswer { null }
        whenever(impostoRepository.findAllByEstaPago(any())).thenAnswer { invocation ->
            listOf(
                Imposto(
                    dataReferencia = today.minusMonths(3),
                    estaPago = false,
                    valor = 5.0
                ),
                Imposto(
                    dataReferencia = today.minusYears(1),
                    estaPago = false,
                    valor = 5.0
                ),
                Imposto(
                    dataReferencia = today.minusYears(2),
                    estaPago = true,
                    valor = 5.0
                )
            ).filter { it.estaPago == invocation.arguments.first() }
        }

        whenever(comprasRepository.findAllByAtivoCodigo(any())).thenAnswer { invocation ->
            listOf(
                // Compra: 20000
                Compra(
                    ativo = Ativo(codigo = "ITSA4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = corretora,
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
                    corretora = corretora,
                    quantidade = 100,
                    preco = 200.08,
                    data = tomorrow
                )
            )
        }

        val impostos = buscarImpostosNoMesComAcoesSwingTradeService.execute(today)
        verify(impostoRepository).findAllByEstaPago(eq(false))

        TestCase.assertTrue(impostos.impostos?.isNotEmpty() == true)
        TestCase.assertEquals(11.20, impostos.total)
    }
}