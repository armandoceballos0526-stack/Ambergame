package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.R

enum class DifficultyLevel(val label: String, val maxNumber: Int, val memoryPairs: Int) {
    PRINCIPIANTE("Principiante (1 al 3)", 3, 2),
    INTERMEDIO("Intermedio (1 al 5)", 5, 3),
    AVANZADO("Avanzado (1 al 10)", 10, 4)
}

enum class ScreenRoute {
    HOME,
    NUMBERS,
    VOWELS,
    SHAPES,
    MEMORY,
    BUBBLES,
    MAGIC_PAINT,
    XYLOPHONE,
    STICKER_ALBUM,
    PET_CARE,
    SIZES_OPPOSITES,
    PARENT_DASHBOARD
}

data class Animal(
    val id: String,
    val name: String,
    val simpleName: String,
    val pluralName: String,
    val emoji: String,
    val soundLabel: String,
    val color: Color,
    val drawableResId: Int? = null
)

object AnimalCatalog {
    val animals = listOf(
        Animal("conejito", "Conejito", "conejito", "conejitos", "🐰", "¡Boing boing!", Color(0xFFEA80FC), R.drawable.img_animal_bunny),
        Animal("perrito", "Perrito", "perrito", "perritos", "🐶", "¡Guau guau!", Color(0xFFFFD180), R.drawable.img_animal_puppy),
        Animal("osito", "Osito", "osito", "ositos", "🐻", "¡Grrr cariñoso!", Color(0xFFFFB74D), R.drawable.img_animal_bear),
        Animal("leoncito", "Leoncito", "leoncito", "leoncitos", "🦁", "¡Roar alegre!", Color(0xFFFFE082), R.drawable.img_animal_lion),
        Animal("ranita", "Ranita", "ranita", "ranitas", "🐸", "¡Croac croac!", Color(0xFFB9F6CA), R.drawable.img_animal_frog),
        Animal("gatito", "Gatito", "gatito", "gatitos", "🐱", "¡Miau miau!", Color(0xFFFF8A80), R.drawable.img_pet_kitten),
        Animal("unicornio", "Unicornio", "unicornio", "unicornios", "🦄", "¡Mágico!", Color(0xFFFF80AB), R.drawable.img_pet_unicorn),
        Animal("panda", "Panda", "panda", "pandas", "🐼", "¡Ñam ñam!", Color(0xFF80D8FF), R.drawable.img_animal_bear),
        Animal("patito", "Patito", "patito", "patitos", "🦆", "¡Cua cua!", Color(0xFFFFFF8D), R.drawable.ic_animal_duck),
        Animal("mariposa", "Mariposa", "mariposa", "mariposas", "🦋", "¡Aleteo!", Color(0xFFB388FF), R.drawable.ic_animal_butterfly)
    )
}

data class VowelItem(
    val vowel: String,
    val word: String,
    val emoji: String,
    val color: Color
)

object VowelCatalog {
    val vowels = listOf(
        VowelItem("A", "Abeja", "🐝", Color(0xFFFF5252)),
        VowelItem("E", "Elefante", "🐘", Color(0xFF448AFF)),
        VowelItem("I", "Iguana", "🦎", Color(0xFF00E676)),
        VowelItem("O", "Oso", "🐻", Color(0xFFFFAB00)),
        VowelItem("U", "Unicornio", "🦄", Color(0xFFE040FB))
    )

