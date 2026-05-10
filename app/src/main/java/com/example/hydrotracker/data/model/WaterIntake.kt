package com.example.hydrotracker.data.model

import com.google.gson.annotations.SerializedName

data class WaterIntake(
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("amount_ml")
    val amountMl: Int,
    @SerializedName("image_url")
    val imageUrl: String
)