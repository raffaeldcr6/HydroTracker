package com.example.hydrotracker.network

import com.example.hydrotracker.model.WaterIntake
import retrofit2.http.GET

interface ApiService {
    @GET("water_intake.json")
    suspend fun getWaterIntakes(): List<WaterIntake>
}