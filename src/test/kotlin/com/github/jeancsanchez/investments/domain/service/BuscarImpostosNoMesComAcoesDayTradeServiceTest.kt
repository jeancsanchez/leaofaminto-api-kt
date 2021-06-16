package com.github.jeancsanchez.investments.domain.service

import com.github.jeancsanchez.investments.data.OperacaoRepository
import com.github.jeancsanchez.investments.domain.BuscarImpostosNoMesComAcoesDayTradeService
import com.github.jeancsanchez.investments.domain.model.*
import junit.framework.TestCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
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

    @Test
    @Suppress("DANGEROUS_CHARACTERS")
    fun `Day trade - qualquer lucro no dia com acoes gera imposto de 20% sobre o lucro do mes`() {
        val today = LocalDate.of(2021, 1, 1)
        val tomorrow = today.plusDays(1)
        whenever(operacaoRepository.findAll()).thenAnswer {
            listOf(
                Compra(
                    ativo = Ativo(codigo = "ITSA4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 10,
                    preco = 100.0,
                    data = today,
                ),
                Venda(
                    ativo = Ativo(codigo = "ITSA4", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 10,
                    preco = 200.0,
                    data = today
                ),
                Compra(
                    ativo = Ativo(codigo = "ITUB3", tipoDeAtivo = TipoDeAtivo.ACAO),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 10,
                    preco = 100.0,
                    data = tomorrow,
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

        val impostos = buscarImpostosNoMesComAcoesDayTradeService.execute(today)
        TestCase.assertEquals(399.98, impostos)
    }
}