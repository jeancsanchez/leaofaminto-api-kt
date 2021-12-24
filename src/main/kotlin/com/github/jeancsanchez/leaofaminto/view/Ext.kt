package com.github.jeancsanchez.leaofaminto.view

import com.github.jeancsanchez.leaofaminto.domain.model.TipoDeAtivo
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

/**
 * @author @jeancsanchez
 * @created 17/05/2021
 * Jesus loves you.
 */

/**
 * Formata [LocalDate] para string no formato brasileiro
 * Ex: 2021-08-01 --> 01/08/2021
 */
fun LocalDate.formatToStringBR(): String {
    var day = dayOfMonth.toString()
    var month = monthValue.toString()

    if (dayOfMonth < 10) {
        day = "0".plus(dayOfMonth)
    }

    if (monthValue < 10) {
        month = "0".plus(monthValue)
    }

    return "$day/$month/$year"
}

/**
 * Formata string para o formato [LocalDate]
 */
fun String.formatStringBRToDate(): LocalDate? {
    if (trim().contains("/")) {
        return try {
            val arr = split("/")
            val day = arr[0].trim()
            val month = arr[1].trim()
            val year = arr[2].trim()

            LocalDate.of(year.toInt(), month.toInt(), day.toInt())
        } catch (t: Throwable) {
            null
        }
    }

    return null
}

/**
 * Extrai o tipo do ativo
 */
fun String.extractTipoDeAtivo(): TipoDeAtivo {
    if (trim().contains("FII", true)) {
        return TipoDeAtivo.FII
    }

    return TipoDeAtivo.ACAO
}

/**
 * Extrai o codigo do ativo
 */
fun String.extractCodigoAtivo(): String {
    if (trim().last().equals('F', true)) {
        return this.toUpperCase().dropLast(1)
    }

    return this.toUpperCase()
}

/**
 * Extrai o codigo do ativo do arquivo de movimentações v2 do portal CEI
 */
fun String.extractCodigoAtivoV2(): String {
    return this.split("-")[0].trim().toUpperCase()
}

/**
 * Extrai o nome da corretora
 */
fun String.extractNomeCorretora(): String {
    if (contains("Clear", true)) {
        return "Clear"
    }

    return this
}

/**
 * Arredonda o número "para cima"
 */
fun Double.round(): Double {
    return BigDecimal(this)
        .setScale(2, RoundingMode.HALF_EVEN)
        .toDouble()
}