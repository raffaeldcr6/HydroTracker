package com.example.hydrotracker.model

import com.example.hydrotracker.R

object WaterDataSource {
    val dummyWater = listOf(
        WaterIntake("Custom Intake", "Input manual", 0, R.drawable.air_0),
        WaterIntake("Small Glass", "250 ml", 250, R.drawable.air_25),
        WaterIntake("Medium Glass", "500 ml", 500, R.drawable.air_50),
        WaterIntake("Large Glass", "750 ml", 750, R.drawable.air_75),
        WaterIntake("Full Bottle", "1000 ml", 1000, R.drawable.air_100)
    )
}