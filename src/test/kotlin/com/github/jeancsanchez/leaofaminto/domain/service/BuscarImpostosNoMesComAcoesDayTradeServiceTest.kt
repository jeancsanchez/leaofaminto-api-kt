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

    // Mesma corretora: vender BAST3 na corretora A e no mesmo dia comprar BAST3 na corretora B não é day trade.

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
    fun `Day trade - qualquer lucro no dia com acoes gera imposto de 20% sobre o lucro do mes`() {
        val today = LocalDate.of(2021, 1, 1)
        val governo = mock<Governo>()
        val bolsa = mock<Bolsa>()
        val corretora = mock<Corretora>()

        whenever(governo.taxarOperacao(any())).thenAnswer { 9.98 }
        whenever(bolsa.taxarOperacao(any())).thenAnswer { 0.10 }
        whenever(corretora.taxarOperacao(any())).thenAnswer { 1.0 }
        whenever(operacaoRepository.findAll()).thenAnswer {
            listOf(
                Compra(
                    ativo = Ativo(codigo = "PETR4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = corretora,
                    quantidade = 10000,
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
}