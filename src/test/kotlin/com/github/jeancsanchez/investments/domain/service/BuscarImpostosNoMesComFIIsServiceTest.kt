package com.github.jeancsanchez.investments.domain.service

import com.github.jeancsanchez.investments.domain.BuscarImpostosNoMesComFIIsService
import com.github.jeancsanchez.investments.domain.BuscarLucroLiquidoNoMesComFIIsService
import junit.framework.TestCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate

/**
 * @author @jeancsanchez
 * @created 15/06/2021
 * Jesus loves you.
 */
@RunWith(MockitoJUnitRunner::class)
internal class BuscarImpostosNoMesComFIIsServiceTest {

    @Mock
    lateinit var buscarLucroLiquidoNoMesComFIIsServiceService: BuscarLucroLiquidoNoMesComFIIsService

    @InjectMocks
    private lateinit var buscarImpostosNoMesComFIIsService: BuscarImpostosNoMesComFIIsService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    @Suppress("DANGEROUS_CHARACTERS")
    fun `FIIs - Day Trade ou nao, gera imposto de 20% sobre o lucro do mes`() {
        val today = LocalDate.of(2021, 2, 1)
        whenever(buscarLucroLiquidoNoMesComFIIsServiceService.execute(any())).thenAnswer {
            1000.0
        }

        val impostos = buscarImpostosNoMesComFIIsService.execute(today)
        verify(buscarLucroLiquidoNoMesComFIIsServiceService).execute(eq(today))
        TestCase.assertEquals(200.0, impostos)
    }
}