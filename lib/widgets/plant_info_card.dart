import 'package:flutter/material.dart';
import '../models/plant_history.dart';
import '../theme/colors.dart';

class PlantInfoCard extends StatelessWidget {
  final PlantHistory result;

  const PlantInfoCard({super.key, required this.result});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        gradient: kCardGradient,
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(
            color: kPrimary.withOpacity(0.3),
            blurRadius: 12,
            offset: const Offset(0, 6),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            result.commonName.isNotEmpty ? result.commonName : 'Desconocido',
            style: const TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.w700,
              color: Colors.white,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            result.plantName,
            style: const TextStyle(
              fontSize: 13,
              color: Colors.white70,
              fontStyle: FontStyle.italic,
            ),
          ),
          const SizedBox(height: 8),
          Row(
            children: [
              _InfoChip(
                label:
                    'Confianza: ${(result.confidence * 100).toInt()}%',
                icon: Icons.verified,
              ),
              const SizedBox(width: 8),
              if (result.familyName.isNotEmpty)
                _InfoChip(label: result.familyName, icon: Icons.category),
            ],
          ),
          if (result.description.isNotEmpty) ...[
            const SizedBox(height: 8),
            Text(
              result.description,
              style: const TextStyle(fontSize: 12, color: Colors.white70),
            ),
          ],
        ],
      ),
    );
  }
}

class _InfoChip extends StatelessWidget {
  final String label;
  final IconData icon;

  const _InfoChip({required this.label, required this.icon});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.2),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 13, color: Colors.white),
          const SizedBox(width: 4),
          Text(label,
              style: const TextStyle(fontSize: 11, color: Colors.white)),
        ],
      ),
    );
  }
}