    val variants: Map<String, List<VowelItem>> = mapOf(
        "A" to listOf(
            VowelItem("A", "Abeja", "🐝", Color(0xFFFF5252)),
            VowelItem("A", "Avión", "✈️", Color(0xFFFF1744)),
            VowelItem("A", "Árbol", "🌳", Color(0xFF00C853)),
            VowelItem("A", "Arcoíris", "🌈", Color(0xFFFF4081)),
            VowelItem("A", "Ardilla", "🐿️", Color(0xFFFF8F00)),
            VowelItem("A", "Anillo", "💍", Color(0xFFFFD600))
        ),
        "E" to listOf(
            VowelItem("E", "Elefante", "🐘", Color(0xFF448AFF)),
            VowelItem("E", "Estrella", "⭐", Color(0xFFFFD600)),
            VowelItem("E", "Escalera", "🪜", Color(0xFF8D6E63)),
            VowelItem("E", "Erizo", "🦔", Color(0xFF795548)),
            VowelItem("E", "Espejo", "🪞", Color(0xFF00B0FF)),
            VowelItem("E", "Escoba", "🧹", Color(0xFFFF9800))
        ),
        "I" to listOf(
            VowelItem("I", "Iguana", "🦎", Color(0xFF00E676)),
            VowelItem("I", "Isla", "🏝️", Color(0xFF00B0FF)),
            VowelItem("I", "Iglú", "🧊", Color(0xFF80D8FF)),
            VowelItem("I", "Imán", "🧲", Color(0xFFFF1744)),
            VowelItem("I", "Insecto", "🐞", Color(0xFFE91E63)),
            VowelItem("I", "Instrumento", "🎺", Color(0xFFFFD600))
        ),
        "O" to listOf(
            VowelItem("O", "Oso", "🐻", Color(0xFFFFAB00)),
            VowelItem("O", "Ojo", "👁️", Color(0xFF29B6F6)),
            VowelItem("O", "Oruga", "🐛", Color(0xFF76FF03)),
            VowelItem("O", "Oveja", "🐑", Color(0xFFB0BEC5)),
            VowelItem("O", "Ola", "🌊", Color(0xFF0091EA)),
            VowelItem("O", "Olla", "🍲", Color(0xFFFF7043))
        ),
        "U" to listOf(
            VowelItem("U", "Unicornio", "🦄", Color(0xFFE040FB)),
            VowelItem("U", "Uvas", "🍇", Color(0xFF7B1FA2)),
            VowelItem("U", "Uña", "💅", Color(0xFFFF80AB)),
            VowelItem("U", "Uno", "1️⃣", Color(0xFF00E5FF)),
            VowelItem("U", "Universo", "🪐", Color(0xFF3F51B5)),
            VowelItem("U", "Urna", "🗳️", Color(0xFF9E9E9E))
        )
    )

    /**
     * Generates a 10-question sequence where each of the 5 vowels (A, E, I, O, U)
     * appears exactly 2 times, each time with a different word variant.
     */
    fun generateTwoPassesRound(): List<VowelItem> {
        val pass1 = vowels.map { base ->
            val pool = variants[base.vowel] ?: listOf(base)
            pool.random()
        }.shuffled()

        val pass2 = vowels.map { base ->
            val pool = variants[base.vowel] ?: listOf(base)
            val usedInPass1 = pass1.firstOrNull { it.vowel == base.vowel }
            val unused = pool.filter { it.word != usedInPass1?.word }
            if (unused.isNotEmpty()) unused.random() else pool.random()
        }.shuffled()

        return pass1 + pass2
    }
}

enum class ShapeType(val spanishName: String) {
    CIRCULO("Círculo"),
    CUADRADO("Cuadrado"),
    TRIANGULO("Triángulo"),
    ESTRELLA("Estrella"),
    CORAZON("Corazón")
}

data class ShapeColorItem(
    val shape: ShapeType,
    val colorName: String,
    val color: Color
)

object ShapeCatalog {
    val sampleShapes = listOf(
        ShapeColorItem(ShapeType.CORAZON, "Rosa", Color(0xFFFF4081)),
        ShapeColorItem(ShapeType.ESTRELLA, "Amarillo", Color(0xFFFFD600)),
        ShapeColorItem(ShapeType.CIRCULO, "Azul", Color(0xFF00B0FF)),
        ShapeColorItem(ShapeType.CUADRADO, "Verde", Color(0xFF00E676)),
        ShapeColorItem(ShapeType.TRIANGULO, "Morado", Color(0xFFAA00FF)),
        ShapeColorItem(ShapeType.CORAZON, "Rojo", Color(0xFFFF1744)),
        ShapeColorItem(ShapeType.ESTRELLA, "Naranja", Color(0xFFFF9100)),
        ShapeColorItem(ShapeType.CIRCULO, "Amarillo", Color(0xFFFFEA00))
    )
}

data class Sticker(
    val id: Int,
    val name: String,
    val emoji: String,
    val starsRequired: Int,
    val unlocked: Boolean = false,
    val posX: Float = 0.5f,
    val posY: Float = 0.5f
)

