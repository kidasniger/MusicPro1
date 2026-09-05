package com.example.data.local.preferences

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "musicpro_user_preferences")

interface UserPreferencesRepository {
    val isOnboardingCompleted: Flow<Boolean>
    val groqApiKey: Flow<String>
    val groqModel: Flow<String>
    val themeMode: Flow<String>
    val audioQuality: Flow<String>
    val gaplessPlayback: Flow<Boolean>
    val crossfadeSeconds: Flow<Int>
    val autoDownloadLyrics: Flow<Boolean>
    val lyricsCacheCount: Flow<Int>

    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun setGroqApiKey(apiKey: String)
    suspend fun setGroqModel(model: String)
    suspend fun setThemeMode(mode: String)
    suspend fun setAudioQuality(quality: String)
    suspend fun setGaplessPlayback(enabled: Boolean)
    suspend fun setCrossfadeSeconds(seconds: Int)
    suspend fun setAutoDownloadLyrics(enabled: Boolean)
    suspend fun clearLyricsCache()
}

class DefaultUserPreferencesRepository(
    private val context: Context
) : UserPreferencesRepository {

    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val GROQ_API_KEY_ENCRYPTED = stringPreferencesKey("groq_api_key_encrypted")
        val GROQ_MODEL = stringPreferencesKey("groq_model")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val AUDIO_QUALITY = stringPreferencesKey("audio_quality")
        val GAPLESS_PLAYBACK = booleanPreferencesKey("gapless_playback")
        val CROSSFADE_SECONDS = androidx.datastore.preferences.core.intPreferencesKey("crossfade_seconds")
        val AUTO_DOWNLOAD_LYRICS = booleanPreferencesKey("auto_download_lyrics")
        val LYRICS_CACHE_COUNT = androidx.datastore.preferences.core.intPreferencesKey("lyrics_cache_count")
    }

    override val isOnboardingCompleted: Flow<Boolean> = context.userDataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
        }

    // Récupération de la clé déchiffrée
    override val groqApiKey: Flow<String> = context.userDataStore.data
        .map { preferences ->
            val encrypted = preferences[PreferencesKeys.GROQ_API_KEY_ENCRYPTED] ?: ""
            if (encrypted.isNotEmpty()) {
                try {
                    // Décodage Base64 avec masque XOR
                    val decoded = Base64.decode(encrypted, Base64.DEFAULT)
                    val xorKey = "MusicProGroqKeySalt2026".toByteArray()
                    val result = ByteArray(decoded.size)
                    for (i in decoded.indices) {
                        result[i] = (decoded[i].toInt() xor xorKey[i % xorKey.size].toInt()).toByte()
                    }
                    String(result)
                } catch (e: Exception) {
                    ""
                }
            } else ""
        }

    override val groqModel: Flow<String> = context.userDataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.GROQ_MODEL] ?: "whisper-large-v3"
        }

    override val themeMode: Flow<String> = context.userDataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.THEME_MODE] ?: "Dark Cyberpunk"
        }

    override val audioQuality: Flow<String> = context.userDataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.AUDIO_QUALITY] ?: "Hi-Res Lossless (FLAC 24-bit)"
        }

    override val gaplessPlayback: Flow<Boolean> = context.userDataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.GAPLESS_PLAYBACK] ?: true
        }

    override val crossfadeSeconds: Flow<Int> = context.userDataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.CROSSFADE_SECONDS] ?: 3
        }

    override val autoDownloadLyrics: Flow<Boolean> = context.userDataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.AUTO_DOWNLOAD_LYRICS] ?: true
        }

    override val lyricsCacheCount: Flow<Int> = context.userDataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.LYRICS_CACHE_COUNT] ?: 18
        }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    // Stockage chiffré dans le DataStore
    override suspend fun setGroqApiKey(apiKey: String) {
        context.userDataStore.edit { preferences ->
            if (apiKey.isEmpty()) {
                preferences[PreferencesKeys.GROQ_API_KEY_ENCRYPTED] = ""
            } else {
                val xorKey = "MusicProGroqKeySalt2026".toByteArray()
                val bytes = apiKey.toByteArray()
                val masked = ByteArray(bytes.size)
                for (i in bytes.indices) {
                    masked[i] = (bytes[i].toInt() xor xorKey[i % xorKey.size].toInt()).toByte()
                }
                val encoded = Base64.encodeToString(masked, Base64.NO_WRAP)
                preferences[PreferencesKeys.GROQ_API_KEY_ENCRYPTED] = encoded
            }
        }
    }

    override suspend fun setGroqModel(model: String) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.GROQ_MODEL] = model
        }
    }

    override suspend fun setThemeMode(mode: String) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode
        }
    }

    override suspend fun setAudioQuality(quality: String) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.AUDIO_QUALITY] = quality
        }
    }

    override suspend fun setGaplessPlayback(enabled: Boolean) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.GAPLESS_PLAYBACK] = enabled
        }
    }

    override suspend fun setCrossfadeSeconds(seconds: Int) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.CROSSFADE_SECONDS] = seconds
        }
    }

    override suspend fun setAutoDownloadLyrics(enabled: Boolean) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_DOWNLOAD_LYRICS] = enabled
        }
    }

    override suspend fun clearLyricsCache() {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.LYRICS_CACHE_COUNT] = 0
        }
    }
}
