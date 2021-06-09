package com.github.jeancsanchez.investments.domain

import com.github.jeancsanchez.investments.domain.model.TipoAcao
import com.github.jeancsanchez.investments.domain.model.TipoDeLote
import com.github.jeancsanchez.investments.domain.model.TipoOperacao
import com.github.jeancsanchez.investments.view.*
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
        assertEquals(TipoAcao.PREFERENCIAL, string.extractTipoAcao())
    }

    @Test
    fun extractTipoDeAcaoON() {
        val string = "MAGAZ LUIZA  ON      NM"
        assertEquals(TipoAcao.ORDINARIA, string.extractTipoAcao())
    }

    @Test
    fun extractTipoDeAcaoFII() {
        val string = "FII XP LOG   CI"
        assertEquals(TipoAcao.FUNDO_IMOBILIARIO, string.extractTipoAcao())
    }

    @Test
    fun extractTipoOperacaoVenda() {
        val string = "v"
        assertEquals(TipoOperacao.VENDA, string.extractTipoOperacao())
    }

    @Test
    fun extractTipoOperacaoCompra() {
        val string = "c"
        assertEquals(TipoOperacao.COMPRA, string.extractTipoOperacao())
    }

    @Test
    fun extractPapel() {
        val string = "itub3f"
        assertEquals("ITUB3", string.extractPapelName())

        val string1 = "itub3"
        assertEquals("ITUB3", string1.extractPapelName())
    }

    @Test
    fun extractTipoDeLote() {
        val string = "itub3f"
        assertEquals(TipoDeLote.FRACIONARIO, string.extractTipoDeLote())

        val string1 = "itub3"
        assertEquals(TipoDeLote.LOTE_DE_100, string1.extractTipoDeLote())
    }
}