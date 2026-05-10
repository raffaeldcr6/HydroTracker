package com.example.hydrotracker.data.repository

import com.example.hydrotracker.data.api.RetrofitClient
import com.example.hydrotracker.data.model.WaterIntake

class WaterRepository {
    suspend fun getWaterIntakes(): List<WaterIntake> {
        return try {
            RetrofitClient.instance.getWaterIntakes()
        } catch (e: Exception) {
            emptyList()
        }
    }
}