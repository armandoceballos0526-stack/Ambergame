package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

/**
 * TextToSpeech voice guide for Amber in Spanish.
 * Speaks warm, clear, friendly instructions and positive praise.
 */
class SpeechManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    var isSpeechEnabled: Boolean = true
    var speechSpeed: Float = 0.88f // Gentle rate suited for 4-year-olds

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("es", "ES"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to generic Spanish
                tts?.setLanguage(Locale("es"))
            }
            tts?.setSpeechRate(speechSpeed)
            tts?.setPitch(1.15f) // Warm, friendly, slightly higher tone for kids
            isInitialized = true
        } else {
            Log.w("SpeechManager", "TTS initialization failed")
        }
    }

    fun speak(text: String) {
        if (!isSpeechEnabled || !isInitialized) return
        try {
            tts?.setSpeechRate(speechSpeed)
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "amber_speech_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e("SpeechManager", "Error speaking text", e)
        }
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (_: Exception) {}
    }

    fun speakPraise(childName: String) {
        val praises = listOf(
            "¡Muy bien, $childName! ¡Lo hiciste excelente!",
            "¡Bravo, $childName! ¡Qué inteligente eres!",
            "¡Increíble, $childName! ¡Una estrella brillante para ti!",
            "¡Fantástico, $childName! ¡Eres una campeona!",
            "¡Genial, $childName! ¡Sigue jugando y aprendiendo!"
        )
        speak(praises.random())
    }

    fun speakEncouragement(childName: String) {
        val messages = listOf(
            "¡Tú puedes, $childName! Inténtalo otra vez.",
            "¡Casi lo tienes, $childName! Prueba de nuevo.",
            "¡Vamos $childName, busca con cuidado!"
        )
        speak(messages.random())
    }

    fun release() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {}
    }
}
