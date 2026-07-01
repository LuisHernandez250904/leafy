import 'dart:io';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:image_picker/image_picker.dart';
import 'package:provider/provider.dart';
import '../models/plant_history.dart';
import '../providers/auth_provider.dart';
import '../providers/history_provider.dart';
import '../theme/colors.dart';
import '../widgets/bottom_nav_bar.dart';
import '../widgets/history_card.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final _picker = ImagePicker();
  int _navIndex = 0;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<HistoryProvider>().loadHistory();
    });
  }

  Future<void> _pickAndScan(ImageSource source) async {
    final picked = await _picker.pickImage(
      source: source,
      imageQuality: 85,
    );
    if (picked == null) return;

    final file = File(picked.path);
    final provider = context.read<HistoryProvider>();

    // Mostrar diálogo de carga
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (_) => const _ScanningDialog(),
    );

    final result = await provider.analyzeAndSave(
      imageFile: file,
    );

    if (mounted) Navigator.of(context).pop(); // cerrar diálogo

    if (result != null && mounted) {
      _showResultSheet(result);
    } else if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('No se pudo identificar la planta.'),
          backgroundColor: Colors.orange,
        ),
      );
    }
  }

  void _showResultSheet(PlantHistory result) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(28)),
      ),
      builder: (_) => _ResultSheet(result: result),
    );
  }

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthProvider>();
    final history = context.watch<HistoryProvider>();
    final recents = history.histories.take(3).toList();

    return Scaffold(
      body: _navIndex == 0
          ? _buildHomeBody(auth, recents)
          : _navIndex == 1
              ? _buildBody('Historial', context)
              : _navIndex == 2
                  ? _buildBody('Explorar', context)
                  : _buildBody('Perfil', context),
      bottomNavigationBar: LeafyBottomNav(
        currentIndex: _navIndex,
        onTap: (i) {
          if (i == 1) {
            context.go('/history');
            return;
          }
          if (i == 2) {
            context.go('/explore');
            return;
          }
          if (i == 3) {
            context.go('/profile');
            return;
          }
          setState(() => _navIndex = i);
        },
      ),
    );
  }

  Widget _buildHomeBody(AuthProvider auth, List<PlantHistory> recents) {
    return CustomScrollView(
      slivers: [
        // Header gradient
        SliverToBoxAdapter(
          child: Container(
            decoration: const BoxDecoration(
              gradient: kGreenGradient,
              borderRadius:
                  BorderRadius.vertical(bottom: Radius.circular(36)),
            ),
            padding: EdgeInsets.only(
              top: MediaQuery.of(context).padding.top + 16,
              left: 24,
              right: 24,
              bottom: 32,
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          'Hola, ${auth.currentUser?.name.split(' ').first ?? 'Explorador'} 👋',
                          style: const TextStyle(
                            fontSize: 22,
                            fontWeight: FontWeight.w800,
                            color: Colors.white,
                          ),
                        ),
                        const Text(
                          '¿Qué planta identificamos hoy?',
                          style: TextStyle(
                              color: Colors.white70, fontSize: 14),
                        ),
                      ],
                    ),
                    GestureDetector(
                      onTap: () => context.go('/profile'),
                      child: CircleAvatar(
                        radius: 24,
                        backgroundColor: Colors.white.withOpacity(0.25),
                        child: Text(
                          auth.currentUser?.name.isNotEmpty == true
                              ? auth.currentUser!.name[0].toUpperCase()
                              : '🌿',
                          style: const TextStyle(
                              fontSize: 20, color: Colors.white),
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 28),
                // Botones de escaneo
                Row(
                  children: [
                    Expanded(
                      child: _ScanButton(
                        icon: Icons.camera_alt,
                        label: 'Cámara',
                        onTap: () => _pickAndScan(ImageSource.camera),
                      ),
                    ),
                    const SizedBox(width: 16),
                    Expanded(
                      child: _ScanButton(
                        icon: Icons.photo_library,
                        label: 'Galería',
                        onTap: () => _pickAndScan(ImageSource.gallery),
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),

        // Estadística rápida
        SliverToBoxAdapter(
          child: Padding(
            padding: const EdgeInsets.fromLTRB(24, 24, 24, 8),
            child: Row(
              children: [
                _QuickStat(
                  label: 'Escaneos',
                  value:
                      '${context.watch<HistoryProvider>().histories.length}',
                  icon: Icons.qr_code_scanner,
                ),
                const SizedBox(width: 12),
                _QuickStat(
                  label: 'Familias',
                  value:
                      '${context.watch<HistoryProvider>().histories.map((h) => h.familyName).toSet().length}',
                  icon: Icons.category_outlined,
                ),
                const SizedBox(width: 12),
                _QuickStat(
                  label: 'Racha',
                  value: '🔥 1',
                  icon: Icons.local_fire_department,
                ),
              ],
            ),
          ),
        ),

        // Recientes
        SliverToBoxAdapter(
          child: Padding(
            padding: const EdgeInsets.fromLTRB(24, 20, 24, 8),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text(
                  'Escaneos recientes',
                  style: TextStyle(
                      fontSize: 17, fontWeight: FontWeight.w700),
                ),
                GestureDetector(
                  onTap: () => context.go('/history'),
                  child: const Text(
                    'Ver todos',
                    style: TextStyle(
                        color: kPrimary, fontWeight: FontWeight.w600),
                  ),
                ),
              ],
            ),
          ),
        ),

        if (context.watch<HistoryProvider>().isLoading)
          const SliverToBoxAdapter(
            child: Center(
                child: Padding(
              padding: EdgeInsets.all(32),
              child: CircularProgressIndicator(),
            )),
          )
        else if (recents.isEmpty)
          SliverToBoxAdapter(
            child: _EmptyState(onScan: () => _pickAndScan(ImageSource.camera)),
          )
        else
          SliverList(
            delegate: SliverChildBuilderDelegate(
              (_, i) => HistoryCard(
                history: recents[i],
                onTap: () => context.go('/detail/${recents[i].id}'),
              ),
              childCount: recents.length,
            ),
          ),

        const SliverToBoxAdapter(child: SizedBox(height: 20)),
      ],
    );
  }

  Widget _buildBody(String title, BuildContext context) {
    return Center(child: Text(title));
  }
}

class _ScanButton extends StatelessWidget {
  final IconData icon;
  final String label;
  final VoidCallback onTap;

  const _ScanButton(
      {required this.icon, required this.label, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 18),
        decoration: BoxDecoration(
          color: Colors.white.withOpacity(0.18),
          borderRadius: BorderRadius.circular(20),
          border:
              Border.all(color: Colors.white.withOpacity(0.3)),
        ),
        child: Column(
          children: [
            Icon(icon, color: Colors.white, size: 32),
            const SizedBox(height: 8),
            Text(label,
                style: const TextStyle(
                    color: Colors.white,
                    fontWeight: FontWeight.w600,
                    fontSize: 14)),
          ],
        ),
      ),
    );
  }
}

