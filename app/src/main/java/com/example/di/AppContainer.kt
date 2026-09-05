package com.example.di

import android.content.Context
import com.example.data.local.MusicDatabase
import com.example.data.local.preferences.DefaultUserPreferencesRepository
import com.example.data.local.preferences.UserPreferencesRepository
import com.example.data.remote.groq.GroqApi
import com.example.data.remote.lrclib.LrclibApi
import com.example.data.repository.DefaultLyricsRepository
import com.example.data.repository.DefaultMusicRepository
import com.example.data.repository.LyricsRepository
import com.example.data.repository.MusicRepository
import com.example.player.MusicPlayerManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.example.data.local.MediaStoreAudioScanner
import com.example.util.NetworkMonitor
import java.util.concurrent.TimeUnit

interface AppContainer {
    val database: MusicDatabase
    val musicRepository: MusicRepository
    val lyricsRepository: LyricsRepository
    val playerManager: MusicPlayerManager
    val userPreferencesRepository: UserPreferencesRepository
    val networkMonitor: NetworkMonitor
    val mediaStoreScanner: MediaStoreAudioScanner
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "MusicPro/1.0 (Android; musicpro@example.com)")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
    }

    private val lrclibRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(LrclibApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private val groqRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(GroqApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private val lrclibApi: LrclibApi by lazy {
        lrclibRetrofit.create(LrclibApi::class.java)
    }

    private val groqApi: GroqApi by lazy {
        groqRetrofit.create(GroqApi::class.java)
    }

    override val database: MusicDatabase by lazy {
        MusicDatabase.getDatabase(context)
    }

    override val musicRepository: MusicRepository by lazy {
        DefaultMusicRepository(database)
    }

    override val lyricsRepository: LyricsRepository by lazy {
        DefaultLyricsRepository(lrclibApi, groqApi)
    }

    override val playerManager: MusicPlayerManager by lazy {
        MusicPlayerManager(context)
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        DefaultUserPreferencesRepository(context)
    }

    override val networkMonitor: NetworkMonitor by lazy {
        NetworkMonitor(context)
    }

    override val mediaStoreScanner: MediaStoreAudioScanner by lazy {
        MediaStoreAudioScanner(context)
    }
}
