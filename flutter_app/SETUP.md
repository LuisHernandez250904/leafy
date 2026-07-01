# Leafy Flutter — Guía de configuración

## 1. Supabase

1. Crea un proyecto en [supabase.com](https://supabase.com)
2. Ejecuta el archivo `supabase_migration.sql` en **SQL Editor** de tu proyecto
3. Copia tus credenciales desde **Settings → API**:
   - `Project URL` → `supabaseUrl`
   - `anon/public` key → `supabaseAnonKey`
4. Pégalas en `lib/utils/constants.dart`

## 2. Regenerar la carpeta android/ (una sola vez)

Este proyecto trae solo el AndroidManifest.xml y algunos recursos, pero le
faltan los archivos de Gradle, el MainActivity y los temas — sin esto NO
compila. Genéralos con:

```bash
cd flutter_app
flutter create . --platforms=android
```

Esto solo agrega lo que falta (gradle, MainActivity, íconos, temas) y
respeta tu `AndroidManifest.xml` y el resto de `lib/` que ya tienes.
Revisa después que el manifest siga teniendo los permisos de CAMERA e
INTERNET; si Flutter lo pisó, vuelve a pegarlos.

## 3. Flutter

```bash
flutter pub get
flutter run
```

## 4. Permisos Android

El archivo `AndroidManifest.xml` ya incluye:
- `INTERNET` (para PlantNet y Supabase)
- `CAMERA` (para escanear plantas)
- `READ_EXTERNAL_STORAGE` / `READ_MEDIA_IMAGES` (para galería)

## 5. Estructura del proyecto

```
flutter_app/
├── lib/
│   ├── main.dart              # Entry point + Supabase.initialize
│   ├── app.dart               # Router (go_router) + tema
│   ├── models/                # Equivalente a los data class de Kotlin
│   ├── services/
│   │   ├── supabase_service.dart   # Auth + DB (reemplaza Firebase)
│   │   └── plant_net_service.dart  # PlantNet API
│   ├── providers/             # Equivalente a los ViewModel de Kotlin
│   ├── screens/               # Equivalente a los Composable Screen
│   ├── widgets/               # Componentes reutilizables
│   └── theme/                 # Colores y tema Material 3
├── supabase_migration.sql     # DDL para crear la tabla en Supabase
└── SETUP.md                   # Esta guía
```

## 6. Equivalencias Firebase → Supabase

| Firebase                     | Supabase                          |
|------------------------------|-----------------------------------|
| `FirebaseAuth.getInstance()` | `Supabase.instance.client.auth`   |
| `signInWithEmailAndPassword` | `auth.signInWithPassword()`       |
| `createUserWithEmailAndPassword` | `auth.signUp()`               |
| `auth.signOut()`             | `auth.signOut()`                  |
| `FirebaseFirestore.getInstance()` | `Supabase.instance.client`   |
| `collection("plant_history")` | `.from('plant_history')`         |
| `.set(data)`                 | `.insert(data)`                   |
| `.whereEqualTo("userId", id)` | `.eq('user_id', id)`             |
| `document(id).get()`         | `.eq('id', id).single()`          |
