package com.example.leafy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leafy.firebase.FirestoreRepository
import com.example.leafy.model.PlantHistory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UserStats(
    val totalScans: Int = 0,
    val distinctFamilies: Int = 0,
    val lastScanDate: Long = 0L,
    val level: Int = 1,
    val currentXp: Int = 0,
    val xpForNextLevel: Int = 5,
    val achievements: List<Achievement> = emptyList()
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val unlocked: Boolean
)

class ProfileViewModel(
    private val repo: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val _stats = MutableStateFlow(UserStats())
    val stats: StateFlow<UserStats> = _stats

    init {
        loadStats()
    }

    fun loadStats() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            repo.getUserHistory(
                userId = uid,
                onResult = { historyList ->
                    computeStats(historyList)
                },
                onError = {
                    // Podrías loguear el error si quieres
                }
            )
        }
    }

    private fun computeStats(historyList: List<PlantHistory>) {
        val total = historyList.size

        val distinctFamilies = historyList
            .mapNotNull { it.familyName }
            .filter { it.isNotBlank() }
            .toSet()
            .size

        val last = historyList
            .maxByOrNull { it.scanDate }
            ?.scanDate ?: 0L

        // ---- LEVEL & XP ----
        val xpPerLevel = 5                 // cada 5 escaneos = 1 nivel
        val level = if (total == 0) 1 else (total / xpPerLevel) + 1
        val currentXp = total % xpPerLevel
        val xpForNextLevel = xpPerLevel

        // ---- LOGROS ----
        val achievements = buildAchievements(
            totalScans = total,
            distinctFamilies = distinctFamilies
        )

        _stats.value = UserStats(
            totalScans = total,
            distinctFamilies = distinctFamilies,
            lastScanDate = last,
            level = level,
            currentXp = currentXp,
            xpForNextLevel = xpForNextLevel,
            achievements = achievements
        )
    }

    private fun buildAchievements(
        totalScans: Int,
        distinctFamilies: Int
    ): List<Achievement> {
        val list = mutableListOf<Achievement>()

        list.add(
            Achievement(
                id = "first_scan",
                title = "Primer escaneo",
                description = "Has identificado tu primera planta.",
                unlocked = totalScans >= 1
            )
        )

        list.add(
            Achievement(
                id = "collector_5",
                title = "Coleccionista inicial",
                description = "Has identificado 5 plantas.",
                unlocked = totalScans >= 5
            )
        )

        list.add(
            Achievement(
                id = "explorer_10",
                title = "Explorador botánico",
                description = "Has identificado 10 plantas.",
                unlocked = totalScans >= 10
            )
        )

        list.add(
            Achievement(
                id = "families_3",
                title = "Diversidad botánica",
                description = "Has identificado plantas de al menos 3 familias distintas.",
                unlocked = distinctFamilies >= 3
            )
        )

        list.add(
            Achievement(
                id = "marathon_20",
                title = "Maratón verde",
                description = "Has identificado 20 plantas o más.",
                unlocked = totalScans >= 20
            )
        )

        return list
    }
}
