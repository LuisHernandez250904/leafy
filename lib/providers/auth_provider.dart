import 'package:flutter/material.dart';
import '../models/user_model.dart';
import '../services/supabase_service.dart';

enum AuthStatus { idle, loading, success, error }

class AuthProvider extends ChangeNotifier {
  final SupabaseService _service = SupabaseService();

  String name = '';
  String email = '';
  String password = '';
  bool isLoginMode = true;
  AuthStatus status = AuthStatus.idle;
  String? errorMessage;

  UserModel? get currentUser => _service.currentUserModel;
  bool get isLoggedIn => _service.currentUser != null;

  void setName(String v) {
    name = v;
    notifyListeners();
  }

  void setEmail(String v) {
    email = v;
    notifyListeners();
  }

  void setPassword(String v) {
    password = v;
    notifyListeners();
  }

  void toggleMode() {
    isLoginMode = !isLoginMode;
    errorMessage = null;
    notifyListeners();
  }

  void reset() {
    name = '';
    email = '';
    password = '';
    isLoginMode = true;
    status = AuthStatus.idle;
    errorMessage = null;
    notifyListeners();
  }

  Future<bool> submit() async {
    final trimEmail = email.trim();
    final trimPassword = password.trim();
    final trimName = name.trim();

    if (trimEmail.isEmpty || trimPassword.isEmpty ||
        (!isLoginMode && trimName.isEmpty)) {
      errorMessage = 'Por favor completa todos los campos';
      notifyListeners();
      return false;
    }

    status = AuthStatus.loading;
    errorMessage = null;
    notifyListeners();

    UserModel? user;
    if (isLoginMode) {
      user = await _service.login(trimEmail, trimPassword);
    } else {
      user = await _service.register(trimName, trimEmail, trimPassword);
    }

    if (user != null) {
      status = AuthStatus.success;
      notifyListeners();
      return true;
    } else {
      status = AuthStatus.error;
      errorMessage = isLoginMode
          ? 'Error al iniciar sesión. Verifica tus credenciales.'
          : 'Error al registrarse. El email ya puede estar en uso.';
      notifyListeners();
      return false;
    }
  }

  Future<void> logout() async {
    await _service.logout();
    notifyListeners();
  }
}
