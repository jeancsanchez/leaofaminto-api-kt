package com.github.jeancsanchez.leaofaminto.domain.service

import com.github.jeancsanchez.leaofaminto.data.ComprasRepository
import com.github.jeancsanchez.leaofaminto.data.OperacaoRepository
import com.github.jeancsanchez.leaofaminto.data.VendasRepository
import com.github.jeancsanchez.leaofaminto.domain.FakeFactory
import com.github.jeancsanchez.leaofaminto.domain.GerarOperacoesConsolidadasService
import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

/**
 * @author @jeancsanchez
 * @created 21/05/2021
 * Jesus loves you.
 */

@RunWith(MockitoJUnitRunner::class)
internal class GerarOperacoesConsolidadasServiceTest {

    @Mock
    lateinit var operacaoRepository: OperacaoRepository

    @Mock
    lateinit var comprasRepository: ComprasRepository

    @Mock
    lateinit var vendasRepository: VendasRepository

    @InjectMocks
    private lateinit var relatorioService: GerarOperacoesConsolidadasService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun gerarOperacoesConsolidadas() {
        whenever(comprasRepository.findAll()).thenAnswer {
            FakeFactory.getCompras()
        }

        whenever(vendasRepository.findAllByAtivoCodigo(any())).thenAnswer { invocation ->
            FakeFactory.getVendas().filter { it.ativo.codigo == invocation.arguments.first() }
        }

        val result = relatorioService.execute(Unit)

        // Deve retornar apenas um item consolidando as operações
        assertEquals(2, result.items.size)

        val itemsSimpar = result.items.filter { it.codigoAtivo == "SIMH3" }
        val itemsCeA = result.items.filter { it.codigoAtivo == "CEAB3" }

        // Deve retornar a quantidade de ações considerando as operações
        assertEquals(20.0, itemsSimpar.sumByDouble { it.quantidadeTotal })

        // Deve retornar a quantidade de ações considerando as operações
        assertEquals(50.0, itemsCeA.sumByDouble { it.quantidadeTotal })

        // Deve retornar o preço médio das ações
        assertEquals(18.50, itemsSimpar.sumByDouble { it.precoMedio })

        // Deve retornar o preço médio das ações
        assertEquals(12.88, itemsCeA.sumByDouble { it.precoMedio })

        // Deve retornar o total considerando as operações
        assertEquals(1013.78, result.totalInvestido)
    }

    @Test
    fun quandoSIMH3OuJSLG3ConsiderarOMesmoPapel() {
        val comprasList = FakeFactory.getCompras().filter { it.ativo.codigo == "SIMH3" || it.ativo.codigo == "JSLG3" }
        val vendasList = FakeFactory.getVendas().filter { it.ativo.codigo == "SIMH3" || it.ativo.codigo == "JSLG3" }
        whenever(comprasRepository.findAll()).thenReturn(comprasList)
        whenever(vendasRepository.findAll()).thenReturn(vendasList)
        whenever(comprasRepository.findAllByAtivoCodigo(anyString())).thenReturn(comprasList)
        whenever(vendasRepository.findAllByAtivoCodigo(anyString())).thenReturn(vendasList)

        val result = relatorioService.execute(Unit)

        // Deve retornar apenas um item consolidando as operações
        assertEquals(1, result.items.size)

        // Deve retornar a quantidade de ações considerando as operações
        assertEquals(20.0, result.items.first().quantidadeTotal)

        // Deve retornar o preço médio das ações
        assertEquals(18.50, result.items.first().precoMedio)

        // Deve retornar o total considerando as operações
        assertEquals(370.0, result.totalInvestido)
    }
}