import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import '../providers/auth_provider.dart';
import '../providers/app_provider.dart';
import '../providers/profile_provider.dart';
import '../theme/colors.dart';
import '../widgets/bottom_nav_bar.dart';
import '../widgets/leafy_button.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<ProfileProvider>().loadStats();
    });
  }

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthProvider>();
    final app = context.watch<AppProvider>();
    final profile = context.watch<ProfileProvider>();
    final stats = profile.stats;
    final user = auth.currentUser;

    return Scaffold(
      body: CustomScrollView(
        slivers: [
          // Header
          SliverToBoxAdapter(
            child: Container(
              decoration: const BoxDecoration(
                gradient: kGreenGradient,
                borderRadius: BorderRadius.vertical(bottom: Radius.circular(36)),
              ),
              padding: EdgeInsets.only(
                top: MediaQuery.of(context).padding.top + 16,
                left: 24,
                right: 24,
                bottom: 32,
              ),
              child: Column(
                children: [
                  Row(
                    children: [
                      IconButton(
                        onPressed: () => context.go('/home'),
                        icon: const Icon(Icons.arrow_back,
                            color: Colors.white),
                      ),
                      const Spacer(),
                      const Text('Perfil',
                          style: TextStyle(
                              color: Colors.white,
                              fontSize: 20,
                              fontWeight: FontWeight.w700)),
                      const Spacer(),
                      const SizedBox(width: 48),
                    ],
                  ),
                  const SizedBox(height: 16),
                  // Avatar
                  CircleAvatar(
                    radius: 48,
                    backgroundColor: Colors.white.withOpacity(0.25),
                    child: Text(
                      user?.name.isNotEmpty == true
                          ? user!.name[0].toUpperCase()
                          : '🌿',
                      style: const TextStyle(
                          fontSize: 40, color: Colors.white),
                    ),
                  ),
                  const SizedBox(height: 12),
                  Text(
                    user?.name ?? 'Explorador botánico',
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 22,
                      fontWeight: FontWeight.w800,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    user?.email ?? 'Inicia sesión para más funciones',
                    style: TextStyle(
                        color: Colors.white.withOpacity(0.8),
                        fontSize: 14),
                  ),
                  const SizedBox(height: 20),
                  // Stats rápidos
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      _StatBubble(
                          label: 'Escaneos', value: '${stats.totalScans}'),
                      _StatBubble(
                          label: 'Familias',
                          value: '${stats.distinctFamilies}'),
                      _StatBubble(label: 'Nivel', value: '${stats.level}'),
                    ],
                  ),
                ],
              ),
            ),
          ),

          // XP Bar
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.fromLTRB(24, 24, 24, 0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text('Nivel ${stats.level}',
                          style: const TextStyle(
                              fontWeight: FontWeight.w700, fontSize: 15)),
                      Text('${stats.currentXp}/${stats.xpForNextLevel} XP',
                          style: const TextStyle(
                              color: kGray, fontSize: 13)),
                    ],
                  ),
                  const SizedBox(height: 8),
                  ClipRRect(
                    borderRadius: BorderRadius.circular(8),
                    child: LinearProgressIndicator(
                      value: stats.xpForNextLevel > 0
                          ? stats.currentXp / stats.xpForNextLevel
                          : 0,
                      minHeight: 10,
                      backgroundColor: const Color(0xFFE8F5E9),
                      valueColor: const AlwaysStoppedAnimation<Color>(kPrimary),
                    ),
                  ),
                  const SizedBox(height: 6),
                  if (stats.lastScanDate > 0)
                    Text(
                      'Último escaneo: ${DateFormat('dd MMM yyyy').format(DateTime.fromMillisecondsSinceEpoch(stats.lastScanDate))}',
                      style:
                          const TextStyle(color: kGray, fontSize: 12),
                    ),
                ],
              ),
            ),
          ),

          // Logros
          const SliverToBoxAdapter(
            child: Padding(
              padding: EdgeInsets.fromLTRB(24, 24, 24, 8),
              child: Text('🏆 Logros',
                  style: TextStyle(
                      fontSize: 17, fontWeight: FontWeight.w700)),
            ),
          ),
          SliverToBoxAdapter(
            child: SizedBox(
              height: 130,
              child: ListView.separated(
                padding: const EdgeInsets.symmetric(horizontal: 20),
                scrollDirection: Axis.horizontal,
                itemCount: stats.achievements.length,
                separatorBuilder: (_, __) => const SizedBox(width: 12),
                itemBuilder: (_, i) =>
                    _AchievementCard(achievement: stats.achievements[i]),
              ),
            ),
          ),

          // Configuración
          const SliverToBoxAdapter(
            child: Padding(
              padding: EdgeInsets.fromLTRB(24, 24, 24, 8),
              child: Text('⚙️ Configuración',
                  style: TextStyle(
                      fontSize: 17, fontWeight: FontWeight.w700)),
            ),
          ),
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20),
              child: _SettingsCard(app: app),
            ),
          ),

          // Botones de sesión
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.all(24),
              child: user != null
                  ? LeafyButton(
                      text: 'Cerrar sesión',
                      icon: Icons.logout,
                      onPressed: () async {
                        await auth.logout();
                        if (context.mounted) context.go('/home');
                      },
                    )
                  : LeafyButton(
                      text: 'Iniciar sesión / Registrarse',
                      icon: Icons.login,
                      onPressed: () => context.go('/login'),
                    ),
            ),
          ),
          const SliverToBoxAdapter(child: SizedBox(height: 8)),
        ],
      ),
      bottomNavigationBar: LeafyBottomNav(
        currentIndex: 3,
        onTap: (i) {
          if (i == 0) context.go('/home');
          if (i == 1) context.go('/history');
          if (i == 2) context.go('/explore');
        },
      ),
    );
  }
}

