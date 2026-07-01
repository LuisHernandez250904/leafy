package com.example.leafy.viewmodel

import androidx.lifecycle.ViewModel
import com.example.leafy.firebase.FirestoreRepository
import com.example.leafy.model.PlantHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel pensado para manejar el historial relacionado con el escaneo.
 * Ahora utiliza getUserHistory(userId) del repositorio.
 */
class ScanViewModel(
    private val repository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val _scanHistory = MutableStateFlow<List<PlantHistory>>(emptyList())
    val scanHistory: StateFlow<List<PlantHistory>> = _scanHistory

    fun loadHistoryForUser(userId: String) {
        repository.getUserHistory(
            userId = userId,
            onResult = { list ->
                _scanHistory.value = list
            },
            onError = {
                // Aquí podrías loguear el error si quieres
                // Log.e("ScanViewModel", "Error cargando historial", it)
            }
        )
    }
}