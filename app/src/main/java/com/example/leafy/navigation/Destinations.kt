package com.example.leafy.navigation

sealed class Destinations(val route: String) {

    object Splash : Destinations("splash")
    object Home : Destinations("home")
    object Login : Destinations("login")
    object Register : Destinations("register")
    object Profile : Destinations("profile")

    object Scan : Destinations("scan/{mode}") {
        fun createRoute(mode: String) = "scan/$mode"
    }

    object Explore : Destinations("explore")
    object History : Destinations("history")
    object Detail : Destinations("detail/{id}") {
        fun createRoute(id: String) = "detail/$id"
    }
}