package com.sergio.flashcards

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sergio.flashcards.ui.theme.FlashcardAppTheme

private data class Flashcard(
    val question: String,
    val answer: String,
    val imageRes: Int
)

private val AppBackgroundColor = Color(0xFF101418)
private val FlashcardCardColor = Color(0xFF1C2532)
private val FlipButtonColor = Color(0xFF2D3748)
private val NextButtonColor = Color(0xFF2563EB)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FlashcardAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AppBackgroundColor
                ) {
                    FlashcardScreen()
                }
            }
        }
    }
}

@Composable
fun FlashcardScreen() {
    val flashcards = listOf(
        Flashcard(
            question = "What is Networking?",
            answer = "Networking is connecting computers to share data.",
            imageRes = R.drawable.network_image
        ),
        Flashcard(
            question = "What is an IP address?",
            answer = "An IP address identifies a device on a network.",
            imageRes = R.drawable.ip_address_image
        ),
        Flashcard(
            question = "What does HTTP stand for?",
            answer = "HTTP stands for HyperText Transfer Protocol.",
            imageRes = R.drawable.http_image
        )
    )

    val context = LocalContext.current
    // Keep the last opened flashcard stored locally on the device.
    val sharedPreferences = remember(context) {
        context.getSharedPreferences("flashcard_prefs", Context.MODE_PRIVATE)
    }

    // Persist the visible card index and the current side of the card.
    fun saveFlashcardState(currentIndex: Int, isFlipped: Boolean) {
        sharedPreferences.edit()
            .putInt("current_index", currentIndex)
            .putBoolean("is_flipped", isFlipped)
            .apply()
    }

    // Restore the saved flashcard state when the screen is recreated.
    var currentIndex by rememberSaveable {
        mutableStateOf(
            sharedPreferences.getInt("current_index", 0).coerceIn(0, flashcards.lastIndex)
        )
    }
    var isFlipped by rememberSaveable {
        mutableStateOf(sharedPreferences.getBoolean("is_flipped", false))
    }
    val currentCard = flashcards[currentIndex]

    val displayText = if (isFlipped) {
        currentCard.answer
    } else {
        currentCard.question
    }

    // Keep the screen logic here and pass only UI data and actions down.
    FlashcardContent(
        text = displayText,
        imageRes = currentCard.imageRes,
        onFlipClick = {
            val nextFlipState = !isFlipped
            isFlipped = nextFlipState
            saveFlashcardState(currentIndex, nextFlipState)
        },
        onNextClick = {
            val nextIndex = (currentIndex + 1) % flashcards.size
            currentIndex = nextIndex
            isFlipped = false
            saveFlashcardState(nextIndex, false)
        }
    )
}

@Composable
private fun FlashcardContent(
    text: String,
    imageRes: Int,
    onFlipClick: () -> Unit,
    onNextClick: () -> Unit
) {
    // Center the flashcard and actions on screen with clear section spacing.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
    ) {
        // Flashcard content is grouped inside a Material 3 card.
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = FlashcardCardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Flashcard concept image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(184.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = text,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 19.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        // Keep the primary actions aligned in one row.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onFlipClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = FlipButtonColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Flip")
            }

            Button(
                onClick = onNextClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NextButtonColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Next")
            }
        }

        Greeting(name = "Sergio")
    }
}

@Composable
private fun Greeting(name: String) {
    Text(
        text = "Hello $name!",
        color = Color.White.copy(alpha = 0.88f),
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun FlashcardPreview() {
    FlashcardAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = AppBackgroundColor
        ) {
            FlashcardScreen()
        }
    }
}
