package com.example.lessontictactoe

import kotlin.system.exitProcess
import kotlin.concurrent.fixedRateTimer
import kotlin.system.measureTimeMillis

enum class GameState {
    IN_PROGRESS,
    CROSS_WIN,
    NOUGHT_WIN,
    DRAW
}

enum class Player {
    CROSS,
    NOUGHT
}

val Player.mark: CellState
    get() = when (this) {
        Player.CROSS -> CellState.CROSS
        Player.NOUGHT -> CellState.NOUGHT
    }

enum class CellState {
    EMPTY,
    CROSS,
    NOUGHT
}

fun checkGameState(field: List<CellState>, dim: Int): GameState {
    fun lineWin(indices: List<Int>): Boolean {
        val first = field[indices[0]]
        return first != CellState.EMPTY && indices.all { field[it] == first }
    }

    // Горизонталі та вертикалі
    for (i in 0 until dim) {
        if (lineWin(List(dim) { i * dim + it })) return when (field[i * dim]) {
            CellState.CROSS -> GameState.CROSS_WIN
            CellState.NOUGHT -> GameState.NOUGHT_WIN
            else -> GameState.IN_PROGRESS
        }
        if (lineWin(List(dim) { it * dim + i })) return when (field[i]) {
            CellState.CROSS -> GameState.CROSS_WIN
            CellState.NOUGHT -> GameState.NOUGHT_WIN
            else -> GameState.IN_PROGRESS
        }
    }

    // Діагоналі
    if (lineWin(List(dim) { it * dim + it })) return when (field[0]) {
        CellState.CROSS -> GameState.CROSS_WIN
        CellState.NOUGHT -> GameState.NOUGHT_WIN
        else -> GameState.IN_PROGRESS
    }
    if (lineWin(List(dim) { it * dim + (dim - it - 1) })) return when (field[dim - 1]) {
        CellState.CROSS -> GameState.CROSS_WIN
        CellState.NOUGHT -> GameState.NOUGHT_WIN
        else -> GameState.IN_PROGRESS
    }

    return if (field.any { it == CellState.EMPTY }) GameState.IN_PROGRESS else GameState.DRAW
}

fun printField(field: List<CellState>, dim: Int) {
    for (row in 0 until dim) {
        for (col in 0 until dim) {
            val symbol = when (field[row * dim + col]) {
                CellState.EMPTY -> "_"
                CellState.CROSS -> "X"
                CellState.NOUGHT -> "0"
            }
            print("$symbol ")
        }
        println()
    }
}

fun main() {
    println("Консольна гра Хрестики-Нулики")
    println("Виберіть розмір поля (3–5): ")
    val dim = readln().toInt().coerceIn(3, 5)

    var crossScore = 0
    var noughtScore = 0

    while (true) {
        var field = MutableList(dim * dim) { CellState.EMPTY }
        var currentPlayer = Player.CROSS
        var turnTimeMillis = 10_000L
        var skipTurn = false

        while (true) {
            printField(field, dim)
            println("Гравець ${if (currentPlayer == Player.CROSS) "X" else "0"}, введіть хід (рядок і стовпчик):")

            var input: String? = null
            var validInput = true

            val inputThread = Thread {
                input = readlnOrNull()
            }
            inputThread.start()
            inputThread.join(turnTimeMillis)

            if (input == null) {
                println("Час вийшов! Хід передано супернику.")
                skipTurn = true
                validInput = false
            } else {
                try {
                    val splitInput = input!!.trim().split(" ")
                    if (splitInput.size != 2) throw Exception("Невірний формат вводу")

                    val row = splitInput[0].toInt()
                    val col = splitInput[1].toInt()
                    val index = row * dim + col

                    if (index !in field.indices || field[index] != CellState.EMPTY) {
                        println("Недопустимий хід. Спробуйте ще раз.")
                        validInput = false
                    } else {
                        field[index] = currentPlayer.mark
                        skipTurn = false
                    }
                } catch (e: Exception) {
                    println("Помилка вводу. Спробуйте ще раз.")
                    validInput = false
                }
            }

            if (!skipTurn) {
                val state = checkGameState(field, dim)
                when (state) {
                    GameState.CROSS_WIN -> {
                        printField(field, dim)
                        println("Гравець X переміг!")
                        crossScore++
                        break
                    }
                    GameState.NOUGHT_WIN -> {
                        printField(field, dim)
                        println("Гравець 0 переміг!")
                        noughtScore++
                        break
                    }
                    GameState.DRAW -> {
                        printField(field, dim)
                        println("Нічия!")
                        break
                    }
                    GameState.IN_PROGRESS -> {}
                }
            }

            currentPlayer = if (currentPlayer == Player.CROSS) Player.NOUGHT else Player.CROSS
        }

        println("Рахунок: X = $crossScore, 0 = $noughtScore")
        println("Натисніть [Enter], щоб розпочати новий раунд або 'exit' для виходу")
        val cmd = readlnOrNull()?.lowercase()?.trim()
        if (cmd == "exit") exitProcess(0)
    }
}
