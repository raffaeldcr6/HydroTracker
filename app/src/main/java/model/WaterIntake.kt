package com.example.hydrotracker.model

import androidx.annotation.DrawableRes

data class WaterIntake(
    val title: String,
    val description: String,
    val amountMl: Int,
    @DrawableRes val imageRes: Int
)