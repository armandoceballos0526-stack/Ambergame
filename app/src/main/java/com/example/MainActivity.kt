package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.data.ScreenRoute
import com.example.ui.MainViewModel
import com.example.ui.components.AmberTopBar
import com.example.ui.components.ParentalGateDialog
import com.example.ui.screens.AnimalXylophoneScreen
import com.example.ui.screens.BubblePopScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MagicPaintScreen
import com.example.ui.screens.MemoryGameScreen
import com.example.ui.screens.NumbersGameScreen
import com.example.ui.screens.ParentDashboardScreen
import com.example.ui.screens.PetCareScreen
import com.example.ui.screens.ShapesGameScreen
import com.example.ui.screens.SizesGameScreen
import com.example.ui.screens.StickerAlbumScreen
import com.example.ui.screens.VowelsGameScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val childName by viewModel.childName.collectAsState()
    val starsCount by viewModel.stars.collectAsState()
    val difficulty by viewModel.difficulty.collectAsState()
    val showParentalGate by viewModel.showParentalGate.collectAsState()

    // Handle back button on sub-screens
    if (currentScreen != ScreenRoute.HOME) {
        BackHandler {
            viewModel.navigateBack()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AmberTopBar(
                childName = childName,
                starsCount = starsCount,
                canGoBack = currentScreen != ScreenRoute.HOME,
                onBackClick = { viewModel.navigateBack() },
                onVoiceRepeatClick = { viewModel.repeatVoiceInstruction() },
                onParentZoneClick = { viewModel.openParentZoneRequested() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentScreen, label = "screen_crossfade") { screen ->
                when (screen) {
                    ScreenRoute.HOME -> {
                        HomeScreen(
                            childName = childName,
                            starsCount = starsCount,
                            onNavigate = { route -> viewModel.navigateTo(route) }
                        )
                    }
                    ScreenRoute.NUMBERS -> {
                        NumbersGameScreen(
                            childName = childName,
                            difficulty = difficulty,
                            soundFx = viewModel.soundFx,
                            speechManager = viewModel.speechManager,
                            onCompleteLevel = { stars -> viewModel.completeGameActivity(ScreenRoute.NUMBERS, stars) },
                            onGoHome = { viewModel.navigateBack() }
                        )
                    }
                    ScreenRoute.VOWELS -> {
                        VowelsGameScreen(
                            childName = childName,
                            soundFx = viewModel.soundFx,
                            speechManager = viewModel.speechManager,
                            onCompleteLevel = { stars -> viewModel.completeGameActivity(ScreenRoute.VOWELS, stars) },
                            onGoHome = { viewModel.navigateBack() }
                        )
                    }
                    ScreenRoute.SHAPES -> {
                        ShapesGameScreen(
                            childName = childName,
                            soundFx = viewModel.soundFx,
                            speechManager = viewModel.speechManager,
                            onCompleteLevel = { stars -> viewModel.completeGameActivity(ScreenRoute.SHAPES, stars) },
                            onGoHome = { viewModel.navigateBack() }
                        )
                    }
                    ScreenRoute.MEMORY -> {
                        MemoryGameScreen(
                            childName = childName,
                            difficulty = difficulty,
                            soundFx = viewModel.soundFx,
                            speechManager = viewModel.speechManager,
                            onCompleteLevel = { stars -> viewModel.completeGameActivity(ScreenRoute.MEMORY, stars) },
                            onGoHome = { viewModel.navigateBack() }
                        )
                    }
                    ScreenRoute.BUBBLES -> {
                        BubblePopScreen(
                            childName = childName,
                            soundFx = viewModel.soundFx,
                            speechManager = viewModel.speechManager,
                            onBubblesPopped = { count -> viewModel.onBubblePopped(count) },
                            onEarnStar = { viewModel.earnSingleStar() }
                        )
                    }
                    ScreenRoute.MAGIC_PAINT -> {
                        MagicPaintScreen(
                            childName = childName,
                            soundFx = viewModel.soundFx,
                            speechManager = viewModel.speechManager,
                            onCompleteDrawing = { viewModel.earnSingleStar() }
                        )
                    }
                    ScreenRoute.XYLOPHONE -> {
                        AnimalXylophoneScreen(
                            childName = childName,
                            soundFx = viewModel.soundFx,
                            speechManager = viewModel.speechManager
                        )
                    }
                    ScreenRoute.STICKER_ALBUM -> {
                        val stickers by viewModel.stickers.collectAsState()
                        StickerAlbumScreen(
                            childName = childName,
                            starsCount = starsCount,
                            stickers = stickers,
                            soundFx = viewModel.soundFx,
                            speechManager = viewModel.speechManager
                        )
                    }
                    ScreenRoute.SIZES_OPPOSITES -> {
                        SizesGameScreen(
                            childName = childName,
                            soundFx = viewModel.soundFx,
                            speechManager = viewModel.speechManager,
                            onCompleteLevel = { stars -> viewModel.completeGameActivity(ScreenRoute.SIZES_OPPOSITES, stars) },
                            onGoHome = { viewModel.navigateBack() }
                        )
                    }
                    ScreenRoute.PET_CARE -> {
                        PetCareScreen(
                            childName = childName,
                            soundFx = viewModel.soundFx,
                            speechManager = viewModel.speechManager,
                            onCareAction = { viewModel.completeGameActivity(ScreenRoute.PET_CARE, 0) },
                            onEarnStar = { viewModel.earnSingleStar() }
                        )
                    }
                    ScreenRoute.PARENT_DASHBOARD -> {
                        val timePlayed by viewModel.timePlayedMinutes.collectAsState()
                        val totalGames by viewModel.totalGamesPlayed.collectAsState()
                        val numbersDone by viewModel.numbersCompleted.collectAsState()
                        val vowelsDone by viewModel.vowelsCompleted.collectAsState()
                        val shapesDone by viewModel.shapesCompleted.collectAsState()
                        val memoryDone by viewModel.memoryCompleted.collectAsState()
                        val bubblesDone by viewModel.bubblesPopped.collectAsState()
                        val oppositesDone by viewModel.oppositesCompleted.collectAsState()
                        val petCareDone by viewModel.petCareActionsCount.collectAsState()
                        val voiceOn by viewModel.isVoiceEnabled.collectAsState()
                        val soundOn by viewModel.isSoundFxEnabled.collectAsState()
                        val speechRate by viewModel.speechRate.collectAsState()

                        ParentDashboardScreen(
                            childName = childName,
                            starsCount = starsCount,
                            timePlayedMinutes = timePlayed,
                            totalGamesPlayed = totalGames,
                            numbersCompleted = numbersDone,
                            vowelsCompleted = vowelsDone,
                            shapesCompleted = shapesDone,
                            memoryCompleted = memoryDone,
                            bubblesPopped = bubblesDone,
                            oppositesCompleted = oppositesDone,
                            petCareActionsCount = petCareDone,
                            difficulty = difficulty,
                            isVoiceEnabled = voiceOn,
                            isSoundFxEnabled = soundOn,
                            speechRate = speechRate,
                            onUpdateChildName = { newName -> viewModel.updateChildName(newName) },
                            onChangeDifficulty = { newDiff -> viewModel.changeDifficulty(newDiff) },
                            onToggleVoice = { enabled -> viewModel.toggleVoice(enabled) },
                            onToggleSoundFx = { enabled -> viewModel.toggleSoundFx(enabled) },
                            onChangeSpeechRate = { rate -> viewModel.changeSpeechRate(rate) },
                            onResetProgress = { viewModel.resetProgress() }
                        )
                    }
                }
            }
        }
    }

    if (showParentalGate) {
        ParentalGateDialog(
            onDismiss = { viewModel.dismissParentalGate() },
            onSuccess = { viewModel.onParentalGatePassed() }
        )
    }
}
