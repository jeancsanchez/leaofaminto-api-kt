package com.github.jeancsanchez.leaofaminto.view

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author @jeancsanchez
 * @created 30/04/2022
 * Jesus loves you.
 */

class ExtTest {

    @Test
    fun toQuantidadeString1() {
        val quantidade = 1.0

        val result = quantidade.toQuantidadeString()

        assertEquals("1", result)
    }

    @Test
    fun toQuantidadeString2() {
        val quantidade = 1.1

        val result = quantidade.toQuantidadeString()

        assertEquals("1.1", result)
    }

    @Test
    fun toQuantidadeString3() {
        val quantidade = 0.123423

        val result = quantidade.toQuantidadeString()

        assertEquals("0.123423", result)
    }
}