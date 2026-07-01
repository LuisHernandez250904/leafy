package com.example.leafy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.leafy.model.User
import com.example.leafy.viewmodel.ProfileViewModel
import com.example.leafy.viewmodel.Achievement   // 👈 importa el tipo de logro

@Composable
fun ProfileScreen(
    user: User?,
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    onLoginRegister: () -> Unit,
    onEditProfile: () -> Unit,
    isDarkTheme: Boolean,
    onDarkThemeToggle: (Boolean) -> Unit,
    notificationsEnabled: Boolean,
    onNotificationsToggle: (Boolean) -> Unit,
    language: String,
    onLanguageClick: (String) -> Unit
) {
    val isGuest = user == null
    val name = user?.name.takeUnless { it.isNullOrBlank() }
        ?: if (language == "Spanish") "Explorador de Plantas" else "Plant Explorer"
    val email = user?.email.takeUnless { it.isNullOrBlank() } ?: "explorer@leaflens.app"

    var showHelpDialog by remember { mutableStateOf(false) }
    var showAchievementsDialog by remember { mutableStateOf(false) } // 👈 nuevo estado

    // ViewModel de perfil (stats, nivel, logros)
    val profileViewModel: ProfileViewModel = viewModel()
    val stats by profileViewModel.stats.collectAsState()

    // Recargar stats cuando el estado de invitado/logueado cambie
    LaunchedEffect(isGuest) {
        if (!isGuest) {
            profileViewModel.loadStats()
        }
    }

    // Valores calculados para mostrar en la UI
    val totalScansText = if (isGuest) "-" else stats.totalScans.toString()

    val totalAchievements = stats.achievements.size
    val unlockedAchievements = stats.achievements.count { it.unlocked }
    val achievementsText =
        if (isGuest || totalAchievements == 0) "-"
        else "$unlockedAchievements/$totalAchievements"

    val xpProgress =
        if (isGuest || stats.xpForNextLevel == 0) 0f
        else stats.currentXp.toFloat() / stats.xpForNextLevel.toFloat()

    val xpLabel = if (isGuest) {
        if (language == "Spanish") "Crea una cuenta para comenzar a ganar XP"
        else "Create an account to start earning XP"
    } else {
        "${stats.currentXp}/${stats.xpForNextLevel} XP"
    }

    val xpLeft = (stats.xpForNextLevel - stats.currentXp).coerceAtLeast(0)
    val xpLeftLabel = if (isGuest) {
        if (language == "Spanish") "Inicia sesión para desbloquear recompensas"
        else "Sign in to unlock rewards"
    } else {
        if (language == "Spanish") "Faltan $xpLeft XP" else "$xpLeft XP left"
    }

    // ========== DIALOGO HELP & SUPPORT ==========
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text(if (language == "Spanish") "Cerrar" else "Close")
                }
            },
            title = {
                Text(if (language == "Spanish") "Ayuda y soporte" else "Help & Support")
            },
            text = {
                Text(
                    if (language == "Spanish") {
                        "Leafy es una app de identificación y cuidado de plantas desarrollada como proyecto académico.\n\n" +
                                "Creador: Studio Leafy Ititeam\n" +
                                "Versión: 1.0.0\n\n" +
                                "Para dudas o sugerencias, contacta al desarrollador."
                    } else {
                        "Leafy is a plant identification and care app developed as an academic project.\n\n" +
                                "Creator: Studio Leafy Ititeam\n" +
                                "Version: 1.0.0\n\n" +
                                "For questions or feedback, contact the developer."
                    }
                )
            }
        )
    }

    // ========== DIALOGO DE LOGROS ==========
    if (showAchievementsDialog && !isGuest) {
        AchievementsDialog(
            achievements = stats.achievements,
            language = language,
            onDismiss = { showAchievementsDialog = false }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // =============== HEADER ===============
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xFF00C26F),
                                Color(0xFF00B45F)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Top bar
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        val titleText = if (language == "Spanish") {
                            if (isGuest) "Perfil invitado" else "Perfil"
                        } else {
                            if (isGuest) "Guest Profile" else "Profile"
                        }

                        Text(
                            text = titleText,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.align(Alignment.Center)
                        )

                        IconButton(
                            onClick = { if (!isGuest) onEditProfile() },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = if (isGuest) Color.White.copy(alpha = 0.4f) else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = email,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )

                    Spacer(Modifier.height(10.dp))

                    // Chip de modo / nivel
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF00A85B))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFEB3B),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))

                            val chipText = if (isGuest) {
                                if (language == "Spanish") "Modo invitado" else "Guest Mode"
                            } else {
                                if (language == "Spanish") "Nivel ${stats.level}" else "Level ${stats.level}"
                            }

                            Text(
                                text = chipText,
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Stats rápidos
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ProfileStatCard(
                            modifier = Modifier.weight(1f),
                            title = if (language == "Spanish") "PLANTAS IDENTIFICADAS" else "PLANTS IDENTIFIED",
                            value = totalScansText,
                            icon = Icons.Default.TrendingUp
                        )
                        ProfileStatCard(
                            modifier = Modifier.weight(1f),
                            title = if (language == "Spanish") "LOGROS" else "ACHIEVEMENTS",
                            value = achievementsText,
                            icon = Icons.Default.EmojiEvents
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // Invitado: bloque para iniciar sesión
                    if (isGuest) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = if (language == "Spanish") "Inicia sesión o regístrate" else "Sign in or sign up",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF00B45F)
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = if (language == "Spanish") {
                                        "Crea una cuenta para guardar tu historial, ver estadísticas y sincronizar tus plantas."
                                    } else {
                                        "Create an account to save your history, view stats and sync your plants."
                                    },
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                                Spacer(Modifier.height(12.dp))
                                Button(
                                    onClick = onLoginRegister,
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00C26F),
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = if (language == "Spanish") "Iniciar sesión / Registrarse" else "Sign in / Register",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // =============== LEVEL PROGRESS ===============
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = if (language == "Spanish") "Progreso de nivel" else "Level Progress",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = xpLabel,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = xpProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(50)),
                        color = Color(0xFF00C26F),
                        trackColor = Color(0xFFE0E0E0)
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isGuest) {
                                if (language == "Spanish") "Inicia sesión para desbloquear recompensas"
                                else "Sign in to unlock rewards"
                            } else {
                                if (language == "Spanish") "Siguiente recompensa: Nueva medalla"
                                else "Next reward: New Badge"
                            },
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        if (!isGuest) {
                            Text(
                                text = xpLeftLabel,
                                fontSize = 12.sp,
                                color = Color(0xFF00C26F)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // =============== ACHIEVEMENTS ===============
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable(enabled = !isGuest) {   // 👈 ahora abre el diálogo
                        if (!isGuest) showAchievementsDialog = true
                    },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = if (language == "Spanish") "Logros" else "Achievements",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(8.dp))

                    if (isGuest) {
                        Text(
                            text = if (language == "Spanish")
                                "Inicia sesión para comenzar a desbloquear logros."
                            else
                                "Sign in to start unlocking achievements.",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val maxSlots = 4
                            val achievements = stats.achievements

                            for (i in 0 until maxSlots) {
                                val active =
                                    i < achievements.size && achievements[i].unlocked
                                AchievementBox(active = active)
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = if (language == "Spanish")
                                "Toca para ver todos tus logros"
                            else
                                "Tap to see all your achievements",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // =============== SETTINGS ===============
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = if (language == "Spanish") "Configuración" else "Settings",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(12.dp))

                // 🌗 Dark Mode
                SettingRow(
                    icon = Icons.Default.WbSunny,
                    iconTint = Color(0xFF00C26F),
                    title = if (language == "Spanish") "Modo oscuro" else "Dark Mode"
                ) {
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onDarkThemeToggle(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF00C26F)
                        )
                    )
                }

                // 🔔 Notifications
                SettingRow(
                    icon = Icons.Default.Notifications,
                    iconTint = Color(0xFF00C26F),
                    title = if (language == "Spanish") "Notificaciones" else "Notifications"
                ) {
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { onNotificationsToggle(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF00C26F)
                        )
                    )
                }

                // 🌎 Language
                SettingRow(
                    icon = Icons.Default.Language,
                    iconTint = Color(0xFF00C26F),
                    title = if (language == "Spanish") "Idioma" else "Language",
                    onClick = {
                        val newLang = if (language == "English") "Spanish" else "English"
                        onLanguageClick(newLang)
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(language, fontSize = 13.sp, color = Color.Gray)
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                }

                // 📤 Share App (placeholder)
                SettingRow(
                    icon = Icons.Default.Share,
                    iconTint = Color(0xFF00C26F),
                    title = if (language == "Spanish") "Compartir app" else "Share App"
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }

                // 🆘 Help & Support
                SettingRow(
                    icon = Icons.Default.HelpOutline,
                    iconTint = Color(0xFF00C26F),
                    title = if (language == "Spanish") "Ayuda y soporte" else "Help & Support",
                    onClick = { showHelpDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }

                // 🔴 Logout solo si está logueado
                if (!isGuest) {
                    SettingRow(
                        icon = Icons.Default.Logout,
                        iconTint = Color(0xFFE53935),
                        title = if (language == "Spanish") "Cerrar sesión" else "Logout",
                        titleColor = Color(0xFFE53935),
                        onClick = onSignOut
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("LeafLens v1.0.0", fontSize = 11.sp, color = Color.Gray)
                Text(
                    text = if (language == "Spanish") "Con tecnología de PlantNet API" else "Powered by PlantNet API",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

// ---------- COMPONENTES REUTILIZABLES ----------

@Composable
fun ProfileStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector
) {
    Card(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .height(90.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF00C26F),
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = title,
                fontSize = 11.sp,
                color = Color.Gray
            )
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00C26F)
            )
        }
    }
}

