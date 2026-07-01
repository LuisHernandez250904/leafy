package com.example.leafy.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leafy.api.RetrofitInstance
import com.example.leafy.firebase.FirestoreRepository
import com.example.leafy.model.PlantHistory
import com.example.leafy.util.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File

class PlantScanViewModel(
    private val firestoreRepo: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    fun analyzeAndSave(
        imageFile: File,     // Archivo temporal solo para PlantNet
        displayUri: Uri,     // URI PERSISTENTE (galería)
        onResult: (PlantHistory?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d(
                    "PlantScan",
                    "FILE path=${imageFile.absolutePath}, exists=${imageFile.exists()}, size=${imageFile.length()}"
                )

                // 1) Preparar archivo para PlantNet
                val requestFile = imageFile
                    .asRequestBody("image/jpeg".toMediaType())

                val part = MultipartBody.Part.createFormData(
                    "images", // PlantNet espera EXACTAMENTE este nombre
                    imageFile.name,
                    requestFile
                )

                Log.d("PlantScan", "Llamando a PlantNet...")

                val response = RetrofitInstance.api.identifyPlant(
                    images = listOf(part),
                    apiKey = Constants.PLANT_NET_API_KEY
                )

                Log.d("PlantScan", "PlantNet OK, results=${response.results.size}")

                val bestMatch = response.results.firstOrNull()
                val species = bestMatch?.species

                // 2) Usuario actual
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                if (userId.isEmpty()) {
                    Log.w("PlantScan", "No hay usuario logueado. Se guardará userId vacío.")
                }

                // 3) Guardamos en Firestore con el URI de la galería
                val historyItem = PlantHistory(
                    userId = userId,
                    plantName = species?.scientificName ?: "Desconocida",
                    commonName = species?.commonNames?.firstOrNull() ?: "Sin nombre común",
                    familyName = species?.family?.scientificName ?: "Sin familia",
                    confidence = bestMatch?.score ?: 0.0,
                    description = species?.genus?.scientificName ?: "Sin información adicional",
                    imageUrl = displayUri.toString(),   // 👈 content://... persistente
                    scanDate = System.currentTimeMillis()
                )

                firestoreRepo.savePlantHistory(historyItem)
                Log.d("PlantScan", "Guardado correctamente en Firestore (userId=$userId)")

                onResult(historyItem)

            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        Log.e(
                            "PlantScan",
                            "HTTP ERROR ${e.code()} body=${e.response()?.errorBody()?.string()}"
                        )
                    }
                    else -> {
                        Log.e("PlantScan", "ERROR genérico: ${e.message}", e)
                    }
                }
                onResult(null)
            }
        }
    }
}
