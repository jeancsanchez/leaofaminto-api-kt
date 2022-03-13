package com.github.jeancsanchez.leaofaminto.domain.service

import com.github.jeancsanchez.leaofaminto.data.AtivoRepository
import com.github.jeancsanchez.leaofaminto.data.ComprasRepository
import com.github.jeancsanchez.leaofaminto.data.VendasRepository
import com.github.jeancsanchez.leaofaminto.domain.GerarDeclaracaoIRPFService
import com.github.jeancsanchez.leaofaminto.domain.GerarOperacoesConsolidadasService
import com.github.jeancsanchez.leaofaminto.domain.model.Ativo
import com.github.jeancsanchez.leaofaminto.domain.service.scenarios.DomainScenarios
import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
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

    @InjectMocks
    private lateinit var scenarios: DomainScenarios

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
        val reportYear = 2022
        val dummyAtivo = Ativo(codigo = "CEAB3", nome = "C&A", cnpj = "010101/01")
        scenarios.loadGerarDeclaracaoIRPFScenario(
            dummyAtivo = dummyAtivo,
            reportYear = LocalDate.of(reportYear, 1, 1),
        )

        // When
        val result = relatorioService.execute(reportYear)

        // Then
        result.bensEDireitos
            .also { secao ->
                assertEquals("Bens e Direitos", secao.titulo)
                assertEquals("31", secao.codigo)

                secao.data
                    .first()
                    .also {
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
    }

    /**
     * Nesse caso, ações da Ambev foram compradas nos anos de 2019, 2020 e 2021. Ao gerar a declaração de 2022,
     * a posição anterior deve considerar todos os anos anteriores a 2022.
     * Nesse cenário, foram compradas 306 ações ao custo de R$4,508.98. Foram vendidas também, 100 ações
     * ao preço de -R$1,145.00, restando a posição final de 206 ações ao custo de R$ R$3,363.98 e preço médio
     * de R$ 16.33.
     */
    @Test
    fun gerarDeclaracaoIRPFSomandoTodosOsAnosAnteriores() {
        val ambev = Ativo(nome = "Ambev", codigo = "ABEV3", cnpj = "010101/01")
        scenarios.loadGerarDeclaracaoIRPFSomandoTodosOsAnosAnterioresScenario(ambev)

        // When
        val result = relatorioService.execute(2022)

        // Then
        result.bensEDireitos.data
            .first()
            .also {
                assertEquals("R$ 1280,68", it.situacaoAnterior)
                assertEquals("R$ 3363,98", it.situacaoAtual)

                assertEquals(
                    "206 AÇÕES DE AMBEV (ABEV3) AO CUSTO MÉDIO DE R$ 16,33" +
                            " CUSTODIADA NA CORRETORA CLEAR, CNPJ: 02.332.886/0011-78",
                    it.discriminacao
                )
            }
    }


    /**
     * Nesse caso, ações da RAIZ4 NÃO foram compradas no de 2020, mas foram compradas 3 ações no ano de 2021
     * ao preço médio de R$ 6,41.
     */
    @Test
    fun gerarDeclaracaoIRPFConsiderandoNovasCompras() {
        val raizen = Ativo(nome = "Raizen SA", codigo = "RAIZ4", cnpj = "010101/01")
        scenarios.loadGerarDeclaracaoIRPFConsiderandoNovasComprasScenario(raizen)

        // When
        val result = relatorioService.execute(2022)

        // Then
        result.bensEDireitos.data
            .first()
            .also {
                assertEquals("R$ 0,00", it.situacaoAnterior)
                assertEquals("R$ 19,23", it.situacaoAtual)

                assertEquals(
                    "3 AÇÕES DE RAIZEN SA (RAIZ4) AO CUSTO MÉDIO DE R$ 6,41" +
                            " CUSTODIADA NA CORRETORA CLEAR, CNPJ: 02.332.886/0011-78",
                    it.discriminacao
                )
            }
    }

    /**
     * Nesse caso, 6 ações da LINX3 FORAM compradas no de 2020 ao preço médio de R$ 36,80
     * e 6 ações foram vendidas no ano de 2021 ao preço médio de R$ 33,52.
     * Logo, a posição anterior deve ser de R$ 220,80 e posição atual R$ 0,00.
     */
    @Test
    fun gerarDeclaracaoIRPFConsiderandoVendasNosAnosSeguintes() {
        val linx = Ativo(nome = "Linx", codigo = "LINX3", cnpj = "010101/01")
        scenarios.loadGerarDeclaracaoIRPFConsiderandoVendasNosAnosSeguintesScenario(linx)

        // When
        val result = relatorioService.execute(2022)

        // Then
        result.bensEDireitos.data
            .first()
            .also {
                assertEquals("R$ 220,8", it.situacaoAnterior)
                assertEquals("R$ 0,00", it.situacaoAtual)

                assertEquals(
                    "0 AÇÃO DE LINX (LINX3) AO CUSTO MÉDIO DE R$ 0,00" +
                            " CUSTODIADA NA CORRETORA CLEAR, CNPJ: 02.332.886/0011-78",
                    it.discriminacao
                )
            }
    }

    /**
     * Nesse caso, 3 ações da WEGE3 FORAM compradas no ano de 2019 ao preço médio de R$ 10,00
     * e nos anos seguintes nenhuma outra operação foi feita.
     * Logo, a posição anterior deve ser de R$ 30,00 e posição atual R$ 30,00.
     */
    @Test
    fun gerarDeclaracaoIRPFConsiderandoNenhumaOperacaoNosAnosSeguintes() {
        val wege = Ativo(nome = "WEGE", codigo = "WEGE3", cnpj = "010101/01")
        scenarios.loadGerarDeclaracaoIRPFConsiderandoNenhumaOperacaoNosAnosSeguintesScenario(wege)

        // When
        val result = relatorioService.execute(2022)

        // Then
        result.bensEDireitos.data
            .first()
            .also {
                assertEquals("R$ 30,00", it.situacaoAnterior)
                assertEquals("R$ 30,00", it.situacaoAtual)

                assertEquals(
                    "3 AÇÕES DE WEGE (WEGE3) AO CUSTO MÉDIO DE R$ 10,00" +
                            " CUSTODIADA NA CORRETORA CLEAR, CNPJ: 02.332.886/0011-78",
                    it.discriminacao
                )
            }
    }

}