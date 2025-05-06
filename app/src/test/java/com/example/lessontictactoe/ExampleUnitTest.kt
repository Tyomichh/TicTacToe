package com.example.lessontictactoe

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun testFieldInitialization() {
        val dim = 3
        val field = MutableList(dim * dim) { "_" }
        assertEquals(9, field.size)
        assertTrue(field.all { it == "_" })
    }

    @Test
    fun testGameStateWinX() {
        val field = listOf("X", "X", "X", "_", "_", "_", "_", "_", "_")
        val result = checkGameState(field, 3)
        assertEquals(GameState.CROSS_WIN, result)
    }

    @Test
    fun testGameStateDraw() {
        val field = listOf("X", "0", "X", "X", "0", "0", "0", "X", "X")
        val result = checkGameState(field, 3)
        assertEquals(GameState.DRAW, result)
    }
}