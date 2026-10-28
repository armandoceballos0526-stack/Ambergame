package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class ProgressRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("amber_world_progress", Context.MODE_PRIVATE)

    private val _childName = MutableStateFlow(prefs.getString("child_name", "Amber") ?: "Amber")
    val childName: StateFlow<String> = _childName.asStateFlow()

    private val _stars = MutableStateFlow(prefs.getInt("stars_count", 3)) // starts with 3 welcome stars!
    val stars: StateFlow<Int> = _stars.asStateFlow()

    private val _difficulty = MutableStateFlow(
        try {
            DifficultyLevel.valueOf(prefs.getString("difficulty_level", DifficultyLevel.PRINCIPIANTE.name) ?: DifficultyLevel.PRINCIPIANTE.name)
        } catch (_: Exception) {
            DifficultyLevel.PRINCIPIANTE
        }
    )
    val difficulty: StateFlow<DifficultyLevel> = _difficulty.asStateFlow()

    private val _timePlayedMinutes = MutableStateFlow(prefs.getInt("time_played_minutes", 12))
    val timePlayedMinutes: StateFlow<Int> = _timePlayedMinutes.asStateFlow()

    private val _totalGamesPlayed = MutableStateFlow(prefs.getInt("total_games_played", 5))
    val totalGamesPlayed: StateFlow<Int> = _totalGamesPlayed.asStateFlow()

    private val _numbersCompleted = MutableStateFlow(prefs.getInt("numbers_completed", 3))
    val numbersCompleted: StateFlow<Int> = _numbersCompleted.asStateFlow()

    private val _vowelsCompleted = MutableStateFlow(prefs.getInt("vowels_completed", 2))
    val vowelsCompleted: StateFlow<Int> = _vowelsCompleted.asStateFlow()

    private val _shapesCompleted = MutableStateFlow(prefs.getInt("shapes_completed", 2))
    val shapesCompleted: StateFlow<Int> = _shapesCompleted.asStateFlow()

    private val _memoryCompleted = MutableStateFlow(prefs.getInt("memory_completed", 1))
    val memoryCompleted: StateFlow<Int> = _memoryCompleted.asStateFlow()

    private val _bubblesPopped = MutableStateFlow(prefs.getInt("bubbles_popped", 25))
    val bubblesPopped: StateFlow<Int> = _bubblesPopped.asStateFlow()

    private val _oppositesCompleted = MutableStateFlow(prefs.getInt("opposites_completed", 2))
    val oppositesCompleted: StateFlow<Int> = _oppositesCompleted.asStateFlow()

    private val _petCareActionsCount = MutableStateFlow(prefs.getInt("pet_care_actions", 4))
    val petCareActionsCount: StateFlow<Int> = _petCareActionsCount.asStateFlow()

    private val _stickers = MutableStateFlow<List<Sticker>>(loadStickers())
    val stickers: StateFlow<List<Sticker>> = _stickers.asStateFlow()

    private val _voiceEnabled = MutableStateFlow(prefs.getBoolean("voice_enabled", true))
    val voiceEnabled: StateFlow<Boolean> = _voiceEnabled.asStateFlow()

    private val _soundFxEnabled = MutableStateFlow(prefs.getBoolean("sound_fx_enabled", true))
    val soundFxEnabled: StateFlow<Boolean> = _soundFxEnabled.asStateFlow()

    private val _speechRate = MutableStateFlow(prefs.getFloat("speech_rate", 0.88f))
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private fun loadStickers(): List<Sticker> {
        val jsonStr = prefs.getString("stickers_json", null)
        val defaultList = StickerCatalog.initialStickers
        if (jsonStr.isNullOrEmpty()) {
            return defaultList
        }
        return try {
            val jsonArray = JSONArray(jsonStr)
            val list = mutableListOf<Sticker>()
            val currentStarCount = prefs.getInt("stars_count", 3)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.getInt("id")
                val defaultSticker = defaultList.firstOrNull { it.id == id } ?: continue
                val unlocked = obj.optBoolean("unlocked", currentStarCount >= defaultSticker.starsRequired)
                val posX = obj.optDouble("posX", defaultSticker.posX.toDouble()).toFloat()
                val posY = obj.optDouble("posY", defaultSticker.posY.toDouble()).toFloat()
                list.add(defaultSticker.copy(unlocked = unlocked || currentStarCount >= defaultSticker.starsRequired, posX = posX, posY = posY))
            }
            if (list.size == defaultList.size) list else defaultList
        } catch (_: Exception) {
            defaultList
        }
    }

    private fun saveStickers(list: List<Sticker>) {
        try {
            val array = JSONArray()
            list.forEach { sticker ->
                val obj = JSONObject()
                obj.put("id", sticker.id)
                obj.put("unlocked", sticker.unlocked)
                obj.put("posX", sticker.posX)
                obj.put("posY", sticker.posY)
                array.put(obj)
            }
            prefs.edit().putString("stickers_json", array.toString()).apply()
        } catch (_: Exception) {}
    }

    fun addStars(amount: Int) {
        val newStars = _stars.value + amount
        _stars.value = newStars
        prefs.edit().putInt("stars_count", newStars).apply()

        // Auto unlock stickers based on new star count
        val updatedStickers = _stickers.value.map { sticker ->
            if (newStars >= sticker.starsRequired && !sticker.unlocked) {
                sticker.copy(unlocked = true)
            } else {
                sticker
            }
        }
        _stickers.value = updatedStickers
        saveStickers(updatedStickers)
    }

    fun recordGameCompleted(gameType: ScreenRoute, starsEarned: Int = 1) {
        addStars(starsEarned)
        val newGames = _totalGamesPlayed.value + 1
        _totalGamesPlayed.value = newGames
        prefs.edit().putInt("total_games_played", newGames).apply()

        when (gameType) {
            ScreenRoute.NUMBERS -> {
                val count = _numbersCompleted.value + 1
                _numbersCompleted.value = count
                prefs.edit().putInt("numbers_completed", count).apply()
                autoAdvanceDifficultyIfNeeded(count)
            }
            ScreenRoute.VOWELS -> {
                val count = _vowelsCompleted.value + 1
                _vowelsCompleted.value = count
                prefs.edit().putInt("vowels_completed", count).apply()
            }
            ScreenRoute.SHAPES -> {
                val count = _shapesCompleted.value + 1
                _shapesCompleted.value = count
                prefs.edit().putInt("shapes_completed", count).apply()
            }
            ScreenRoute.MEMORY -> {
                val count = _memoryCompleted.value + 1
                _memoryCompleted.value = count
                prefs.edit().putInt("memory_completed", count).apply()
            }
            ScreenRoute.SIZES_OPPOSITES -> {
                val count = _oppositesCompleted.value + 1
                _oppositesCompleted.value = count
                prefs.edit().putInt("opposites_completed", count).apply()
            }
            ScreenRoute.PET_CARE -> {
                val count = _petCareActionsCount.value + 1
                _petCareActionsCount.value = count
                prefs.edit().putInt("pet_care_actions", count).apply()
            }
            ScreenRoute.BUBBLES -> {
                // Bubbles are pure fun
            }
            else -> {}
        }
    }

    private fun autoAdvanceDifficultyIfNeeded(numbersMastered: Int) {
        // Auto-advance difficulty smoothly as Amber masters content
        if (numbersMastered >= 6 && _difficulty.value == DifficultyLevel.PRINCIPIANTE) {
            setDifficulty(DifficultyLevel.INTERMEDIO)
        } else if (numbersMastered >= 15 && _difficulty.value == DifficultyLevel.INTERMEDIO) {
            setDifficulty(DifficultyLevel.AVANZADO)
        }
    }

    fun addBubblesPopped(count: Int) {
        val newCount = _bubblesPopped.value + count
        _bubblesPopped.value = newCount
        prefs.edit().putInt("bubbles_popped", newCount).apply()
    }

    fun addPlayTimeMinutes(minutes: Int) {
        val newTime = _timePlayedMinutes.value + minutes
        _timePlayedMinutes.value = newTime
        prefs.edit().putInt("time_played_minutes", newTime).apply()
    }

    fun setChildName(name: String) {
        val sanitized = name.trim().ifEmpty { "Amber" }
        _childName.value = sanitized
        prefs.edit().putString("child_name", sanitized).apply()
    }

    fun setDifficulty(level: DifficultyLevel) {
        _difficulty.value = level
        prefs.edit().putString("difficulty_level", level.name).apply()
    }

    fun setVoiceEnabled(enabled: Boolean) {
        _voiceEnabled.value = enabled
        prefs.edit().putBoolean("voice_enabled", enabled).apply()
    }

    fun setSoundFxEnabled(enabled: Boolean) {
        _soundFxEnabled.value = enabled
        prefs.edit().putBoolean("sound_fx_enabled", enabled).apply()
    }

    fun setSpeechRate(rate: Float) {
        _speechRate.value = rate
        prefs.edit().putFloat("speech_rate", rate).apply()
    }

    fun updateStickerPosition(stickerId: Int, posX: Float, posY: Float) {
        val updated = _stickers.value.map {
            if (it.id == stickerId) it.copy(posX = posX.coerceIn(0.05f, 0.95f), posY = posY.coerceIn(0.05f, 0.95f))
            else it
        }
        _stickers.value = updated
        saveStickers(updated)
    }

    fun resetProgress() {
        prefs.edit().clear().apply()
        _childName.value = "Amber"
        _stars.value = 3
        _difficulty.value = DifficultyLevel.PRINCIPIANTE
        _timePlayedMinutes.value = 0
        _totalGamesPlayed.value = 0
        _numbersCompleted.value = 0
        _vowelsCompleted.value = 0
        _shapesCompleted.value = 0
        _memoryCompleted.value = 0
        _bubblesPopped.value = 0
        val freshStickers = StickerCatalog.initialStickers
        _stickers.value = freshStickers
        saveStickers(freshStickers)
    }
}
