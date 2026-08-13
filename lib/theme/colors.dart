import 'package:flutter/material.dart';

const Color kPrimary = Color(0xFF2E7D32);       // Verde profundo
const Color kPrimaryLight = Color(0xFF66BB6A);  // Verde claro
const Color kPrimaryVibrant = Color(0xFF43A047); // Verde medio
const Color kAccent = Color(0xFF00C853);         // Verde neón suave
const Color kSand = Color(0xFFF5F3E7);           // Fondo arena
const Color kSurface = Color(0xFFFFFFFF);
const Color kDarkBg = Color(0xFF121212);
const Color kDarkSurface = Color(0xFF1E1E1E);
const Color kGray = Color(0xFF9E9E9E);
const Color kTextDark = Color(0xFF1B1B1B);
const Color kTextLight = Color(0xFFF9FBE7);

// Gradient verde para fondos
const LinearGradient kGreenGradient = LinearGradient(
  begin: Alignment.topLeft,
  end: Alignment.bottomRight,
  colors: [Color(0xFF1B5E20), Color(0xFF388E3C), Color(0xFF66BB6A)],
);

const LinearGradient kCardGradient = LinearGradient(
  begin: Alignment.topLeft,
  end: Alignment.bottomRight,
  colors: [Color(0xFF2E7D32), Color(0xFF43A047)],
);
