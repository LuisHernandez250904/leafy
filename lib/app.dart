import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import 'providers/app_provider.dart';
import 'screens/splash_screen.dart';
import 'screens/home_screen.dart';
import 'screens/login_screen.dart';
import 'screens/history_screen.dart';
import 'screens/plant_detail_screen.dart';
import 'screens/explore_screen.dart';
import 'screens/profile_screen.dart';
import 'theme/theme.dart';

final _router = GoRouter(
  initialLocation: '/splash',
  routes: [
    GoRoute(
      path: '/splash',
      builder: (_, __) => const SplashScreen(),
    ),
    GoRoute(
      path: '/home',
      builder: (_, __) => const HomeScreen(),
    ),
    GoRoute(
      path: '/login',
      builder: (_, __) => const LoginScreen(),
    ),
    GoRoute(
      path: '/history',
      builder: (_, __) => const HistoryScreen(),
    ),
    GoRoute(
      path: '/detail/:id',
      builder: (_, state) => PlantDetailScreen(
        historyId: state.pathParameters['id'] ?? '',
      ),
    ),
    GoRoute(
      path: '/explore',
      builder: (_, __) => const ExploreScreen(),
    ),
    GoRoute(
      path: '/profile',
      builder: (_, __) => const ProfileScreen(),
    ),
  ],
);

class LeafyApp extends StatelessWidget {
  const LeafyApp({super.key});

  @override
  Widget build(BuildContext context) {
    final app = context.watch<AppProvider>();
    return MaterialApp.router(
      title: 'Leafy',
      debugShowCheckedModeBanner: false,
      theme: lightTheme(),
      darkTheme: darkTheme(),
      themeMode: app.darkTheme ? ThemeMode.dark : ThemeMode.light,
      routerConfig: _router,
    );
  }
}
