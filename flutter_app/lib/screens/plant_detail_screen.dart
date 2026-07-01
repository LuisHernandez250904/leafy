import 'dart:io';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import '../models/plant_history.dart';
import '../providers/history_provider.dart';
import '../theme/colors.dart';

class PlantDetailScreen extends StatefulWidget {
  final String historyId;

  const PlantDetailScreen({super.key, required this.historyId});

  @override
  State<PlantDetailScreen> createState() => _PlantDetailScreenState();
}

class _PlantDetailScreenState extends State<PlantDetailScreen> {
  PlantHistory? _item;
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    final item =
        await context.read<HistoryProvider>().getById(widget.historyId);
    if (mounted) {
      setState(() {
        _item = item;
        _loading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : _item == null
              ? _NotFound(onBack: () => context.pop())
              : _buildDetail(_item!),
    );
  }

  Widget _buildDetail(PlantHistory item) {
    final pct = (item.confidence * 100).toInt();
    final date = item.scanDate != 0
        ? DateFormat('dd MMM yyyy, HH:mm')
            .format(DateTime.fromMillisecondsSinceEpoch(item.scanDate))
        : '-';

    return CustomScrollView(
      slivers: [
        // Imagen en AppBar
        SliverAppBar(
          expandedHeight: 320,
          pinned: true,
          leading: GestureDetector(
            onTap: () => context.pop(),
            child: Container(
              margin: const EdgeInsets.all(8),
              decoration: BoxDecoration(
                color: Colors.black.withOpacity(0.35),
                shape: BoxShape.circle,
              ),
              child:
                  const Icon(Icons.arrow_back, color: Colors.white),
            ),
          ),
          flexibleSpace: FlexibleSpaceBar(
            background: _buildHeroImage(item),
          ),
        ),

        SliverToBoxAdapter(
          child: Padding(
            padding: const EdgeInsets.all(24),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Nombre y familia
                Row(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            item.commonName.isNotEmpty
                                ? item.commonName
                                : item.plantName,
                            style: const TextStyle(
                              fontSize: 26,
                              fontWeight: FontWeight.w800,
                            ),
                          ),
                          const SizedBox(height: 4),
                          Text(
                            item.plantName,
                            style: const TextStyle(
                              fontSize: 15,
                              color: kGray,
                              fontStyle: FontStyle.italic,
                            ),
                          ),
                        ],
                      ),
                    ),
                    // Badge confianza
                    Container(
                      padding: const EdgeInsets.symmetric(
                          horizontal: 14, vertical: 8),
                      decoration: BoxDecoration(
                        gradient: kCardGradient,
                        borderRadius: BorderRadius.circular(20),
                      ),
                      child: Text(
                        '$pct%',
                        style: const TextStyle(
                          color: Colors.white,
                          fontWeight: FontWeight.w800,
                          fontSize: 18,
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 24),
                // Tarjetas de info
                _InfoGrid(item: item, date: date),
                const SizedBox(height: 24),
                // Descripción / Género
                if (item.description.isNotEmpty) ...[
                  const Text(
                    'Género botánico',
                    style: TextStyle(
                        fontSize: 17, fontWeight: FontWeight.w700),
                  ),
                  const SizedBox(height: 10),
                  Container(
                    width: double.infinity,
                    padding: const EdgeInsets.all(18),
                    decoration: BoxDecoration(
                      color: const Color(0xFFE8F5E9),
                      borderRadius: BorderRadius.circular(18),
                    ),
                    child: Text(
                      item.description,
                      style: const TextStyle(
                          fontSize: 15, height: 1.5, color: kPrimary),
                    ),
                  ),
                  const SizedBox(height: 24),
                ],
                // Barra de confianza
                const Text(
                  'Precisión del análisis',
                  style: TextStyle(
                      fontSize: 17, fontWeight: FontWeight.w700),
                ),
                const SizedBox(height: 12),
                _ConfidenceBar(pct: pct),
                const SizedBox(height: 32),
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildHeroImage(PlantHistory item) {
    if (item.imageUrl.isEmpty) {
      return Container(
        decoration: const BoxDecoration(gradient: kGreenGradient),
        child: const Center(
          child: Icon(Icons.local_florist, size: 80, color: Colors.white),
        ),
      );
    }
    if (item.imageUrl.startsWith('http')) {
      return CachedNetworkImage(
        imageUrl: item.imageUrl,
        fit: BoxFit.cover,
        placeholder: (_, __) => Container(
          color: const Color(0xFFE8F5E9),
          child: const Center(child: CircularProgressIndicator()),
        ),
        errorWidget: (_, __, ___) => Container(
          decoration: const BoxDecoration(gradient: kGreenGradient),
          child: const Center(
              child: Icon(Icons.local_florist,
                  size: 80, color: Colors.white)),
        ),
      );
    }
    // Imagen local
    return Image.file(
      File(item.imageUrl),
      fit: BoxFit.cover,
      errorBuilder: (_, __, ___) => Container(
        decoration: const BoxDecoration(gradient: kGreenGradient),
        child: const Center(
            child:
                Icon(Icons.local_florist, size: 80, color: Colors.white)),
      ),
    );
  }
}

class _InfoGrid extends StatelessWidget {
  final PlantHistory item;
  final String date;

  const _InfoGrid({required this.item, required this.date});

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    return GridView.count(
      crossAxisCount: 2,
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      crossAxisSpacing: 12,
      mainAxisSpacing: 12,
      childAspectRatio: 2.0,
      children: [
        _InfoTile(
          icon: Icons.category_outlined,
          label: 'Familia',
          value:
              item.familyName.isNotEmpty ? item.familyName : '-',
          isDark: isDark,
        ),
        _InfoTile(
          icon: Icons.bar_chart,
          label: 'Confianza',
          value: '${(item.confidence * 100).toInt()}%',
          isDark: isDark,
        ),
        _InfoTile(
          icon: Icons.calendar_today_outlined,
          label: 'Fecha',
          value: date,
          isDark: isDark,
        ),
        _InfoTile(
          icon: Icons.eco_outlined,
          label: 'Nombre científico',
          value: item.plantName.isNotEmpty ? item.plantName : '-',
          isDark: isDark,
        ),
      ],
    );
  }
}

class _InfoTile extends StatelessWidget {
  final IconData icon;
  final String label;
  final String value;
  final bool isDark;

  const _InfoTile({
    required this.icon,
    required this.label,
    required this.value,
    required this.isDark,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(12),
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
      child: Row(
        children: [
          Icon(icon, color: kPrimary, size: 20),
          const SizedBox(width: 8),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text(label,
                    style: const TextStyle(
                        fontSize: 10, color: kGray)),
                Text(
                  value,
                  style: const TextStyle(
                      fontSize: 12, fontWeight: FontWeight.w600),
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _ConfidenceBar extends StatelessWidget {
  final int pct;
  const _ConfidenceBar({required this.pct});

  @override
  Widget build(BuildContext context) {
    final color =
        pct > 70 ? kAccent : pct > 40 ? Colors.orange : Colors.red;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        ClipRRect(
          borderRadius: BorderRadius.circular(10),
          child: LinearProgressIndicator(
            value: pct / 100,
            minHeight: 12,
            backgroundColor: const Color(0xFFE8F5E9),
            valueColor: AlwaysStoppedAnimation<Color>(color),
          ),
        ),
        const SizedBox(height: 6),
        Text(
          pct > 70
              ? 'Alta confianza — resultado muy probable'
              : pct > 40
                  ? 'Confianza media — podría ser otra especie'
                  : 'Confianza baja — se recomienda verificar',
          style: TextStyle(fontSize: 12, color: color),
        ),
      ],
    );
  }
}

class _NotFound extends StatelessWidget {
  final VoidCallback onBack;
  const _NotFound({required this.onBack});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.search_off, size: 64, color: kGray),
          const SizedBox(height: 16),
          const Text('Planta no encontrada',
              style: TextStyle(
                  fontSize: 18, fontWeight: FontWeight.w700)),
          const SizedBox(height: 20),
          ElevatedButton(
            onPressed: onBack,
            child: const Text('Volver'),
          ),
        ],
      ),
    );
  }
}
