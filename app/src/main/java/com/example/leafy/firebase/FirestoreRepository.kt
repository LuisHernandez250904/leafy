package com.example.leafy.firebase

import com.example.leafy.model.PlantHistory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val historyCollection = firestore.collection("plant_history")

    /**
     * Guarda un registro de historial de planta.
     * - Si history.id está vacío -> crea un documento nuevo.
     * - Si history.id tiene valor -> actualiza ese documento.
     */
    suspend fun savePlantHistory(history: PlantHistory) {
        val docRef = if (history.id.isBlank()) {
            historyCollection.document()
        } else {
            historyCollection.document(history.id)
        }

        val dataToSave = history.copy(id = docRef.id)
        docRef.set(dataToSave).await()
    }

    /**
     * Obtiene todo el historial de un usuario (una sola vez).
     * - Filtra por userId
     * - Ordena por scanDate descendente (los más recientes primero)
     */
    fun getUserHistory(
        userId: String,
        onResult: (List<PlantHistory>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        historyCollection
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents
                    .mapNotNull { d ->
                        d.toObject(PlantHistory::class.java)?.copy(id = d.id)
                    }
                    .sortedByDescending { it.scanDate } // ordenar localmente por fecha

                onResult(list)
            }
            .addOnFailureListener { e ->
                onError(e)
            }
    }

    /**
     * Obtiene un solo elemento del historial por ID.
     * Lo usa PlantDetailScreen.
     */
    suspend fun getHistoryItemById(id: String): PlantHistory? {
        val snapshot = historyCollection.document(id).get().await()
        return snapshot.toObject(PlantHistory::class.java)?.copy(id = snapshot.id)
    }
}