class _StatBubble extends StatelessWidget {
  final String label;
  final String value;

  const _StatBubble({required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.18),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Column(
        children: [
          Text(value,
              style: const TextStyle(
                  color: Colors.white,
                  fontSize: 22,
                  fontWeight: FontWeight.w800)),
          const SizedBox(height: 2),
          Text(label,
              style: const TextStyle(
                  color: Colors.white70, fontSize: 11)),
        ],
      ),
    );
  }
}

class _AchievementCard extends StatelessWidget {
  final Achievement achievement;

  const _AchievementCard({required this.achievement});

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    final unlocked = achievement.unlocked;

    return Container(
      width: 110,
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: unlocked
            ? (isDark ? kDarkSurface : kSurface)
            : (isDark ? const Color(0xFF1A1A1A) : const Color(0xFFF5F5F5)),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(
          color: unlocked ? kPrimary.withOpacity(0.3) : Colors.transparent,
        ),
        boxShadow: unlocked
            ? [
                BoxShadow(
                    color: kPrimary.withOpacity(0.15),
                    blurRadius: 8,
                    offset: const Offset(0, 3))
              ]
            : [],
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Text(
            achievement.emoji,
            style: TextStyle(
                fontSize: 30,
                color: unlocked ? null : const Color(0xFFBBBBBB)),
          ),
          const SizedBox(height: 6),
          Text(
            achievement.title,
            textAlign: TextAlign.center,
            style: TextStyle(
              fontSize: 11,
              fontWeight: FontWeight.w600,
              color: unlocked ? null : kGray,
            ),
            maxLines: 2,
          ),
          if (!unlocked)
            const Icon(Icons.lock_outline, size: 14, color: kGray),
        ],
      ),
    );
  }
}

class _SettingsCard extends StatelessWidget {
  final AppProvider app;

  const _SettingsCard({required this.app});

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: Theme.of(context).brightness == Brightness.dark
            ? kDarkSurface
            : kSurface,
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(
              color: Colors.black.withOpacity(0.05),
              blurRadius: 8,
              offset: const Offset(0, 2)),
        ],
      ),
      child: Column(
        children: [
          SwitchListTile.adaptive(
            title: const Text('Modo oscuro'),
            subtitle: const Text('Cambia el tema de la app'),
            secondary: const Icon(Icons.dark_mode_outlined, color: kPrimary),
            value: app.darkTheme,
            activeColor: kPrimary,
            onChanged: app.setDarkTheme,
          ),
          const Divider(height: 1, indent: 16, endIndent: 16),
          SwitchListTile.adaptive(
            title: const Text('Notificaciones'),
            subtitle: const Text('Recibe recordatorios y tips'),
            secondary: const Icon(Icons.notifications_outlined, color: kPrimary),
            value: app.notificationsEnabled,
            activeColor: kPrimary,
            onChanged: app.setNotifications,
          ),
          const Divider(height: 1, indent: 16, endIndent: 16),
          ListTile(
            leading: const Icon(Icons.language, color: kPrimary),
            title: const Text('Idioma'),
            subtitle: Text(app.language),
            trailing: const Icon(Icons.chevron_right, color: kGray),
            onTap: () => _showLanguagePicker(context, app),
          ),
        ],
      ),
    );
  }

  void _showLanguagePicker(BuildContext context, AppProvider app) {
    const languages = ['Español', 'English', 'Français', 'Deutsch'];
    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(
          borderRadius: BorderRadius.vertical(top: Radius.circular(24))),
      builder: (_) => Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Seleccionar idioma',
                style: TextStyle(
                    fontSize: 17, fontWeight: FontWeight.w700)),
            const SizedBox(height: 16),
            ...languages.map(
              (lang) => ListTile(
                title: Text(lang),
                trailing: app.language == lang
                    ? const Icon(Icons.check, color: kPrimary)
                    : null,
                onTap: () {
                  app.setLanguage(lang);
                  Navigator.of(context).pop();
                },
              ),
            ),
          ],
        ),
      ),
    );
  }
}
