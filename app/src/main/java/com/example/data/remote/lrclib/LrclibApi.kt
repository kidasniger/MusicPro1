package com.example.data.remote.lrclib

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class LrclibResponse(
    val id: Long? = null,
    val name: String? = null,
    @Json(name = "trackName")
    val trackName: String? = null,
    @Json(name = "artistName")
    val artistName: String? = null,
    @Json(name = "albumName")
    val albumName: String? = null,
    val duration: Double? = null,
    val instrumental: Boolean? = false,
    val plainLyrics: String? = null,
    val syncedLyrics: String? = null
)

interface LrclibApi {
    @GET("api/get")
    suspend fun getLyrics(
        @Query("track_name") trackName: String,
        @Query("artist_name") artistName: String,
        @Query("album_name") albumName: String? = null,
        @Query("duration") durationSeconds: Int? = null
    ): LrclibResponse

    @GET("api/search")
    suspend fun searchLyrics(
        @Query("q") query: String
    ): List<LrclibResponse>

    companion object {
        const val BASE_URL = "https://lrclib.net/"
    }
}
