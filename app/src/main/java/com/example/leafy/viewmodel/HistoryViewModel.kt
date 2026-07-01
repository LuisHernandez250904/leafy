package com.example.leafy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leafy.firebase.FirestoreRepository
import com.example.leafy.model.PlantHistory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// 🔹 Estado que manejará HistoryScreen
data class HistoryUiState(
    val isLoading: Boolean = false,
    val histories: List<PlantHistory> = emptyList(),
    val error: String? = null
)

class HistoryViewModel(
    private val repo: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState

    init {
        loadHistory()  // Cargar cuando el ViewModel se inicializa
    }

    fun loadHistory() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            _uiState.value = HistoryUiState(
                isLoading = false,
                histories = emptyList(),
                error = "Usuario no autenticado"
            )
            return
        }

        _uiState.value = HistoryUiState(isLoading = true)

        repo.getUserHistory(
            userId = currentUser.uid,
            onResult = { list ->
                viewModelScope.launch {
                    _uiState.value = HistoryUiState(
                        isLoading = false,
                        histories = list,
                        error = null
                    )
                }
            },
            onError = { e ->
                viewModelScope.launch {
                    _uiState.value = HistoryUiState(
                        isLoading = false,
                        histories = emptyList(),
                        error = e.message
                    )
                }
            }
        )
    }
}