object StickerCatalog {
    val initialStickers = listOf(
        Sticker(1, "Unicornio Radiante", "🦄", 1, true, 0.25f, 0.3f),
        Sticker(2, "Estrella Dorada", "⭐", 3, false, 0.7f, 0.2f),
        Sticker(3, "Gatito Princesa", "👑🐱", 6, false, 0.35f, 0.6f),
        Sticker(4, "Perrito Astronauta", "🐶🚀", 9, false, 0.75f, 0.55f),
        Sticker(5, "Arcoíris Mágico", "🌈", 12, false, 0.5f, 0.25f),
        Sticker(6, "Helado Multicolor", "🍦", 15, false, 0.2f, 0.75f),
        Sticker(7, "Bebé Panda Feliz", "🐼🎋", 18, false, 0.65f, 0.8f),
        Sticker(8, "Sirena Marina", "🧜‍♀️🐠", 22, false, 0.82f, 0.35f),
        Sticker(9, "Mariposa Brillante", "🦋✨", 26, false, 0.15f, 0.45f),
        Sticker(10, "Flor Encantada", "🌸💖", 30, false, 0.48f, 0.72f),
        Sticker(11, "Pastel de Cumple", "🎂🎈", 35, false, 0.38f, 0.42f),
        Sticker(12, "Trofeo de Campeona", "🏆🌟", 40, false, 0.55f, 0.52f)
    )
}

enum class PetType(val petName: String, val emoji: String, val bgEmoji: String) {
    UNICORNIO("Unicornio Bebé", "🦄", "🌈"),
    GATITO("Gatito Tierno", "🐱", "🐾")
}

enum class PetTool(val label: String, val emoji: String) {
    COMIDA("Alimentar", "🍓"),
    PEINAR("Peinar", "🪮"),
    BANIO("Bañar", "🫧"),
    MIMOS("Acariciar", "💖")
}

data class OppositeItem(
    val id: String,
    val promptText: String,
    val questionVoice: String,
    val successVoice: String,
    val emoji: String,
    val optionSmallScale: Float = 0.55f,
    val optionBigScale: Float = 1.15f,
    val targetIsBig: Boolean = true // true = find big, false = find small
)

object OppositeCatalog {
    val challenges = listOf(
        OppositeItem("panda_big", "Toca el panda más GRANDE", "¿Amber, cuál es el panda más grande?", "¡Excelente Amber! ¡Ese es el panda grande!", "🐼", 0.5f, 1.15f, true),
        OppositeItem("gatito_small", "Toca el gatito más PEQUEÑO", "¿Amber, cuál es el gatito más pequeño?", "¡Genial Amber! ¡Ese es el gatito pequeño!", "🐱", 0.5f, 1.15f, false),
        OppositeItem("estrella_big", "Toca la estrella más GRANDE", "¿Amber, dónde está la estrella más grande?", "¡Muy bien Amber! ¡La gran estrella brillante!", "⭐", 0.5f, 1.15f, true),
        OppositeItem("conejito_small", "Toca el conejito más PEQUEÑO", "¿Amber, cuál es el conejito más pequeño?", "¡Bravo Amber! ¡El conejito chiquitito!", "🐰", 0.5f, 1.15f, false),
        OppositeItem("corazon_big", "Toca el corazón más GRANDE", "¿Amber, dónde está el corazón más grande?", "¡Increíble Amber! ¡El corazón gigante!", "💖", 0.5f, 1.15f, true),
        OppositeItem("patito_small", "Toca el patito más PEQUEÑO", "¿Amber, cuál es el patito más pequeño?", "¡Fantástico Amber! ¡El patito pequeñito!", "🦆", 0.5f, 1.15f, false),
        OppositeItem("manzana_big", "Toca la manzana más GRANDE", "¿Amber, cuál es la manzana más grande?", "¡Súper Amber! ¡La manzana grande!", "🍎", 0.5f, 1.15f, true),
        OppositeItem("perrito_small", "Toca el perrito más PEQUEÑO", "¿Amber, cuál es el perrito más pequeño?", "¡Muy bien Amber! ¡El perrito bebé!", "🐶", 0.5f, 1.15f, false)
    )
}
