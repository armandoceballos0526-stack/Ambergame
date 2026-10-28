package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * Procedural low-latency sound generator for Amber's game.
 * Generates pop sounds, xylophone notes, success chimes, and star fanfares
 * without requiring external sound files.
 */
class SoundFxManager(private val vibrator: Vibrator?) {

    private val audioScope = CoroutineScope(Dispatchers.Default)

    var isSoundEnabled: Boolean = true
    var isHapticsEnabled: Boolean = true

    fun playPop() {
        if (isHapticsEnabled) {
            vibrate(30)
        }
        if (!isSoundEnabled) return
        audioScope.launch {
            // Rapid pitch drop produces a cartoon bubble pop sound
            playChirp(startFreq = 800f, endFreq = 200f, durationMs = 60, amplitude = 0.7f)
        }
    }

    fun playTap() {
        if (isHapticsEnabled) {
            vibrate(15)
        }
        if (!isSoundEnabled) return
        audioScope.launch {
            playTone(freq = 600f, durationMs = 30, amplitude = 0.3f)
        }
    }

    fun playSuccess() {
        if (isHapticsEnabled) {
            vibrate(50)
        }
        if (!isSoundEnabled) return
        audioScope.launch {
            // Cheerful major chord arpeggio: C5 -> E5 -> G5 -> C6
            val notes = listOf(523.25f, 659.25f, 783.99f, 1046.50f)
            for (freq in notes) {
                playTone(freq, 70, 0.45f)
            }
        }
    }

    fun playStarFanfare() {
        if (isHapticsEnabled) {
            vibrate(70)
        }
        if (!isSoundEnabled) return
        audioScope.launch {
            // Triumphant star reward: G5 -> B5 -> D6 -> G6
            val notes = listOf(783.99f, 987.77f, 1174.66f, 1567.98f)
            for (freq in notes) {
                playTone(freq, 110, 0.5f)
            }
        }
    }

    fun playMunch() {
        if (isHapticsEnabled) {
            vibrate(35)
        }
        if (!isSoundEnabled) return
        audioScope.launch {
            // Crunchy cartoon bite: quick crisp double chirp
            playChirp(startFreq = 480f, endFreq = 180f, durationMs = 50, amplitude = 0.65f)
            kotlinx.coroutines.delay(20)
            playChirp(startFreq = 540f, endFreq = 220f, durationMs = 45, amplitude = 0.55f)
        }
    }

    fun playPurr() {
        if (isHapticsEnabled) {
            vibrate(40)
        }
        if (!isSoundEnabled) return
        audioScope.launch {
            // Low soothing rumble frequency mimicking a happy purr
            val purrFrequencies = listOf(140f, 160f, 130f, 150f)
            for (freq in purrFrequencies) {
                playTone(freq, 65, 0.35f)
            }
        }
    }

    fun playGiggle() {
        if (isHapticsEnabled) {
            vibrate(25)
        }
        if (!isSoundEnabled) return
        audioScope.launch {
            // Playful bubbly staccato notes in quick succession
            val notes = listOf(587f, 659f, 784f, 880f, 1046f)
            for (freq in notes) {
                playTone(freq, 40, 0.4f)
                kotlinx.coroutines.delay(25)
            }
        }
    }

    fun playMagicChime() {
        if (isHapticsEnabled) {
            vibrate(30)
        }
        if (!isSoundEnabled) return
        audioScope.launch {
            // Shimmering celestial glissando for grooming & stars
            val chimeNotes = listOf(1046.50f, 1318.51f, 1567.98f, 2093.00f)
            for (freq in chimeNotes) {
                playTone(freq, 60, 0.35f)
            }
        }
    }

    fun playSnore() {
        if (isHapticsEnabled) {
            vibrate(20)
        }
        if (!isSoundEnabled) return
        audioScope.launch {
            // Gentle warm breathing whoosh
            playChirp(startFreq = 160f, endFreq = 110f, durationMs = 180, amplitude = 0.25f)
        }
    }

    fun playGentleTryAgain() {
        if (isHapticsEnabled) {
            vibrate(25)
        }
        if (!isSoundEnabled) return
        audioScope.launch {
            // Gentle encouragement two-tone
            playTone(440f, 90, 0.3f)
            playTone(392f, 130, 0.3f)
        }
    }

    fun playXylophoneNote(noteIndex: Int) {
        if (isHapticsEnabled) {
            vibrate(25)
        }
        if (!isSoundEnabled) return
        audioScope.launch {
            // C4, D4, E4, F4, G4, A4, B4, C5
            val frequencies = listOf(
                261.63f, // Do
                293.66f, // Re
                329.63f, // Mi
                349.23f, // Fa
                392.00f, // Sol
                440.00f, // La
                493.88f, // Si
                523.25f  // Do agudo
            )
            val freq = frequencies.getOrElse(noteIndex.coerceIn(0, frequencies.size - 1)) { 440f }
            playXylophoneTone(freq, 350, 0.65f)
        }
    }

    private fun playTone(freq: Float, durationMs: Int, amplitude: Float) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / sampleRate
                // Sine wave with linear decay
                val envelope = 1f - (i.toFloat() / numSamples)
                val sample = sin(2.0 * PI * freq * t) * envelope * amplitude
                buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
            }

            writeAndPlayBuffer(buffer, sampleRate)
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    private fun playChirp(startFreq: Float, endFreq: Float, durationMs: Int, amplitude: Float) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
            val buffer = ShortArray(numSamples)

            var phase = 0.0
            for (i in 0 until numSamples) {
                val progress = i.toFloat() / numSamples
                val currentFreq = startFreq + (endFreq - startFreq) * progress
                val dt = 1.0 / sampleRate
                phase += 2.0 * PI * currentFreq * dt
                val envelope = (1f - progress) * (1f - progress)
                val sample = sin(phase) * envelope * amplitude
                buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
            }

            writeAndPlayBuffer(buffer, sampleRate)
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    private fun playXylophoneTone(freq: Float, durationMs: Int, amplitude: Float) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / sampleRate
                // Exponential decay bell-like envelope
                val envelope = kotlin.math.exp(-3.5 * (i.toDouble() / numSamples)).toFloat()
                // Fundamental + mild 2nd harmonic for rich bell timbre
                val sample = (sin(2.0 * PI * freq * t) * 0.85 + sin(4.0 * PI * freq * t) * 0.15) * envelope * amplitude
                buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
            }

            writeAndPlayBuffer(buffer, sampleRate)
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    private fun writeAndPlayBuffer(buffer: ShortArray, sampleRate: Int) {
        var track: AudioTrack? = null
        try {
            val minBufSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSizeInBytes = maxOf(buffer.size * 2, minBufSize)

            track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSizeInBytes)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            Thread.sleep((buffer.size * 1000L / sampleRate) + 30)
        } catch (_: Exception) {
        } finally {
            try {
                track?.stop()
                track?.release()
            } catch (_: Exception) {}
        }
    }

    private fun vibrate(durationMs: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }
}
