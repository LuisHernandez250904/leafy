package com.example.leafy.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.leafy.navigation.NavGraph
import com.example.leafy.ui.theme.LeafyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            // 🌗 Estado global de tema
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }

            // 🔔 Estado global de notificaciones (por ahora sólo preferencia)
            var notificationsEnabled by rememberSaveable { mutableStateOf(true) }

            // 🌎 Estado global de lenguaje mostrado en Perfil
            var language by rememberSaveable { mutableStateOf("English") }

            LeafyTheme(darkTheme = isDarkTheme) {
                NavGraph(
                    isDarkTheme = isDarkTheme,
                    onThemeChanged = { isDarkTheme = it },
                    notificationsEnabled = notificationsEnabled,
                    onNotificationsChanged = { notificationsEnabled = it },
                    language = language,
                    onLanguageSelected = { language = it }
                )
            }
        }
    }
}