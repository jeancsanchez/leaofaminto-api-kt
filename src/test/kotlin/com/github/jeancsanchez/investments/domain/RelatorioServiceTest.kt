package com.github.jeancsanchez.investments.domain

import com.github.jeancsanchez.investments.data.OperacaoRepository
import com.github.jeancsanchez.investments.data.PapelRepository
import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

/**
 * @author @jeancsanchez
 * @created 21/05/2021
 * Jesus loves you.
 */

@RunWith(MockitoJUnitRunner::class)
class RelatorioServiceTest {

    @Mock
    lateinit var operacaoRepository: OperacaoRepository

    @Mock
    lateinit var papelRepository: PapelRepository

    @InjectMocks
    private lateinit var relatorioService: RelatorioService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun pegarPapeisConsolidados() {
        Mockito.`when`(operacaoRepository.findAll()).thenAnswer {
            FakeFactory.getOperacoes()
        }

        val result = relatorioService.pegarOperacoesConsolidadas()

        // Deve retornar apenas um item consolidando as operações
        assertEquals(2, result.items.size)

        val itemsSimpar = result.items.filter { it.papel == "SIMH3" }
        val itemsCeA = result.items.filter { it.papel == "CEAB3" }

        // Deve retornar a quantidade de ações considerando as operações
        assertEquals(20, itemsSimpar.sumBy { it.quantidadeTotal })

        // Deve retornar a quantidade de ações considerando as operações
        assertEquals(50, itemsCeA.sumBy { it.quantidadeTotal })

        // Deve retornar o preço médio das ações
        assertEquals(18.50, itemsSimpar.sumByDouble { it.precoMedio })

        // Deve retornar o preço médio das ações
        assertEquals(12.88, itemsCeA.sumByDouble { it.precoMedio })

        // Deve retornar o total considerando as operações
        assertEquals(1013.78, result.totalInvestido)
    }

    @Test
    fun quandoSIMH3OuJSLG3ConsiderarOMesmoPapel() {
        val data = FakeFactory.getOperacoes().filter { it.papel.codigo == "SIMH3" || it.papel.codigo == "JSLG3" }
        Mockito.`when`(operacaoRepository.findAll()).thenReturn(data)
        Mockito.`when`(operacaoRepository.findAllByPapelCodigo(anyString())).thenReturn(data)

        val result = relatorioService.pegarOperacoesConsolidadas()

        // Deve retornar apenas um item consolidando as operações
        assertEquals(1, result.items.size)

        // Deve retornar a quantidade de ações considerando as operações
        assertEquals(20, result.items.first().quantidadeTotal)

        // Deve retornar o preço médio das ações
        assertEquals(18.50, result.items.first().precoMedio)

        // Deve retornar o total considerando as operações
        assertEquals(370.0, result.totalInvestido)
    }
}