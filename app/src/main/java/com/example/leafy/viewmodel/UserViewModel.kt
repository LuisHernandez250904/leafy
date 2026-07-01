package com.example.leafy.viewmodel

import androidx.lifecycle.ViewModel
import com.example.leafy.firebase.FirestoreRepository
import com.example.leafy.model.PlantHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel asociado al usuario.
 * Expone el historial del usuario usando el mismo repositorio.
 */
class UserViewModel(
    private val repository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val _userHistory = MutableStateFlow<List<PlantHistory>>(emptyList())
    val userHistory: StateFlow<List<PlantHistory>> = _userHistory

    fun loadUserHistory(userId: String) {
        repository.getUserHistory(
            userId = userId,
            onResult = { list ->
                _userHistory.value = list
            },
            onError = {
                // Aquí también podrías manejar o loguear el error
            }
        )
    }
}