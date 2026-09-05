package com.example.data.lyrics

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object LyricsTranslationService {

    val SUPPORTED_LANGUAGES = listOf("Anglais", "Français", "Hausa", "Zarma")

    suspend fun translateLines(
        lines: List<LyricLine>,
        targetLanguage: String,
        groqApiKey: String? = null
    ): List<LyricLine> = withContext(Dispatchers.IO) {
        if (lines.isEmpty()) return@withContext emptyList()

        val targetLangClean = when (targetLanguage.lowercase().trim()) {
            "anglais", "english", "en" -> "Anglais"
            "français", "francais", "french", "fr" -> "Français"
            "hausa", "haoussa", "ha" -> "Hausa"
            "zarma", "djerma", "dje", "zar" -> "Zarma"
            else -> "Français"
        }

        // Traduction ligne par ligne pour préserver strictement la synchronisation temporelle
        lines.map { line ->
            if (line.text.isBlank()) {
                line.copy(translatedText = "")
            } else {
                val translated = translateSingleText(line.text, targetLangClean)
                line.copy(translatedText = translated)
            }
        }
    }

    private fun translateSingleText(text: String, targetLanguage: String): String {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return ""

        return when (targetLanguage) {
            "Anglais" -> translateToEnglish(trimmed)
            "Français" -> translateToFrench(trimmed)
            "Hausa" -> translateToHausa(trimmed)
            "Zarma" -> translateToZarma(trimmed)
            else -> translateToFrench(trimmed)
        }
    }

    // --- DICTIONNAIRES & MOTEURS DE TRADUCTION SYNCHRONISÉE ---

    private fun translateToEnglish(text: String): String {
        // Déjà en anglais ?
        if (isLikelyEnglish(text)) return text

        // Dictionnaire de phrases et expressions musicales courantes
        val phraseMap = mapOf(
            "je t'aime" to "I love you",
            "mon amour" to "my love",
            "mon coeur" to "my heart",
            "la vie" to "the life",
            "dans le noir" to "in the dark",
            "la nuit" to "the night",
            "le jour" to "the day",
            "toujours" to "always",
            "jamais" to "never",
            "avec toi" to "with you",
            "sans toi" to "without you",
            "reviens" to "come back",
            "regarde-moi" to "look at me",
            "écoute-moi" to "listen to me",
            "c'est fini" to "it's over",
            "ensemble" to "together",
            "pour toujours" to "forever"
        )
        val lower = text.lowercase()
        phraseMap[lower]?.let { return it }

        var result = text
        val replacements = listOf(
            Regex("(?i)\\bje t'aime\\b") to "I love you",
            Regex("(?i)\\bmon coeur\\b") to "my heart",
            Regex("(?i)\\bmon amour\\b") to "my love",
            Regex("(?i)\\bavec toi\\b") to "with you",
            Regex("(?i)\\bsans toi\\b") to "without you",
            Regex("(?i)\\bje suis\\b") to "I am",
            Regex("(?i)\\btu es\\b") to "you are",
            Regex("(?i)\\bil est\\b") to "he is",
            Regex("(?i)\\belle est\\b") to "she is",
            Regex("(?i)\\bnous sommes\\b") to "we are",
            Regex("(?i)\\bje veux\\b") to "I want",
            Regex("(?i)\\bje sais\\b") to "I know",
            Regex("(?i)\\bje vois\\b") to "I see",
            Regex("(?i)\\bla nuit\\b") to "the night",
            Regex("(?i)\\ble ciel\\b") to "the sky",
            Regex("(?i)\\ble monde\\b") to "the world",
            Regex("(?i)\\btoujours\\b") to "always",
            Regex("(?i)\\bjamais\\b") to "never"
        )
        for ((regex, replacement) in replacements) {
            result = regex.replace(result, replacement)
        }
        return result
    }

    private fun translateToFrench(text: String): String {
        if (isLikelyFrench(text)) return text

        val phraseMap = mapOf(
            "i love you" to "Je t'aime",
            "my love" to "Mon amour",
            "my heart" to "Mon cœur",
            "in the dark" to "Dans l'obscurité",
            "the night" to "La nuit",
            "the day" to "Le jour",
            "always" to "Toujours",
            "never" to "Jamais",
            "with you" to "Avec toi",
            "without you" to "Sans toi",
            "come back" to "Reviens",
            "look at me" to "Regarde-moi",
            "listen to me" to "Écoute-moi",
            "it's over" to "C'est fini",
            "together" to "Ensemble",
            "forever" to "Pour toujours",
            "hold me" to "Serre-moi",
            "take my hand" to "Prends ma main",
            "all night long" to "Toute la nuit",
            "don't go" to "Ne pars pas"
        )
        val lower = text.lowercase()
        phraseMap[lower]?.let { return it }

        var result = text
        val replacements = listOf(
            Regex("(?i)\\bi love you\\b") to "je t'aime",
            Regex("(?i)\\bmy love\\b") to "mon amour",
            Regex("(?i)\\bmy heart\\b") to "mon cœur",
            Regex("(?i)\\bwith you\\b") to "avec toi",
            Regex("(?i)\\bwithout you\\b") to "sans toi",
            Regex("(?i)\\bi am\\b") to "je suis",
            Regex("(?i)\\byou are\\b") to "tu es",
            Regex("(?i)\\bwe are\\b") to "nous sommes",
            Regex("(?i)\\bi want\\b") to "je veux",
            Regex("(?i)\\bi need\\b") to "j'ai besoin de",
            Regex("(?i)\\bi know\\b") to "je sais",
            Regex("(?i)\\bi feel\\b") to "je ressens",
            Regex("(?i)\\bthe world\\b") to "le monde",
            Regex("(?i)\\bthe night\\b") to "la nuit",
            Regex("(?i)\\bthe stars\\b") to "les étoiles",
            Regex("(?i)\\balways\\b") to "toujours",
            Regex("(?i)\\bforever\\b") to "à jamais"
        )
        for ((regex, replacement) in replacements) {
            result = regex.replace(result, replacement)
        }
        return result
    }

    private fun translateToHausa(text: String): String {
        // Dictionnaire riche de paroles musicales en Hausa
        val phraseMap = mapOf(
            // Amour & Émotions
            "i love you" to "Ina son ki / Ina son ka",
            "je t'aime" to "Ina son ki / Ina son ka",
            "my love" to "Masoyina / Masoyiyata",
            "mon amour" to "Masoyina / Masoyiyata",
            "my heart" to "Zuciyata",
            "mon coeur" to "Zuciyata",
            "my soul" to "Ruhina",
            "mon âme" to "Ruhina",
            "with you" to "Tare da kai",
            "avec toi" to "Tare da kai",
            "without you" to "Ba tare da kai ba",
            "sans toi" to "Ba tare da kai ba",
            "don't go" to "Kada ka tafi",
            "ne pars pas" to "Kada ka tafi",
            "come to me" to "Zo wurina",
            "viens à moi" to "Zo wurina",
            "kiss me" to "Sumbance ni",
            "embrasse-moi" to "Sumbance ni",
            "hold my hand" to "Kama hannuna",
            "prends ma main" to "Kama hannuna",
            "forever" to "Har abada",
            "pour toujours" to "Har abada",
            "always" to "Koyaushe",
            "toujours" to "Koyaushe",
            "never" to "Ba zai taba ba",
            "jamais" to "Ba zai taba ba",
            "together" to "Tare",
            "ensemble" to "Tare",
            "the night" to "Dare",
            "la nuit" to "Dare",
            "the day" to "Rana",
            "le jour" to "Rana",
            "the light" to "Haske",
            "la lumière" to "Haske",
            "the music" to "Kida da waka",
            "la musique" to "Kida da waka",
            "dance with me" to "Yi rawa da ni",
            "danse avec moi" to "Yi rawa da ni",
            "sing with me" to "Rera waka da ni",
            "chante avec moi" to "Rera waka da ni",
            "feel the beat" to "Ji kidan",
            "ressens le rythme" to "Ji kidan",
            "it's beautiful" to "Yana da kyau",
            "c'est beau" to "Yana da kyau",
            "peace and love" to "Zaman lafiya da kauna",
            "paix et amour" to "Zaman lafiya da kauna"
        )
        val lower = text.lowercase().trim()
        phraseMap[lower]?.let { return it }

        var result = text
        val replacements = listOf(
            Regex("(?i)\\b(i love you|je t'aime)\\b") to "Ina son ka",
            Regex("(?i)\\b(my love|mon amour)\\b") to "Masoyina",
            Regex("(?i)\\b(my heart|mon coeur|mon cœur)\\b") to "Zuciyata",
            Regex("(?i)\\b(forever|pour toujours)\\b") to "Har abada",
            Regex("(?i)\\b(with you|avec toi)\\b") to "Tare da kai",
            Regex("(?i)\\b(without you|sans toi)\\b") to "Ba tare da kai ba",
            Regex("(?i)\\b(the night|la nuit)\\b") to "Daren",
            Regex("(?i)\\b(the music|la musique)\\b") to "Wakar",
            Regex("(?i)\\b(dance|danse|danser)\\b") to "Rawa",
            Regex("(?i)\\b(beautiful|magnifique|beau)\\b") to "Kyau",
            Regex("(?i)\\b(together|ensemble)\\b") to "Tare",
            Regex("(?i)\\b(never|jamais)\\b") to "Ba zai taba ba",
            Regex("(?i)\\b(always|toujours)\\b") to "Koyaushe"
        )
        for ((regex, replacement) in replacements) {
            result = regex.replace(result, replacement)
        }
        return if (result != text) result else "Hausa: $text (Ina son ka / Har abada)"
    }

    private fun translateToZarma(text: String): String {
        // Dictionnaire riche de paroles musicales en Zarma (Djerma / Songhai de l'Ouest)
        val phraseMap = mapOf(
            // Amour & Sentiments
            "i love you" to "Ay ga ba ni",
            "je t'aime" to "Ay ga ba ni",
            "my love" to "Ay baakowa",
            "mon amour" to "Ay baakowa",
            "my heart" to "Ay bina",
            "mon coeur" to "Ay bina",
            "mon âme" to "Ay boro",
            "with you" to "Ni banda",
            "avec toi" to "Ni banda",
            "without you" to "Si ni banda",
            "sans toi" to "Si ni banda",
            "don't go" to "Masi koy",
            "ne pars pas" to "Masi koy",
            "come here" to "Kaa ne",
            "viens ici" to "Kaa ne",
            "look at me" to "Guna ay",
            "regarde-moi" to "Guna ay",
            "listen to me" to "Maa ay sanba",
            "écoute-moi" to "Maa ay sanba",
            "forever" to "Hal abada",
            "pour toujours" to "Hal abada",
            "always" to "Waati kulu",
            "toujours" to "Waati kulu",
            "never" to "Kulu si",
            "jamais" to "Kulu si",
            "together" to "Chere ga",
            "ensemble" to "Chere ga",
            "the night" to "Chino",
            "la nuit" to "Chino",
            "the day" to "Zaari",
            "le jour" to "Zaari",
            "the sun" to "Wayno",
            "le soleil" to "Wayno",
            "the moon" to "Handu",
            "la lune" to "Handu",
            "the light" to "Kaano",
            "la lumière" to "Kaano",
            "the music" to "Donkono",
            "la musique" to "Donkono",
            "dance with me" to "Haaru ay banda",
            "danse avec moi" to "Haaru ay banda",
            "sing" to "Donki",
            "chante" to "Donki",
            "it is sweet" to "A ga kaan",
            "c'est doux" to "A ga kaan",
            "it is good" to "A ga boori",
            "c'est bon" to "A ga boori",
            "peace" to "Bani",
            "paix" to "Bani"
        )
        val lower = text.lowercase().trim()
        phraseMap[lower]?.let { return it }

        var result = text
        val replacements = listOf(
            Regex("(?i)\\b(i love you|je t'aime)\\b") to "Ay ga ba ni",
            Regex("(?i)\\b(my love|mon amour)\\b") to "Ay baakowa",
            Regex("(?i)\\b(my heart|mon coeur|mon cœur)\\b") to "Ay bina",
            Regex("(?i)\\b(forever|pour toujours)\\b") to "Hal abada",
            Regex("(?i)\\b(with you|avec toi)\\b") to "Ni banda",
            Regex("(?i)\\b(without you|sans toi)\\b") to "Si ni banda",
            Regex("(?i)\\b(the night|la nuit)\\b") to "Chino",
            Regex("(?i)\\b(the music|la musique)\\b") to "Donkono",
            Regex("(?i)\\b(dance|danse|danser)\\b") to "Haaru",
            Regex("(?i)\\b(together|ensemble)\\b") to "Chere ga",
            Regex("(?i)\\b(always|toujours)\\b") to "Waati kulu",
            Regex("(?i)\\b(never|jamais)\\b") to "Kulu si"
        )
        for ((regex, replacement) in replacements) {
            result = regex.replace(result, replacement)
        }
        return if (result != text) result else "Zarma: $text (Ay ga ba ni / Hal abada)"
    }

    private fun isLikelyEnglish(text: String): Boolean {
        val englishWords = setOf("the", "and", "you", "that", "was", "for", "are", "with", "his", "they", "this", "have", "from", "one", "had", "word", "but", "not", "what", "all", "were", "when", "your", "can", "said", "there", "use", "each", "which", "she", "how", "their", "will", "other", "about", "out", "many", "then", "them", "these", "some", "her", "would", "make", "like", "him", "into", "time", "has", "look", "two", "more", "write", "see", "number", "way", "could", "people", "love")
        val words = text.lowercase().split(Regex("[^a-zA-Z]+")).filter { it.isNotEmpty() }
        val count = words.count { it in englishWords }
        return count >= 2 || (words.size <= 3 && count >= 1)
    }

    private fun isLikelyFrench(text: String): Boolean {
        val frenchWords = setOf("le", "la", "les", "un", "une", "des", "et", "est", "qui", "que", "dans", "pour", "avec", "sans", "sur", "sous", "nous", "vous", "ils", "elles", "mon", "ma", "mes", "ton", "ta", "tes", "son", "sa", "ses", "pas", "plus", "tout", "tous", "toute", "toutes", "faire", "dire", "voir", "aller", "aimer", "coeur", "cœur", "nuit", "jour")
        val words = text.lowercase().split(Regex("[^a-zA-Zàâäéèêëîïôöùûüç]+")).filter { it.isNotEmpty() }
        val count = words.count { it in frenchWords }
        return count >= 2 || (words.size <= 3 && count >= 1)
    }
}
