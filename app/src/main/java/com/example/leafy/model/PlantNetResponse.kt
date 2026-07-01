package com.example.leafy.model

data class PlantNetResponse(
    val results: List<PlantNetResult> = emptyList()
)

// PlantNet suele devolver score como número (double)
data class PlantNetResult(
    val score: Double = 0.0,
    val species: Species = Species()
)

/**
 * OJO: aquí añadimos family y genus como objetos anidados.
 * Si por alguna razón no vienen en la respuesta, quedan en null.
 */
data class Species(
    val scientificName: String = "",
    val commonNames: List<String> = emptyList(),
    val family: Taxon? = null,
    val genus: Taxon? = null
)

data class Taxon(
    val scientificName: String = ""
)