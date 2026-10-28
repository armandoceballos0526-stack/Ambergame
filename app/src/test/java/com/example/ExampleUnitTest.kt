package com.example

import com.example.data.AnimalCatalog
import com.example.data.DifficultyLevel
import com.example.data.ShapeCatalog
import com.example.data.StickerCatalog
import com.example.data.VowelCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testVowelsCatalogCompleteness() {
        assertEquals(5, VowelCatalog.vowels.size)
        val vowels = VowelCatalog.vowels.map { it.vowel }
        assertTrue(vowels.containsAll(listOf("A", "E", "I", "O", "U")))
    }

    @Test
    fun testAnimalCatalogHasRichAnimals() {
        assertTrue(AnimalCatalog.animals.size >= 8)
        AnimalCatalog.animals.forEach { animal ->
            assertNotNull(animal.name)
            assertNotNull(animal.simpleName)
            assertNotNull(animal.pluralName)
            assertNotNull(animal.emoji)
        }
    }

    @Test
    fun testDifficultyProgression() {
        assertTrue(DifficultyLevel.PRINCIPIANTE.maxNumber < DifficultyLevel.INTERMEDIO.maxNumber)
        assertTrue(DifficultyLevel.INTERMEDIO.maxNumber < DifficultyLevel.AVANZADO.maxNumber)
        assertEquals(10, DifficultyLevel.AVANZADO.maxNumber)
    }

    @Test
    fun testStickerCatalogInitialUnlock() {
        val firstSticker = StickerCatalog.initialStickers.first()
        assertTrue(firstSticker.unlocked)
        assertTrue(StickerCatalog.initialStickers.size >= 10)
    }

    @Test
    fun testOppositesCatalogHasContrasts() {
        assertTrue(com.example.data.OppositeCatalog.challenges.size >= 5)
        com.example.data.OppositeCatalog.challenges.forEach { item ->
            assertNotNull(item.promptText)
            assertNotNull(item.questionVoice)
            assertNotNull(item.successVoice)
            assertTrue(item.optionBigScale > item.optionSmallScale)
        }
    }

    @Test
    fun testPetTypesAvailable() {
        assertEquals(2, com.example.data.PetType.values().size)
    }

    @Test
    fun testPetToolsComplete() {
        val tools = com.example.data.PetTool.values()
        assertEquals(4, tools.size)
        assertTrue(tools.any { it == com.example.data.PetTool.COMIDA })
        assertTrue(tools.any { it == com.example.data.PetTool.PEINAR })
        assertTrue(tools.any { it == com.example.data.PetTool.BANIO })
        assertTrue(tools.any { it == com.example.data.PetTool.MIMOS })
    }

    @Test
    fun testVowelVariantsAndTwoPassesRound() {
        listOf("A", "E", "I", "O", "U").forEach { v ->
            val list = VowelCatalog.variants[v]
            assertNotNull(list)
            assertTrue(list!!.size >= 4)
        }

        val round = VowelCatalog.generateTwoPassesRound()
        assertEquals(10, round.size)
        val countMap = round.groupingBy { it.vowel }.eachCount()
        listOf("A", "E", "I", "O", "U").forEach { v ->
            assertEquals(2, countMap[v])
        }
    }
}
