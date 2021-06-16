package com.github.jeancsanchez.leaofaminto.domain

import com.github.jeancsanchez.leaofaminto.domain.model.TipoDeAtivo
import com.github.jeancsanchez.leaofaminto.view.extractCodigoAtivo
import com.github.jeancsanchez.leaofaminto.view.extractTipoDeAtivo
import com.github.jeancsanchez.leaofaminto.view.formatStringBRToDate
import com.github.jeancsanchez.leaofaminto.view.formatToStringBR
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate

/**
 * @author @jeancsanchez
 * @created 17/05/2021
 * Jesus loves you.
 */

@RunWith(JUnit4::class)
class ExtensionsTest {

    @Test
    fun formatDateToStringBR() {
        val date = LocalDate.of(2020, 5, 1)
        assertEquals("01/05/2020", date.formatToStringBR())
    }

    @Test
    fun formatStringBRToDate() {
        val dateString = "01/05/2020"
        val date = LocalDate.of(2020, 5, 1)
        assertEquals(date, dateString.formatStringBRToDate())
    }

    @Test
    fun formatStringBRWithWhiteSpacesToDate() {
        val dateString = "  01/05 / 2020"
        val date = LocalDate.of(2020, 5, 1)
        assertEquals(date, dateString.formatStringBRToDate())
    }

    @Test
    fun extractTipoDeAcaoPN() {
        val string = "ITAUSA       PN  ED  N1"
        assertEquals(TipoDeAtivo.ACAO, string.extractTipoDeAtivo())
    }

    @Test
    fun extractTipoDeAcaoON() {
        val string = "MAGAZ LUIZA  ON      NM"
        assertEquals(TipoDeAtivo.ACAO, string.extractTipoDeAtivo())
    }

    @Test
    fun extractTipoDeAcaoFII() {
        val string = "FII XP LOG   CI"
        assertEquals(TipoDeAtivo.FII, string.extractTipoDeAtivo())
    }

    @Test
    fun extractPapel() {
        val string = "itub3f"
        assertEquals("ITUB3", string.extractCodigoAtivo())

        val string1 = "itub3"
        assertEquals("ITUB3", string1.extractCodigoAtivo())
    }
}