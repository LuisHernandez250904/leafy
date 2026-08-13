import 'dart:io';
import 'package:flutter/material.dart';
import '../models/plant_history.dart';
import '../services/supabase_service.dart';
import '../services/plant_net_service.dart';

enum HistoryStatus { idle, loading, loaded, error }

class HistoryProvider extends ChangeNotifier {
  final SupabaseService _supabase = SupabaseService();
  final PlantNetService _plantNet = PlantNetService();

  List<PlantHistory> _histories = [];
  HistoryStatus _status = HistoryStatus.idle;
  String? _error;

  // Scan state
  bool _scanning = false;
  PlantHistory? _lastScan;

  List<PlantHistory> get histories => _histories;
  HistoryStatus get status => _status;
  String? get error => _error;
  bool get scanning => _scanning;
  PlantHistory? get lastScan => _lastScan;
  bool get isLoading => _status == HistoryStatus.loading;

  Future<void> loadHistory() async {
    _status = HistoryStatus.loading;
    _error = null;
    notifyListeners();

    try {
      _histories = await _supabase.getUserHistory();
      _status = HistoryStatus.loaded;
    } catch (e) {
      _status = HistoryStatus.error;
      _error = e.toString();
    }
    notifyListeners();
  }

  /// Analiza la imagen con PlantNet, sube la foto a Supabase Storage y
  /// guarda el registro (con la URL pública, no la ruta local) en la BD.
  Future<PlantHistory?> analyzeAndSave({
    required File imageFile,
  }) async {
    _scanning = true;
    _lastScan = null;
    notifyListeners();

    try {
      final response = await _plantNet.identifyPlant(imageFile);
      if (response == null || response.results.isEmpty) {
        _scanning = false;
        notifyListeners();
        return null;
      }

      // Sube la imagen al bucket 'plant-images' y obtiene la URL pública.
      // Si falla el upload (ej. sin internet momentáneo), el registro se
      // guarda igual pero sin imagen, en vez de guardar una ruta local
      // que solo existe en este teléfono.
      final uploadedUrl = await _supabase.uploadPlantImage(imageFile);

      final best = response.results.first;
      final species = best.species;
      final userId = _supabase.currentUser?.id ?? '';

      final history = PlantHistory(
        userId: userId,
        plantName: species.scientificName.isNotEmpty
            ? species.scientificName
            : 'Desconocida',
        commonName: species.commonNames.isNotEmpty
            ? species.commonNames.first
            : 'Sin nombre común',
        familyName: species.family?.scientificName ?? 'Sin familia',
        confidence: best.score,
        description:
            species.genus?.scientificName ?? 'Sin información adicional',
        imageUrl: uploadedUrl ?? '',
        scanDate: DateTime.now().millisecondsSinceEpoch,
      );

      final saved = await _supabase.savePlantHistory(history);
      _lastScan = saved ?? history;

      // Añadir al inicio de la lista local
      if (saved != null) {
        _histories = [saved, ..._histories];
      }

      _scanning = false;
      notifyListeners();
      return _lastScan;
    } catch (e) {
      // ignore: avoid_print
      print('HistoryProvider.analyzeAndSave error: $e');
      _scanning = false;
      notifyListeners();
      return null;
    }
  }

  Future<PlantHistory?> getById(String id) async {
    // Primero buscar en caché local
    final local = _histories.where((h) => h.id == id).firstOrNull;
    if (local != null) return local;
    return await _supabase.getHistoryItemById(id);
  }
}