class _QuickStat extends StatelessWidget {
  final String label;
  final String value;
  final IconData icon;

  const _QuickStat(
      {required this.label, required this.value, required this.icon});

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    return Expanded(
      child: Container(
        padding: const EdgeInsets.all(14),
        decoration: BoxDecoration(
          color: isDark ? kDarkSurface : kSurface,
          borderRadius: BorderRadius.circular(16),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.05),
              blurRadius: 8,
              offset: const Offset(0, 2),
            ),
          ],
        ),
        child: Column(
          children: [
            Icon(icon, color: kPrimary, size: 22),
            const SizedBox(height: 6),
            Text(value,
                style: const TextStyle(
                    fontWeight: FontWeight.w700, fontSize: 16)),
            Text(label,
                style:
                    const TextStyle(fontSize: 11, color: kGray)),
          ],
        ),
      ),
    );
  }
}

class _EmptyState extends StatelessWidget {
  final VoidCallback onScan;
  const _EmptyState({required this.onScan});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(32),
      child: Column(
        children: [
          Container(
            width: 100,
            height: 100,
            decoration: BoxDecoration(
              color: const Color(0xFFE8F5E9),
              shape: BoxShape.circle,
            ),
            child: const Icon(Icons.local_florist,
                size: 54, color: kPrimaryLight),
          ),
          const SizedBox(height: 20),
          const Text(
            '¡Aún no hay escaneos!',
            style:
                TextStyle(fontSize: 18, fontWeight: FontWeight.w700),
          ),
          const SizedBox(height: 8),
          const Text(
            'Usa la cámara o galería para\nidentificar tu primera planta 🌿',
            textAlign: TextAlign.center,
            style: TextStyle(color: kGray, fontSize: 14),
          ),
          const SizedBox(height: 24),
          ElevatedButton.icon(
            onPressed: onScan,
            icon: const Icon(Icons.camera_alt),
            label: const Text('Escanear ahora'),
          ),
        ],
      ),
    );
  }
}

