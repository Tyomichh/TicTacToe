package com.example.lessontictactoe

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lessontictactoe.ui.theme.LessonTicTacToeTheme
import kotlinx.coroutines.delay

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var isDarkTheme by remember { mutableStateOf(false) }

    LessonTicTacToeTheme(darkTheme = isDarkTheme) {
        var dim by remember { mutableIntStateOf(3) }
        var gameStarted by remember { mutableStateOf(false) }
        var gameState by remember { mutableStateOf(GameState.IN_PROGRESS) }
        var currentPlayer by remember { mutableStateOf("X") }
        var playerXScore by remember { mutableIntStateOf(0) }
        var playerOScore by remember { mutableIntStateOf(0) }
        var showScore by remember { mutableStateOf(false) }
        var showGameResult by remember { mutableStateOf(false) }

        LaunchedEffect(gameState) {
            if (gameState != GameState.IN_PROGRESS) {
                showGameResult = true
                delay(5000)
                showGameResult = false
            }
        }


        val field = remember { mutableStateListOf(*Array(dim * dim) { "_" }) }
        val moveHistory = remember { mutableStateListOf<Int>() }

        Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tic Tac Toe",
                    style = MaterialTheme.typography.headlineMedium
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Dark Theme",
                        modifier = Modifier.padding(end = 4.dp))
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { isDarkTheme = it }
                    )
                }
            }

            if (!gameStarted) {
                Text(
                    text = "Виберіть розмір поля:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 300.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp).padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    listOf(3, 4, 5).forEach { size ->
                        Button(onClick = {
                            dim = size
                            gameStarted = true
                            gameState = GameState.IN_PROGRESS
                            field.clear()
                            field.addAll(List(size * size) { "_" })
                            moveHistory.clear()
                        }) {
                            Text("${size}x$size")
                        }
                    }
                }

                if (showGameResult) {
                    GameResult(gameState)
                }
            }
            else {
                Spacer(modifier = Modifier.height(16.dp))

                if (showScore) {
                    Text(
                        text = "Рахунок: X = $playerXScore | O = $playerOScore",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Text(
                    text = "Хід гравця: $currentPlayer",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                GameBoard(
                    dim = dim,
                    gameState = gameState,
                    currentPlayer = currentPlayer,
                    field = field,
                    moveHistory = moveHistory,
                    onMove = { updatedField ->
                        gameState = checkGameState(updatedField, dim)
                        if (gameState == GameState.CROSS_WIN) playerXScore++
                        if (gameState == GameState.NOUGHT_WIN) playerOScore++
                        if (gameState != GameState.IN_PROGRESS) {
                            gameStarted = false
                        } else {
                            currentPlayer = if (currentPlayer == "X") "0" else "X"
                        }
                    },
                    onTimerExpire = {
                        currentPlayer = if (currentPlayer == "X") "0" else "X"
                    }
                )

                ControlButtons(
                    onReset = {
                        if (moveHistory.isNotEmpty()) {
                            val lastMove = moveHistory.removeAt(moveHistory.lastIndex)
                            field[lastMove] = "_"
                            currentPlayer = if (currentPlayer == "X") "0" else "X"
                            gameState = GameState.IN_PROGRESS
                        }
                    },
                    onNewGame = {
                        gameState = GameState.IN_PROGRESS
                        currentPlayer = "X"
                        playerXScore = 0
                        playerOScore = 0
                        gameStarted = false
                        field.clear()
                        moveHistory.clear()
                        showScore = false
                    },
                    onShowScore = {
                        showScore = !showScore
                    }
                )
            }
        }
    }
}

@Composable
fun ControlButtons(
    onReset: () -> Unit,
    onNewGame: () -> Unit,
    onShowScore: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = onReset, modifier = Modifier.weight(0.8f).padding(end = 4.dp).padding(start = 4.dp)) { Text("Скинути раунд") }
            Button(onClick = onShowScore, modifier = Modifier.weight(0.8f).padding(end = 4.dp).padding(start = 4.dp)) { Text("Показати рахунок") }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = onNewGame, modifier = Modifier.weight(0.8f).padding(end = 4.dp).padding(start = 4.dp)) { Text("Нова гра") }
        }
    }
}

@Composable
fun GameBoard(
    dim: Int,
    gameState: GameState,
    currentPlayer: String,
    field: MutableList<String>,
    moveHistory: MutableList<Int>,
    onMove: (List<String>) -> Unit,
    onTimerExpire: () -> Unit
) {
    var timerCount by remember { mutableIntStateOf(10) }

    LaunchedEffect(currentPlayer, field) {
        timerCount = 10
        while (timerCount > 0 && gameState == GameState.IN_PROGRESS) {
            delay(1000)
            timerCount--
        }
        if (timerCount == 0 && gameState == GameState.IN_PROGRESS) onTimerExpire()
    }

    Column(modifier = Modifier.padding(top = 8.dp)) {
        LinearProgressIndicator(
            progress = { timerCount / 10f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
        Text(
            text = "$timerCount s",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment  = Alignment.Center
        ) {
            Column {
                for (row in 0 until dim) {
                    Row(horizontalArrangement = Arrangement.Center) {
                        for (col in 0 until dim) {
                            val index = row * dim + col
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .padding(4.dp)
                                    .border(2.dp, MaterialTheme.colorScheme.secondary)
                                    .clickable {
                                        if (field[index] == "_" && gameState == GameState.IN_PROGRESS) {
                                            field[index] = currentPlayer
                                            moveHistory.add(index)
                                            onMove(field.toList())
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = field[index],
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameResult(gameState: GameState) {
    when (gameState) {
        GameState.CROSS_WIN -> Text(
            text = "Гравець X переміг!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            textAlign = TextAlign.Center
        )
        GameState.NOUGHT_WIN -> Text(
            text = "Гравець 0 переміг!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            textAlign = TextAlign.Center
        )
        GameState.DRAW -> Text(
            text = "Нічия!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            textAlign = TextAlign.Center
        )
        else -> {}
    }
}

fun checkGameState(field: List<String>, dim: Int): GameState {
    val winLines = generateWinLines(dim)
    for (line in winLines) {
        if (line.all { field[it] == "X" }) return GameState.CROSS_WIN
        if (line.all { field[it] == "0" }) return GameState.NOUGHT_WIN
    }
    return if (field.any { it == "_" }) GameState.IN_PROGRESS else GameState.DRAW
}

fun generateWinLines(dim: Int): List<List<Int>> {
    val lines = mutableListOf<List<Int>>()
    for (i in 0 until dim) lines.add(List(dim) { j -> i * dim + j }) // rows
    for (j in 0 until dim) lines.add(List(dim) { i -> i * dim + j }) // columns
    lines.add(List(dim) { i -> i * dim + i }) // main diagonal
    lines.add(List(dim) { i -> (i + 1) * (dim - 1) }) // anti-diagonal
    return lines
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}
