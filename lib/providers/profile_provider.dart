import 'package:flutter/material.dart';
import '../models/plant_history.dart';
import '../services/supabase_service.dart';

class Achievement {
  final String id;
  final String title;
  final String description;
  final bool unlocked;
  final String emoji;

  const Achievement({
    required this.id,
    required this.title,
    required this.description,
    required this.unlocked,
    required this.emoji,
  });
}

class UserStats {
  final int totalScans;
  final int distinctFamilies;
  final int lastScanDate;
  final int level;
  final int currentXp;
  final int xpForNextLevel;
  final List<Achievement> achievements;

  const UserStats({
    this.totalScans = 0,
    this.distinctFamilies = 0,
    this.lastScanDate = 0,
    this.level = 1,
    this.currentXp = 0,
    this.xpForNextLevel = 5,
    this.achievements = const [],
  });
}

class ProfileProvider extends ChangeNotifier {
  final SupabaseService _service = SupabaseService();

  UserStats _stats = const UserStats();
  bool _loading = false;

  UserStats get stats => _stats;
  bool get loading => _loading;

  Future<void> loadStats() async {
    _loading = true;
    notifyListeners();

    final history = await _service.getUserHistory();
    _stats = _computeStats(history);

    _loading = false;
    notifyListeners();
  }

  UserStats _computeStats(List<PlantHistory> list) {
    final total = list.length;
    final families = list
        .map((h) => h.familyName)
        .where((f) => f.isNotEmpty)
        .toSet()
        .length;
    final last =
        list.isNotEmpty ? list.map((h) => h.scanDate).reduce((a, b) => a > b ? a : b) : 0;

    const xpPerLevel = 5;
    final level = total == 0 ? 1 : (total ~/ xpPerLevel) + 1;
    final currentXp = total % xpPerLevel;

    final achievements = _buildAchievements(
      totalScans: total,
      distinctFamilies: families,
    );

    return UserStats(
      totalScans: total,
      distinctFamilies: families,
      lastScanDate: last,
      level: level,
      currentXp: currentXp,
      xpForNextLevel: xpPerLevel,
      achievements: achievements,
    );
  }

  List<Achievement> _buildAchievements({
    required int totalScans,
    required int distinctFamilies,
  }) {
    return [
      Achievement(
        id: 'first_scan',
        title: 'Primer escaneo',
        description: 'Has identificado tu primera planta.',
        unlocked: totalScans >= 1,
        emoji: '🌱',
      ),
      Achievement(
        id: 'collector_5',
        title: 'Coleccionista inicial',
        description: 'Has identificado 5 plantas.',
        unlocked: totalScans >= 5,
        emoji: '🌿',
      ),
      Achievement(
        id: 'explorer_10',
        title: 'Explorador botánico',
        description: 'Has identificado 10 plantas.',
        unlocked: totalScans >= 10,
        emoji: '🔭',
      ),
      Achievement(
        id: 'families_3',
        title: 'Diversidad botánica',
        description: 'Al menos 3 familias distintas.',
        unlocked: distinctFamilies >= 3,
        emoji: '🌳',
      ),
      Achievement(
        id: 'marathon_20',
        title: 'Maratón verde',
        description: '20 plantas o más identificadas.',
        unlocked: totalScans >= 20,
        emoji: '🏅',
      ),
    ];
  }
}
