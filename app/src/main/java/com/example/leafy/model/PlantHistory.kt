package com.example.leafy.model

data class PlantHistory(
    val id: String = "",
    val userId: String = "",
    val plantName: String = "",
    val commonName: String = "",
    val familyName: String = "",
    val confidence: Double = 0.0,
    val description: String = "",
    val imageUrl: String = "",
    val scanDate: Long = 0L
)