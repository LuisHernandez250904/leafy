package com.example.leafy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.leafy.ui.components.TopBar

// ---------------------------
//   DATOS DE EJEMPLO
// ---------------------------
data class ExplorePlant(
    val name: String,
    val scientificName: String,
    val family: String,
    val imageUrl: String,
    val description: String
)

private val demoPlants = listOf(
    ExplorePlant(
        name = "Girasol",
        scientificName = "Helianthus annuus",
        family = "Asteraceae",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/4/40/Sunflower_sky_backdrop.jpg",
        description = "Planta muy llamativa por su gran inflorescencia amarilla y su seguimiento al sol."
    ),
    ExplorePlant(
        name = "Monstera",
        scientificName = "Monstera deliciosa",
        family = "Araceae",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e3/Monstera_deliciosa2.jpg",
        description = "Planta de interior popular por sus hojas grandes y perforadas."
    ),
    ExplorePlant(
        name = "Lavanda",
        scientificName = "Lavandula angustifolia",
        family = "Lamiaceae",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/f/fb/Lavandula_angustifolia_001.jpg",
        description = "Conocida por su aroma relajante y flores moradas."
    )
)


// ---------------------------
//   CHIP CATEGORÍAS
// ---------------------------
@Composable
fun CategoryChip(
    text: String,
    selected: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (selected) Color(0xFF0FA958)
                else Color(0xFFF0F0F0)
            )
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = text,
            color = if (selected) Color.White else Color.Black,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}


// ---------------------------
//  ENUM PARA MENÚ
// ---------------------------
enum class BottomDestination { HOME, MY_PLANTS, EXPLORE, PROFILE }


// ---------------------------
//  ICONO SELECCIONABLE (CÍRCULO VERDE)
// ---------------------------
@Composable
private fun SelectableIcon(
    selected: Boolean,
    icon: ImageVector
) {
    if (selected) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFF00C26F)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White
            )
        }
    } else {
        Icon(
            imageVector = icon,
            contentDescription = null
        )
    }
}


// ---------------------------
//  MENÚ INFERIOR COMPARTIDO
// ---------------------------
@Composable
fun MainBottomBar(
    current: BottomDestination,
    onHomePressed: () -> Unit,
    onMyPlantsPressed: () -> Unit,
    onExplorePressed: () -> Unit,
    onProfilePressed: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = current == BottomDestination.HOME,
            onClick = onHomePressed,
            icon = { SelectableIcon(current == BottomDestination.HOME, Icons.Default.Home) },
            label = { Text("Inicio") }
        )

        NavigationBarItem(
            selected = current == BottomDestination.MY_PLANTS,
            onClick = onMyPlantsPressed,
            icon = { SelectableIcon(current == BottomDestination.MY_PLANTS, Icons.Default.List) },
            label = { Text("Mis Plantas") }
        )

        NavigationBarItem(
            selected = current == BottomDestination.EXPLORE,
            onClick = onExplorePressed,
            icon = { SelectableIcon(current == BottomDestination.EXPLORE, Icons.Default.Search) },
            label = { Text("Explorar") }
        )

        NavigationBarItem(
            selected = current == BottomDestination.PROFILE,
            onClick = onProfilePressed,
            icon = { SelectableIcon(current == BottomDestination.PROFILE, Icons.Default.Person) },
            label = { Text("Perfil") }
        )
    }
}


// ---------------------------
//  CARD FEATURED
// ---------------------------
@Composable
fun FeaturedCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF00C26F),
                        Color(0xFF05AA62)
                    )
                )
            )
            .padding(22.dp)
    ) {
        Column {
            Text(
                text = "FEATURED THIS WEEK",
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Monstera Deliciosa",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "The most popular houseplant of 2024",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp
            )
            Spacer(Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(horizontal = 22.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Learn More",
                    color = Color(0xFF00C26F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}


// ---------------------------
// CARD DE PLANTA MINI
// ---------------------------
@Composable
fun PlantMiniCard(plant: ExplorePlant) {
    Box(
        modifier = Modifier
            .width(170.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            ) {
                AsyncImage(
                    model = plant.imageUrl,
                    contentDescription = plant.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "95%",
                        color = Color(0xFF0FA958),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = plant.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = plant.scientificName,
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
        }
    }
}


// ---------------------------
//  PANTALLA COMPLETA
// ---------------------------
@Composable
fun ExploreScreen(
    onBack: () -> Unit,
    onHomePressed: () -> Unit,
    onMyPlantsPressed: () -> Unit,
    onExplorePressed: () -> Unit,
    onProfilePressed: () -> Unit
) {

    Scaffold(
        topBar = {
            TopBar(
                title = "",
                onBack = onBack
            )
        },
        bottomBar = {
            MainBottomBar(
                current = BottomDestination.EXPLORE,
                onHomePressed = onHomePressed,
                onMyPlantsPressed = onMyPlantsPressed,
                onExplorePressed = onExplorePressed,
                onProfilePressed = onProfilePressed
            )
        },
        containerColor = Color(0xFFF5F6FA)
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F6FA))
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {

            // HEADER
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF00C26F)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Explore",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                        Text(
                            text = "Discover popular plant species",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // CATEGORÍAS
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    item { CategoryChip("All Plants", selected = true) }
                    item { CategoryChip("Tropical") }
                    item { CategoryChip("Succulent") }
                }
            }

            // FEATURED
            item { FeaturedCard() }

            // TÍTULO POPULAR SPECIES
            item {
                Text(
                    text = "Popular Species",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            }

            // LISTA HORIZONTAL
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(demoPlants) { plant ->
                        PlantMiniCard(plant)
                    }
                }
            }

            // LISTA VERTICAL ORIGINAL
            items(demoPlants) { plant ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        AsyncImage(
                            model = plant.imageUrl,
                            contentDescription = plant.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(plant.name, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Text(plant.scientificName, color = Color.Gray)
                        Text("Familia: ${plant.family}", color = Color.Gray)
                        Spacer(Modifier.height(4.dp))
                        Text(plant.description)
                    }
                }
            }
        }
    }
}
