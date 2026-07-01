import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../theme/colors.dart';
import '../widgets/bottom_nav_bar.dart';

class ExploreScreen extends StatelessWidget {
  const ExploreScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Explorar'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go('/home'),
        ),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Banner informativo
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(20),
              decoration: BoxDecoration(
                gradient: kCardGradient,
                borderRadius: BorderRadius.circular(24),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Icon(Icons.tips_and_updates,
                      color: Colors.white, size: 32),
                  const SizedBox(height: 12),
                  const Text(
                    'Consejos para identificar plantas',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 18,
                      fontWeight: FontWeight.w700,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'Toma fotos con buena iluminación y enfoca las hojas, flores o frutos para mejores resultados.',
                    style: TextStyle(
                        color: Colors.white.withOpacity(0.85),
                        fontSize: 14),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),

            const _SectionTitle('📚 Sabías que...'),
            const SizedBox(height: 12),
            ..._facts.map((f) => _FactCard(fact: f)),

            const SizedBox(height: 24),
            const _SectionTitle('🌿 Categorías de plantas'),
            const SizedBox(height: 12),
            GridView.count(
              crossAxisCount: 2,
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              crossAxisSpacing: 12,
              mainAxisSpacing: 12,
              childAspectRatio: 1.3,
              children: _categories.map((c) => _CategoryCard(data: c)).toList(),
            ),

            const SizedBox(height: 24),
            const _SectionTitle('🔬 Cómo funciona PlantNet'),
            const SizedBox(height: 12),
            _HowItWorks(),
            const SizedBox(height: 24),
          ],
        ),
      ),
      bottomNavigationBar: LeafyBottomNav(
        currentIndex: 2,
        onTap: (i) {
          if (i == 0) context.go('/home');
          if (i == 1) context.go('/history');
          if (i == 3) context.go('/profile');
        },
      ),
    );
  }
}

class _SectionTitle extends StatelessWidget {
  final String text;
  const _SectionTitle(this.text);

  @override
  Widget build(BuildContext context) {
    return Text(text,
        style:
            const TextStyle(fontSize: 17, fontWeight: FontWeight.w700));
  }
}

class _FactCard extends StatelessWidget {
  final String fact;
  const _FactCard({required this.fact});

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    return Container(
      margin: const EdgeInsets.only(bottom: 10),
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: isDark ? kDarkSurface : kSurface,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
              color: Colors.black.withOpacity(0.05),
              blurRadius: 6,
              offset: const Offset(0, 2)),
        ],
      ),
      child: Row(
        children: [
          const Icon(Icons.eco, color: kPrimary, size: 22),
          const SizedBox(width: 12),
          Expanded(
            child:
                Text(fact, style: const TextStyle(fontSize: 13, height: 1.4)),
          ),
        ],
      ),
    );
  }
}

class _CategoryCard extends StatelessWidget {
  final Map<String, dynamic> data;
  const _CategoryCard({required this.data});

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    return Container(
      decoration: BoxDecoration(
        color: isDark ? kDarkSurface : kSurface,
        borderRadius: BorderRadius.circular(18),
        boxShadow: [
          BoxShadow(
              color: Colors.black.withOpacity(0.06),
              blurRadius: 8,
              offset: const Offset(0, 3)),
        ],
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Text(data['emoji'] as String, style: const TextStyle(fontSize: 36)),
          const SizedBox(height: 8),
          Text(data['name'] as String,
              style: const TextStyle(
                  fontWeight: FontWeight.w600, fontSize: 13)),
          Text(data['count'] as String,
              style: const TextStyle(color: kGray, fontSize: 11)),
        ],
      ),
    );
  }
}

class _HowItWorks extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final steps = [
      {'n': '1', 'text': 'Tomas una foto de la planta con la cámara o galería'},
      {'n': '2', 'text': 'Leafy envía la imagen a la API de PlantNet'},
      {'n': '3', 'text': 'PlantNet compara con su base de datos botánica'},
      {'n': '4', 'text': 'Recibes el resultado con nombre y confianza'},
      {'n': '5', 'text': 'El escaneo se guarda en Supabase automáticamente'},
    ];

    return Column(
      children: steps.map((s) {
        return Padding(
          padding: const EdgeInsets.only(bottom: 12),
          child: Row(
            children: [
              Container(
                width: 32,
                height: 32,
                decoration: BoxDecoration(
                  gradient: kCardGradient,
                  shape: BoxShape.circle,
                ),
                child: Center(
                  child: Text(s['n']!,
                      style: const TextStyle(
                          color: Colors.white,
                          fontWeight: FontWeight.w700)),
                ),
              ),
              const SizedBox(width: 14),
              Expanded(
                child: Text(s['text']!,
                    style: const TextStyle(fontSize: 14, height: 1.3)),
              ),
            ],
          ),
        );
      }).toList(),
    );
  }
}

const List<String> _facts = [
  'Hay más de 390.000 especies de plantas conocidas en el mundo.',
  'Las plantas producen el 70% del oxígeno de la Tierra.',
  'El bambú puede crecer hasta 91 cm en solo un día.',
  'La Victoria amazonica puede soportar hasta 40 kg sobre su hoja.',
  'Las orquídeas son la familia más grande de plantas con flores (25.000+ especies).',
];

const List<Map<String, dynamic>> _categories = [
  {'emoji': '🌺', 'name': 'Flores', 'count': '+12.000 sp.'},
  {'emoji': '🌵', 'name': 'Cactus', 'count': '+1.750 sp.'},
  {'emoji': '🌲', 'name': 'Árboles', 'count': '+60.000 sp.'},
  {'emoji': '🍄', 'name': 'Hongos', 'count': '+5.000 sp.'},
  {'emoji': '🌿', 'name': 'Hierbas', 'count': '+3.000 sp.'},
  {'emoji': '🪴', 'name': 'Interior', 'count': '+1.200 sp.'},
];
