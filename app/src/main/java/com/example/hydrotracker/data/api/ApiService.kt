package com.example.hydrotracker.data.api

import com.example.hydrotracker.data.model.WaterIntake
import retrofit2.http.GET

interface ApiService {
    @GET("water_intake.json")
    suspend fun getWaterIntakes(): List<WaterIntake>
}