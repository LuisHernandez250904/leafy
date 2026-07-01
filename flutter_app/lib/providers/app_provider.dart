import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// Estado global de la app: tema, notificaciones, idioma.
class AppProvider extends ChangeNotifier {
  bool _darkTheme = false;
  bool _notificationsEnabled = true;
  String _language = 'Español';

  bool get darkTheme => _darkTheme;
  bool get notificationsEnabled => _notificationsEnabled;
  String get language => _language;

  Future<void> init() async {
    final prefs = await SharedPreferences.getInstance();
    _darkTheme = prefs.getBool('darkTheme') ?? false;
    _notificationsEnabled = prefs.getBool('notifications') ?? true;
    _language = prefs.getString('language') ?? 'Español';
    notifyListeners();
  }

  Future<void> setDarkTheme(bool value) async {
    _darkTheme = value;
    notifyListeners();
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('darkTheme', value);
  }

  Future<void> setNotifications(bool value) async {
    _notificationsEnabled = value;
    notifyListeners();
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('notifications', value);
  }

  Future<void> setLanguage(String value) async {
    _language = value;
    notifyListeners();
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('language', value);
  }
}
