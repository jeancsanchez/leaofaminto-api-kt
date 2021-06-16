package com.github.jeancsanchez.investments.domain.service

import com.github.jeancsanchez.investments.data.ComprasRepository
import com.github.jeancsanchez.investments.data.VendasRepository
import com.github.jeancsanchez.investments.domain.BuscarLucroLiquidoNoMesComFIIsService
import com.github.jeancsanchez.investments.domain.model.*
import org.hamcrest.MatcherAssert
import org.hamcrest.core.IsEqual
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
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

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    @Suppress("DANGEROUS_CHARACTERS")
    fun `Trazer lucros com FIIs do mes`() {
        val today = LocalDate.of(2021, 2, 1)
        whenever(comprasRepository.findAll()).thenAnswer {
            listOf(
                Compra(
                    ativo = Ativo(codigo = "XPLG11", tipoDeAtivo = TipoDeAtivo.FII),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 10,
                    preco = 100.0,
                    data = today.minusMonths(1),
                )
            )
        }

        whenever(vendasRepository.findAll()).thenAnswer {
            listOf(
                Venda(
                    ativo = Ativo(codigo = "XPLG11", tipoDeAtivo = TipoDeAtivo.FII),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 2,
                    preco = 200.0,
                    data = today.minusMonths(1)
                ),
                // Somente esse entra na contagem
                Venda(
                    ativo = Ativo(codigo = "XPLG11", tipoDeAtivo = TipoDeAtivo.FII),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 4,
                    preco = 210.0,
                    data = today,
                ),
                Venda(
                    ativo = Ativo(codigo = "XPLG11", tipoDeAtivo = TipoDeAtivo.FII),
                    corretora = Corretora(nome = "Clear"),
                    quantidade = 2,
                    preco = 230.0,
                    data = today.plusDays(30)
                ),
            )
        }

        val lucroLiquido = buscarLucroLiquidoNoMesComFIIsService.execute(today)
        MatcherAssert.assertThat(lucroLiquido, IsEqual.equalTo(840.0))
    }
}