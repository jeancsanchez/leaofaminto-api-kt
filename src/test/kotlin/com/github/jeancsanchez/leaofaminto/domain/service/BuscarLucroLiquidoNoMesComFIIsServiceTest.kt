package com.github.jeancsanchez.leaofaminto.domain.service

import com.github.jeancsanchez.leaofaminto.data.ComprasRepository
import com.github.jeancsanchez.leaofaminto.data.VendasRepository
import com.github.jeancsanchez.leaofaminto.domain.BuscarLucroLiquidoNoMesComFIIsService
import com.github.jeancsanchez.leaofaminto.domain.model.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate

/**
 * @author @jeancsanchez
 * @created 15/06/2021
 * Jesus loves you.
 */
@RunWith(MockitoJUnitRunner::class)
internal class BuscarLucroLiquidoNoMesComFIIsServiceTest {

    @Mock
    lateinit var comprasRepository: ComprasRepository

    @Mock
    lateinit var vendasRepository: VendasRepository

    @InjectMocks
    private lateinit var buscarLucroLiquidoNoMesComFIIsService: BuscarLucroLiquidoNoMesComFIIsService

    private val governo = mock<Governo>()
    private val bolsa = mock<Bolsa>()
    private val corretora = mock<Corretora>()


    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        whenever(bolsa.governo).thenAnswer { governo }
        whenever(corretora.bolsa).thenAnswer { bolsa }
    }

    @Test
    @Suppress("DANGEROUS_CHARACTERS")
    fun `Trazer lucros com FIIs do mes, considerando impostos por corretora`() {
        val today = LocalDate.of(2021, 2, 1)
        val corretora2 = mock<Corretora>()
        whenever(governo.taxarLucroVenda(any(), eq(210.0))).thenAnswer { 42.0 }
        whenever(governo.taxarLucroVenda(any(), eq(408.0))).thenAnswer { 81.60 }
        whenever(bolsa.taxarLucroVenda(any(), any())).thenAnswer { 0.0 }
        whenever(corretora.taxarLucroVenda(any(), any())).thenAnswer { 0.0 }
        whenever(corretora2.taxarLucroVenda(any(), any())).thenAnswer { 0.0 }
        whenever(corretora2.bolsa).thenAnswer { bolsa }
        whenever(comprasRepository.findAll()).thenAnswer {
            listOf(
                Compra(
                    ativo = Ativo(codigo = "XPLG11", tipoDeAtivo = TipoDeAtivo.FII),
                    corretora = corretora,
                    quantidade = 10,
                    preco = 100.0,
                    data = today,
                ),
                Compra(
                    ativo = Ativo(codigo = "FII3", tipoDeAtivo = TipoDeAtivo.FII),
                    corretora = corretora2,
                    quantidade = 10,
                    preco = 100.0,
                    data = today,
                )
            )
        }

        whenever(vendasRepository.findAll()).thenAnswer {
            listOf(
                Venda(
                    ativo = Ativo(codigo = "XPLG11", tipoDeAtivo = TipoDeAtivo.FII),
                    corretora = corretora,
                    quantidade = 2,
                    preco = 105.0, // Lucro R$ 210
                    data = today
                ),
                Venda(
                    ativo = Ativo(codigo = "FII3", tipoDeAtivo = TipoDeAtivo.FII),
                    corretora = corretora2,
                    quantidade = 4,
                    preco = 102.0, // Lucro R$ 408
                    data = today.plusDays(5),
                ),

                // Esse não deve ser considerado devido a data ser do mês posterior
                Venda(
                    ativo = Ativo(codigo = "XPLG11", tipoDeAtivo = TipoDeAtivo.FII),
                    corretora = corretora,
                    quantidade = 2,
                    preco = 230.0,
                    data = today.plusMonths(1)
                ),

                // Esse não deve ser considerando devido a data ser do mês anterior
                Venda(
                    ativo = Ativo(codigo = "FII3", tipoDeAtivo = TipoDeAtivo.FII),
                    corretora = corretora2,
                    quantidade = 2,
                    preco = 230.0,
                    data = today.minusMonths(1)
                ),
            )
        }

        val resultado = buscarLucroLiquidoNoMesComFIIsService.execute(today)

        assertNotNull(resultado.find { it.corretora == corretora })
        assertEquals(42.0, resultado.find { it.corretora == corretora }!!.impostos)
        assertEquals(210.0, resultado.find { it.corretora == corretora }!!.lucroLiquido)
        assertNotNull(resultado.find { it.corretora == corretora2 })
        assertEquals(81.60, resultado.find { it.corretora == corretora2 }!!.impostos)
        assertEquals(408.0, resultado.find { it.corretora == corretora2 }!!.lucroLiquido)
        assertEquals(618.0, resultado.sumByDouble { it.lucroLiquido })
    }
}