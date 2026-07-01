import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:intl/intl.dart';
import '../models/plant_history.dart';
import '../theme/colors.dart';

class HistoryCard extends StatelessWidget {
  final PlantHistory history;
  final VoidCallback onTap;

  const HistoryCard({super.key, required this.history, required this.onTap});

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return GestureDetector(
      onTap: onTap,
      child: Container(
        margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        decoration: BoxDecoration(
          color: isDark ? kDarkSurface : kSurface,
          borderRadius: BorderRadius.circular(20),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.07),
              blurRadius: 12,
              offset: const Offset(0, 4),
            ),
          ],
        ),
        child: Row(
          children: [
            // Imagen
            ClipRRect(
              borderRadius: const BorderRadius.horizontal(
                left: Radius.circular(20),
              ),
              child: _buildImage(),
            ),
            // Info
            Expanded(
              child: Padding(
                padding: const EdgeInsets.all(14),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      history.commonName.isNotEmpty
                          ? history.commonName
                          : history.plantName,
                      style: TextStyle(
                        fontWeight: FontWeight.w700,
                        fontSize: 15,
                        color: isDark ? Colors.white : kPrimary,
                      ),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                    const SizedBox(height: 3),
                    if (history.plantName.isNotEmpty)
                      Text(
                        history.plantName,
                        style: TextStyle(
                            fontSize: 12,
                            color: isDark ? Colors.white54 : kGray,
                            fontStyle: FontStyle.italic),
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                      ),
                    if (history.familyName.isNotEmpty) ...[
                      const SizedBox(height: 2),
                      Text(
                        'Familia: ${history.familyName}',
                        style: TextStyle(
                            fontSize: 11,
                            color: isDark ? Colors.white38 : const Color(0xFF757575)),
                      ),
                    ],
                    const SizedBox(height: 6),
                    if (history.confidence > 0) ...[
                      _ConfidenceBadge(value: history.confidence),
                      const SizedBox(height: 4),
                    ],
                    Text(
                      _formatDate(history.scanDate),
                      style: TextStyle(
                          fontSize: 11,
                          color: isDark ? Colors.white38 : kGray),
                    ),
                  ],
                ),
              ),
            ),
            const Padding(
              padding: EdgeInsets.only(right: 12),
              child: Icon(Icons.chevron_right, color: kGray),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildImage() {
    if (history.imageUrl.isEmpty) {
      return Container(
        width: 90,
        height: 90,
        color: const Color(0xFFE8F5E9),
        child: const Icon(Icons.local_florist, color: kPrimaryLight, size: 40),
      );
    }
    if (history.imageUrl.startsWith('http')) {
      return CachedNetworkImage(
        imageUrl: history.imageUrl,
        width: 90,
        height: 90,
        fit: BoxFit.cover,
        placeholder: (_, __) => Container(
          width: 90,
          height: 90,
          color: const Color(0xFFE8F5E9),
          child: const Center(child: CircularProgressIndicator(strokeWidth: 2)),
        ),
        errorWidget: (_, __, ___) => Container(
          width: 90,
          height: 90,
          color: const Color(0xFFE8F5E9),
          child: const Icon(Icons.broken_image, color: kGray),
        ),
      );
    }
    // Imagen local (content URI no se puede mostrar con CachedNetworkImage)
    return Container(
      width: 90,
      height: 90,
      color: const Color(0xFFE8F5E9),
      child: const Icon(Icons.local_florist, color: kPrimaryLight, size: 40),
    );
  }

  String _formatDate(int timestamp) {
    if (timestamp == 0) return '-';
    final dt = DateTime.fromMillisecondsSinceEpoch(timestamp);
    return DateFormat('dd MMM yyyy, HH:mm').format(dt);
  }
}

class _ConfidenceBadge extends StatelessWidget {
  final double value;
  const _ConfidenceBadge({required this.value});

  @override
  Widget build(BuildContext context) {
    final pct = (value * 100).toInt();
    final color = pct > 70
        ? kAccent
        : pct > 40
            ? Colors.orange
            : Colors.red;
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
      decoration: BoxDecoration(
        color: color.withOpacity(0.12),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: color.withOpacity(0.4)),
      ),
      child: Text(
        'Confianza: $pct%',
        style: TextStyle(
            fontSize: 11, color: color, fontWeight: FontWeight.w600),
      ),
    );
  }
}
