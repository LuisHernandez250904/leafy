package com.example.leafy.model

data class ScanResult(
    val commonName: String? = null,
    val scientificName: String? = null,
    val confidence: Double? = null,
    val description: String? = null
)