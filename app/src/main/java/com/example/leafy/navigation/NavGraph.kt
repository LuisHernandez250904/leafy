package com.example.leafy.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.leafy.model.User
import com.example.leafy.ui.screens.*
import com.example.leafy.viewmodel.HistoryViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    notificationsEnabled: Boolean,
    onNotificationsChanged: (Boolean) -> Unit,
    language: String,
    onLanguageSelected: (String) -> Unit
) {

    NavHost(
        navController = navController,
        startDestination = Destinations.Splash.route   // 👈 Splash como inicio
    ) {

        // 🌱 SPLASH
        composable(Destinations.Splash.route) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Destinations.Home.route) {
                        popUpTo(Destinations.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // 🔐 LOGIN
        composable(Destinations.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Destinations.Profile.route) {
                        popUpTo(Destinations.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // 🏠 HOME
        composable(Destinations.Home.route) {
            HomeScreen(
                recentResults = emptyList(),
                onScanPressed = {
                    // DIRECTO a cámara
                    navController.navigate(Destinations.Scan.createRoute("camera"))
                },
                onGalleryPressed = {
                    // DIRECTO a galería
                    navController.navigate(Destinations.Scan.createRoute("gallery"))
                },
                onExplorePressed = {
                    navController.navigate(Destinations.Explore.route)
                },
                onHistoryPressed = {
                    navController.navigate(Destinations.History.route)
                },
                onProfilePressed = {
                    navController.navigate(Destinations.Profile.route)
                }
            )
        }

        // 📜 HISTORIAL
        composable(Destinations.History.route) {
            val historyViewModel: HistoryViewModel = viewModel()

            HistoryScreen(
                onBack = { navController.popBackStack() },
                onItemClicked = { item ->
                    navController.navigate(Destinations.Detail.createRoute(item.id))
                },
                onHomePressed = { navController.navigate(Destinations.Home.route) },
                onMyPlantsPressed = { navController.navigate(Destinations.History.route) },
                onExplorePressed = { navController.navigate(Destinations.Explore.route) },
                onProfilePressed = { navController.navigate(Destinations.Profile.route) }
            )
        }


        // 📝 DETALLE
        composable(
            route = "detail/{id}"
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            PlantDetailScreen(
                historyId = id,
                onBack = { navController.popBackStack() }
            )
        }

        // 🌿 EXPLORE
        composable(Destinations.Explore.route) {
            ExploreScreen(
                onBack = { navController.popBackStack() },
                onHomePressed = { navController.navigate(Destinations.Home.route) },
                onMyPlantsPressed = { navController.navigate(Destinations.History.route) },
                onExplorePressed = {},
                onProfilePressed = { navController.navigate(Destinations.Profile.route) }
            )
        }

        // 👤 PERFIL
        composable(Destinations.Profile.route) {

            // 🔹 Tomamos el usuario actual de FirebaseAuth
            val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser

            // 🔹 Lo convertimos a tu modelo User (ajusta campos si tu User es distinto)
            val currentUser: User? = firebaseUser?.let { u ->
                User(
                    id = u.uid,
                    name = u.displayName ?: (u.email?.substringBefore("@") ?: "User"),
                    email = u.email ?: ""
                )
            }

            ProfileScreen(
                user = currentUser,  // 👈 YA NO ES NULL SI HAY SESIÓN

                onBack = { navController.popBackStack() },

                onSignOut = {
                    // Cerramos sesión en Firebase
                    com.google.firebase.auth.FirebaseAuth.getInstance().signOut()

                    // Y mandamos a Home como invitado
                    navController.navigate(Destinations.Home.route) {
                        popUpTo(Destinations.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },

                onLoginRegister = {
                    // Abre la pantalla de Login/Registro
                    navController.navigate(Destinations.Login.route)
                },

                onEditProfile = {
                    // Aquí luego puedes navegar a EditProfile
                },

                // 🔗 Estados globales
                isDarkTheme = isDarkTheme,
                onDarkThemeToggle = { enabled ->
                    onThemeChanged(enabled)
                },

                notificationsEnabled = notificationsEnabled,
                onNotificationsToggle = { enabled ->
                    onNotificationsChanged(enabled)
                },

                language = language,
                onLanguageClick = { newLang ->
                    onLanguageSelected(newLang)
                }
            )
        }
    }
}