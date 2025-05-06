package com.example.lessontictactoe


import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /* Тест для перевірки початкового відображення поля*/
    @Test
    fun testFieldInitializationOnUI() {
        composeTestRule.setContent {
            MainScreen()
        }

        composeTestRule.onNodeWithText("Tic Tac Toe").assertExists()
    }
}
