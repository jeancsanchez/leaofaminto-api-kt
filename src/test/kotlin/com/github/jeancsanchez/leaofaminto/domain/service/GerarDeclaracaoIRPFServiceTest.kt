package com.github.jeancsanchez.leaofaminto.domain.service

import com.github.jeancsanchez.leaofaminto.data.AtivoRepository
import com.github.jeancsanchez.leaofaminto.data.ComprasRepository
import com.github.jeancsanchez.leaofaminto.data.VendasRepository
import com.github.jeancsanchez.leaofaminto.domain.GerarDeclaracaoIRPFService
import com.github.jeancsanchez.leaofaminto.domain.GerarOperacoesConsolidadasService
import com.github.jeancsanchez.leaofaminto.domain.model.Ativo
import com.github.jeancsanchez.leaofaminto.domain.model.Compra
import com.github.jeancsanchez.leaofaminto.domain.model.Venda
import com.github.jeancsanchez.leaofaminto.domain.model.corretoras.ClearCorretora
import com.github.jeancsanchez.leaofaminto.view.dto.ConsolidadoDTO
import com.github.jeancsanchez.leaofaminto.view.dto.OperacaoConsolidadaDTO
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
 * @created 22/01/2022
 * Jesus loves you.
 */

@RunWith(MockitoJUnitRunner::class)
internal class GerarDeclaracaoIRPFServiceTest {

    @Mock
    lateinit var comprasRepository: ComprasRepository

    @Mock
    lateinit var vendasRepository: VendasRepository

    @Mock
    lateinit var ativoRepository: AtivoRepository

    @Mock
    lateinit var gerarOperacoesConsolidadasService: GerarOperacoesConsolidadasService


    @InjectMocks
    private lateinit var relatorioService: GerarDeclaracaoIRPFService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    /**
     * Nesse caso, foram compradas 20 ações de C&A ao valor de R$ 10,00 no ano de 2020. No mesmo ano,
     * 10 ações foram vendidas ao mesmo preço, somando 100 ações ao preço médio de R$ 10,00 no ano de 2020.
     * No ano de 2021, foram compradas mais 10 ações a R$ 10,00, totalizando 200 ações a R$ 10,00
     * em 2021.
     */
    @Test
    fun gerarDeclaracaoIRPF() {
        val lastYear = LocalDate.of(2020, 1, 1)
        val currentYear = LocalDate.of(2021, 1, 1)
        val dummyAtivo = Ativo(codigo = "CEAB3", nome = "C&A", cnpj = "010101/01")
        val comprasList = listOf(
            Compra(
                ativo = dummyAtivo,
                quantidade = 10.0,
                preco = 10.0,
                corretora = ClearCorretora(),
                data = lastYear,
            ),
            Compra(
                ativo = dummyAtivo,
                quantidade = 10.0,
                preco = 10.0,
                corretora = ClearCorretora(),
                data = lastYear.plusMonths(2)
            ),
            Compra(
                ativo = dummyAtivo,
                quantidade = 10.0,
                preco = 10.0,
                corretora = ClearCorretora(),
                data = currentYear
            ),
        )

        val vendasList = listOf(
            Venda(
                ativo = dummyAtivo,
                quantidade = 10.0,
                preco = 10.0,
                corretora = ClearCorretora(),
                data = lastYear.plusMonths(4)
            )
        )

        whenever(comprasRepository.findAll()).thenAnswer { comprasList }
        whenever(vendasRepository.findAllByAtivoCodigo(any())).thenAnswer { vendasList }
        whenever(comprasRepository.findTopByAtivoCodigoOrderByDataDesc(any())).thenAnswer { comprasList.first() }
        whenever(gerarOperacoesConsolidadasService.execute(Unit)).thenAnswer {
            ConsolidadoDTO(
                items = listOf(
                    OperacaoConsolidadaDTO(
                        ativo = dummyAtivo,
                        quantidadeTotal = 200.0,
                        precoMedio = 10.0,
                        totalInvestido = 200.0
                    )
                ),
                totalInvestido = 200.0
            )
        }

        // When
        val result = relatorioService.execute(currentYear.year)

        // Then
        result.bensEDireitos
            .first()
            .also {
                assertEquals("Bens e Direitos", it.titulo)
                assertEquals("31", it.codigo)
                assertEquals("Brasil", it.localizacao)
                assertEquals(dummyAtivo.cnpj, it.cnpj)

                assertEquals(
                    "200 AÇÕES DE C&A (CEAB3) AO CUSTO MÉDIO DE R$ 10,00" +
                            " CUSTODIADA NA CORRETORA CLEAR, CNPJ: 02.332.886/0011-78",
                    it.discriminacao
                )

                assertEquals("R$ 100,00", it.situacaoAnterior)
                assertEquals("R$ 200,00", it.situacaoAtual)
            }
    }

    /**
     * Nesse caso, foram compradas 20 ações de C&A ao valor de R$ 10,00 no ano de 2020. No mesmo ano,
     * 10 ações foram vendidas ao mesmo preço, somando 100 ações ao preço médio de R$ 10,00 no ano de 2020.
     * No ano de 2021, foram compradas mais 10 ações a R$ 10,00, totalizando 200 ações a R$ 10,00
     * em 2021.
     */
    @Test
    fun pegarPosicaoAtivoAnoAnteriorEAnoCorrente() {
        val lastYear = LocalDate.of(2020, 1, 1)
        val currentYear = LocalDate.of(2021, 1, 1)
        val dummyAtivo = Ativo(codigo = "CEAB3", cnpj = "010101/01")

        whenever(comprasRepository.findAll()).thenAnswer {
            listOf(
                Compra(
                    ativo = dummyAtivo,
                    quantidade = 10.0,
                    preco = 10.0,
                    corretora = ClearCorretora(),
                    data = lastYear,
                ),
                Compra(
                    ativo = dummyAtivo,
                    quantidade = 10.0,
                    preco = 10.0,
                    corretora = ClearCorretora(),
                    data = lastYear.plusMonths(2)
                ),
                Compra(
                    ativo = dummyAtivo,
                    quantidade = 10.0,
                    preco = 10.0,
                    corretora = ClearCorretora(),
                    data = currentYear
                ),
            )
        }

        whenever(vendasRepository.findAllByAtivoCodigo(any())).thenAnswer { invocation ->
            listOf(
                Venda(
                    ativo = dummyAtivo,
                    quantidade = 10.0,
                    preco = 10.0,
                    corretora = ClearCorretora(),
                    data = lastYear.plusMonths(4)
                )
            )
        }

        val result = relatorioService.getLastAndCurrentPosition(currentYear.year)

        result
            .first()
            .also {
                assertEquals("CEAB3", it.ativo.codigo)
                assertEquals("R$ 100,00", it.lastPosition)
                assertEquals("R$ 200,00", it.currentPosition)
            }
    }
}