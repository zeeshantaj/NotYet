package com.linkitsoft.notyet

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.linkitsoft.notyet.ui.theme.NotYetTheme
import org.junit.Rule
import org.junit.Test

class GreetingTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun greeting_displaysHelloAndroid() {
        composeTestRule.setContent {
            NotYetTheme {
                Greeting(name = "Android")
            }
        }

        composeTestRule.onNodeWithText("Hello Android!").assertIsDisplayed()
    }
}
