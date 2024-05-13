package com.arc.cinexpert.chat

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIService {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer OPENAI_API_KEY"
    )
    @POST("v1/completions")
    fun getCompletion(@Body request: ChatRequest): Call<ChatResponse>
}
