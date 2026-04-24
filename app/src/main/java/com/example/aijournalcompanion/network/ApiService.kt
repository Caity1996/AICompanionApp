package com.example.aijournalcompanion.network

import retrofit2.http.Body
import retrofit2.http.POST

data class AnalyzeRequest(val text: String)
data class AnalyzeResponse(val emotion: String, val advice: String)

interface ApiService {
    @POST("analyze")
    suspend fun analyzeEntry(@Body request: AnalyzeRequest): AnalyzeResponse
}