@Composable
fun AchievementBox(active: Boolean) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (active) Color(0xFF00C26F) else Color(0xFFE0E0E0)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (active) Icons.Default.EmojiEvents else Icons.Default.Lock,
            contentDescription = null,
            tint = if (active) Color.White else Color.Gray
        )
    }
}

@Composable
fun SettingRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    titleColor: Color = Color.Black,
    onClick: () -> Unit = {},
    trailing: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                color = titleColor,
                modifier = Modifier.weight(1f)
            )

            trailing?.invoke()
        }
    }
}

// ---------- DIALOGO DE LISTA DE LOGROS ----------

@Composable
fun AchievementsDialog(
    achievements: List<Achievement>,
    language: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == "Spanish") "Cerrar" else "Close")
            }
        },
        title = {
            Text(
                text = if (language == "Spanish") "Tus logros" else "Your achievements",
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            if (achievements.isEmpty()) {
                Text(
                    text = if (language == "Spanish")
                        "Aún no has desbloqueado logros. ¡Empieza identificando plantas!"
                    else
                        "You haven't unlocked any achievements yet. Start identifying plants!",
                    fontSize = 13.sp
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    achievements.forEach { ach ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (ach.unlocked) Color(0xFF00C26F).copy(alpha = 0.12f)
                                        else Color.LightGray.copy(alpha = 0.2f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (ach.unlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (ach.unlocked) Color(0xFF00C26F) else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = ach.title,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = ach.description,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = if (ach.unlocked)
                                        if (language == "Spanish") "Desbloqueado ✅" else "Unlocked ✅"
                                    else
                                        if (language == "Spanish") "Bloqueado" else "Locked",
                                    fontSize = 11.sp,
                                    color = if (ach.unlocked) Color(0xFF00C26F) else Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

