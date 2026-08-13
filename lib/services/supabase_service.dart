import 'dart:io';
import 'package:supabase_flutter/supabase_flutter.dart';
import '../models/plant_history.dart';
import '../models/user_model.dart';
import '../utils/constants.dart';

/// Servicio único que envuelve Supabase Auth + Database.
/// Reemplaza AuthRepository + FirestoreRepository de Firebase.
class SupabaseService {
  static final SupabaseService _instance = SupabaseService._internal();
  factory SupabaseService() => _instance;
  SupabaseService._internal();

  SupabaseClient get _client => Supabase.instance.client;
  GoTrueClient get _auth => _client.auth;

  // ── AUTH ─────────────────────────────────────────────────────────────────

  User? get currentUser => _auth.currentUser;

  Stream<AuthState> get authStateChanges => _auth.onAuthStateChange;

  UserModel? get currentUserModel {
    final u = currentUser;
    if (u == null) return null;
    return UserModel(
      id: u.id,
      name: u.userMetadata?['name']?.toString() ??
          u.email?.split('@').first ??
          'User',
      email: u.email ?? '',
    );
  }

  Future<UserModel?> login(String email, String password) async {
    try {
      final response = await _auth.signInWithPassword(
        email: email,
        password: password,
      );
      final u = response.user;
      if (u == null) return null;
      return UserModel(
        id: u.id,
        name: u.userMetadata?['name']?.toString() ??
            u.email?.split('@').first ??
            'User',
        email: u.email ?? '',
      );
    } catch (e) {
      // ignore: avoid_print
      print('SupabaseService.login error: $e');
      return null;
    }
  }

  Future<UserModel?> register(
      String name, String email, String password) async {
    try {
      final response = await _auth.signUp(
        email: email,
        password: password,
        data: {'name': name},
      );
      final u = response.user;
      if (u == null) return null;
      return UserModel(id: u.id, name: name, email: email);
    } catch (e) {
      // ignore: avoid_print
      print('SupabaseService.register error: $e');
      return null;
    }
  }

  Future<void> logout() async {
    await _auth.signOut();
  }

  // ── STORAGE ──────────────────────────────────────────────────────────────

  /// Sube la foto tomada/seleccionada al bucket de Supabase Storage y
  /// retorna la URL pública. Retorna null si falla (usuario no autenticado
  /// o error de red/permmisos).
  ///
  /// Reemplaza el bug anterior de guardar `picked.path` (ruta local del
  /// dispositivo) directo en la base de datos.
  Future<String?> uploadPlantImage(File imageFile) async {
    final userId = currentUser?.id;
    if (userId == null) return null;
    try {
      final ext = imageFile.path.split('.').last;
      final fileName =
          '$userId/${DateTime.now().millisecondsSinceEpoch}.$ext';

      await _client.storage.from(Constants.plantImagesBucket).upload(
            fileName,
            imageFile,
            fileOptions: const FileOptions(upsert: false),
          );

      return _client.storage
          .from(Constants.plantImagesBucket)
          .getPublicUrl(fileName);
    } catch (e) {
      // ignore: avoid_print
      print('SupabaseService.uploadPlantImage error: $e');
      return null;
    }
  }

  // ── DATABASE ─────────────────────────────────────────────────────────────

  /// Inserta un nuevo registro de historial de planta.
  Future<PlantHistory?> savePlantHistory(PlantHistory history) async {
    try {
      final data = await _client
          .from(Constants.plantHistoryTable)
          .insert(history.toMap())
          .select()
          .single();
      return PlantHistory.fromMap(data);
    } catch (e) {
      // ignore: avoid_print
      print('SupabaseService.savePlantHistory error: $e');
      return null;
    }
  }

  /// Obtiene todo el historial del usuario autenticado, ordenado por fecha desc.
  Future<List<PlantHistory>> getUserHistory() async {
    final userId = currentUser?.id;
    if (userId == null) return [];
    try {
      final data = await _client
          .from(Constants.plantHistoryTable)
          .select()
          .eq('user_id', userId)
          .order('scan_date', ascending: false);
      return (data as List<dynamic>)
          .map((e) => PlantHistory.fromMap(e as Map<String, dynamic>))
          .toList();
    } catch (_) {
      return [];
    }
  }

  /// Obtiene un único item por su ID.
  Future<PlantHistory?> getHistoryItemById(String id) async {
    try {
      final data = await _client
          .from(Constants.plantHistoryTable)
          .select()
          .eq('id', id)
          .single();
      return PlantHistory.fromMap(data);
    } catch (_) {
      return null;
    }
  }
}
