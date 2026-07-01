package com.example.leafy.firebase

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.auth.auth

object FirebaseModule {

    fun provideAuth() = Firebase.auth

    fun provideFirestore() = Firebase.firestore
}