class _ScanningDialog extends StatelessWidget {
  const _ScanningDialog();

  @override
  Widget build(BuildContext context) {
    return Dialog(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      child: Padding(
        padding: const EdgeInsets.all(32),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              width: 80,
              height: 80,
              decoration: BoxDecoration(
                color: const Color(0xFFE8F5E9),
                shape: BoxShape.circle,
              ),
              child: const Icon(Icons.local_florist,
                  size: 44, color: kPrimary),
            ),
            const SizedBox(height: 20),
            const Text(
              'Analizando planta...',
              style: TextStyle(
                  fontSize: 17, fontWeight: FontWeight.w700),
            ),
            const SizedBox(height: 8),
            const Text(
              'Consultando la base de datos\nbotánica de PlantNet',
              textAlign: TextAlign.center,
              style: TextStyle(color: kGray, fontSize: 13),
            ),
            const SizedBox(height: 20),
            const LinearProgressIndicator(
              backgroundColor: Color(0xFFE8F5E9),
              valueColor: AlwaysStoppedAnimation<Color>(kPrimary),
            ),
          ],
        ),
      ),
    );
  }
}

class _ResultSheet extends StatelessWidget {
  final PlantHistory result;
  const _ResultSheet({required this.result});

  @override
  Widget build(BuildContext context) {
    final pct = (result.confidence * 100).toInt();

    return DraggableScrollableSheet(
      expand: false,
      initialChildSize: 0.55,
      minChildSize: 0.4,
      maxChildSize: 0.85,
      builder: (_, controller) => Container(
        padding: const EdgeInsets.all(24),
        child: ListView(
          controller: controller,
          children: [
            Center(
              child: Container(
                width: 40,
                height: 4,
                decoration: BoxDecoration(
                  color: Colors.grey.shade300,
                  borderRadius: BorderRadius.circular(2),
                ),
              ),
            ),
            const SizedBox(height: 20),
            Row(
              children: [
                Container(
                  width: 60,
                  height: 60,
                  decoration: BoxDecoration(
                    gradient: kCardGradient,
                    borderRadius: BorderRadius.circular(18),
                  ),
                  child: const Icon(Icons.local_florist,
                      color: Colors.white, size: 32),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        result.commonName.isNotEmpty
                            ? result.commonName
                            : result.plantName,
                        style: const TextStyle(
                          fontSize: 20,
                          fontWeight: FontWeight.w800,
                        ),
                      ),
                      Text(
                        result.plantName,
                        style: const TextStyle(
                            color: kGray,
                            fontStyle: FontStyle.italic,
                            fontSize: 13),
                      ),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 20),
            _InfoRow(Icons.verified, 'Confianza', '$pct%',
                color: pct > 70 ? kAccent : Colors.orange),
            _InfoRow(Icons.category_outlined, 'Familia',
                result.familyName),
            _InfoRow(Icons.info_outline, 'Género', result.description),
            const SizedBox(height: 24),
            ElevatedButton.icon(
              onPressed: () {
                Navigator.of(context).pop();
                context.go('/detail/${result.id}');
              },
              icon: const Icon(Icons.open_in_new),
              label: const Text('Ver detalle completo'),
            ),
            const SizedBox(height: 12),
            TextButton(
              onPressed: () => Navigator.of(context).pop(),
              child: const Text('Cerrar'),
            ),
          ],
        ),
      ),
    );
  }
}

class _InfoRow extends StatelessWidget {
  final IconData icon;
  final String label;
  final String value;
  final Color? color;

  const _InfoRow(this.icon, this.label, this.value, {this.color});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        children: [
          Icon(icon, size: 20, color: color ?? kPrimary),
          const SizedBox(width: 12),
          Text('$label: ',
              style: const TextStyle(
                  fontWeight: FontWeight.w600, fontSize: 14)),
          Expanded(
            child: Text(
              value.isNotEmpty ? value : '-',
              style: TextStyle(
                  fontSize: 14,
                  color: color ?? Colors.grey.shade700),
            ),
          ),
        ],
      ),
    );
  }
}
