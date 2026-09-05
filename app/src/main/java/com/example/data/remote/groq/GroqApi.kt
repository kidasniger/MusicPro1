package com.example.data.remote.groq

import com.squareup.moshi.JsonClass
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

@JsonClass(generateAdapter = true)
data class GroqTranscriptionResponse(
    val text: String? = null,
    val segments: List<GroqSegment>? = null
)

@JsonClass(generateAdapter = true)
data class GroqSegment(
    val id: Int? = null,
    val start: Double? = null,
    val end: Double? = null,
    val text: String? = null
)

interface GroqApi {
    @Multipart
    @POST("openai/v1/audio/transcriptions")
    suspend fun transcribeAudio(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("model") model: RequestBody,
        @Part("response_format") responseFormat: RequestBody? = null,
        @Part("language") language: RequestBody? = null
    ): GroqTranscriptionResponse

    companion object {
        const val BASE_URL = "https://api.groq.com/"
    }
}
