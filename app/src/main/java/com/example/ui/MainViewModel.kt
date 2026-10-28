package com.example.ui

import android.app.Application
import android.content.Context
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundFxManager
import com.example.audio.SpeechManager
import com.example.data.DifficultyLevel
import com.example.data.ProgressRepository
import com.example.data.ScreenRoute
import com.example.data.Sticker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository = ProgressRepository(application.applicationContext)
    
    val soundFx = SoundFxManager(
        application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    )
    val speechManager = SpeechManager(application.applicationContext)

    private val _currentScreen = MutableStateFlow(ScreenRoute.HOME)
    val currentScreen: StateFlow<ScreenRoute> = _currentScreen.asStateFlow()

    private val _showParentalGate = MutableStateFlow(false)
    val showParentalGate: StateFlow<Boolean> = _showParentalGate.asStateFlow()

    val childName = repository.childName
    val stars = repository.stars
    val difficulty = repository.difficulty
    val timePlayedMinutes = repository.timePlayedMinutes
    val totalGamesPlayed = repository.totalGamesPlayed
    val numbersCompleted = repository.numbersCompleted
    val vowelsCompleted = repository.vowelsCompleted
    val shapesCompleted = repository.shapesCompleted
    val memoryCompleted = repository.memoryCompleted
    val bubblesPopped = repository.bubblesPopped
    val oppositesCompleted = repository.oppositesCompleted
    val petCareActionsCount = repository.petCareActionsCount
    val stickers = repository.stickers
    val isVoiceEnabled = repository.voiceEnabled
    val isSoundFxEnabled = repository.soundFxEnabled
    val speechRate = repository.speechRate

    init {
        // Sync audio settings
        soundFx.isSoundEnabled = repository.soundFxEnabled.value
        soundFx.isHapticsEnabled = repository.soundFxEnabled.value
        speechManager.isSpeechEnabled = repository.voiceEnabled.value
        speechManager.speechSpeed = repository.speechRate.value

        // Timer for tracking play time every minute
        viewModelScope.launch {
            while (true) {
                delay(60_000L) // 1 minute
                repository.addPlayTimeMinutes(1)
            }
        }
    }

    fun navigateTo(route: ScreenRoute) {
        soundFx.playTap()
        _currentScreen.value = route
    }

    fun navigateBack() {
        soundFx.playTap()
        speechManager.stop()
        _currentScreen.value = ScreenRoute.HOME
    }

    fun openParentZoneRequested() {
        soundFx.playTap()
        _showParentalGate.value = true
    }

    fun dismissParentalGate() {
        _showParentalGate.value = false
    }

    fun onParentalGatePassed() {
        _showParentalGate.value = false
        _currentScreen.value = ScreenRoute.PARENT_DASHBOARD
    }

    fun repeatVoiceInstruction() {
        soundFx.playTap()
        when (_currentScreen.value) {
            ScreenRoute.HOME -> speechManager.speak("¡Hola ${childName.value}! Elige cualquier juego para divertirte.")
            ScreenRoute.NUMBERS -> speechManager.speak("${childName.value}, cuenta los animalitos tocándolos con tu dedito.")
            ScreenRoute.VOWELS -> speechManager.speak("${childName.value}, busca la vocal correcta del animalito.")
            ScreenRoute.SHAPES -> speechManager.speak("${childName.value}, busca la figura y color que te pedimos.")
            ScreenRoute.MEMORY -> speechManager.speak("${childName.value}, voltea dos cartas para encontrar la pareja de animalitos.")
            ScreenRoute.BUBBLES -> speechManager.speak("¡Explota todas las burbujas que puedas, ${childName.value}!")
            ScreenRoute.MAGIC_PAINT -> speechManager.speak("¡Usa tus colores mágicos y sellitos para crear una obra de arte, ${childName.value}!")
            ScreenRoute.XYLOPHONE -> speechManager.speak("¡Toca las teclas del xilófono para hacer música hermosa!")
            ScreenRoute.STICKER_ALBUM -> speechManager.speak("¡Este es tu álbum de pegatinas mágicas, ${childName.value}!")
            ScreenRoute.SIZES_OPPOSITES -> speechManager.speak("${childName.value}, toca el objeto según el tamaño que te pedimos.")
            ScreenRoute.PET_CARE -> speechManager.speak("¡Dale comidita, baña y hazle mimos a tu mascota, ${childName.value}!")
            ScreenRoute.PARENT_DASHBOARD -> {}
        }
    }

    fun completeGameActivity(route: ScreenRoute, starsEarned: Int) {
        repository.recordGameCompleted(route, starsEarned)
    }

    fun onBubblePopped(count: Int) {
        repository.addBubblesPopped(count)
    }

    fun earnSingleStar() {
        repository.addStars(1)
    }

    fun updateChildName(name: String) {
        repository.setChildName(name)
        speechManager.speak("¡Hola $name!")
    }

    fun changeDifficulty(level: DifficultyLevel) {
        repository.setDifficulty(level)
    }

    fun toggleVoice(enabled: Boolean) {
        repository.setVoiceEnabled(enabled)
        speechManager.isSpeechEnabled = enabled
    }

    fun toggleSoundFx(enabled: Boolean) {
        repository.setSoundFxEnabled(enabled)
        soundFx.isSoundEnabled = enabled
        soundFx.isHapticsEnabled = enabled
    }

    fun changeSpeechRate(rate: Float) {
        repository.setSpeechRate(rate)
        speechManager.speechSpeed = rate
    }

    fun resetProgress() {
        repository.resetProgress()
        soundFx.playSuccess()
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.release()
    }
}
