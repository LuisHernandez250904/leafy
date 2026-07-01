class PlantHistory {
  final String id;
  final String userId;
  final String plantName;
  final String commonName;
  final String familyName;
  final double confidence;
  final String description;
  final String imageUrl;
  final int scanDate; // milliseconds epoch

  const PlantHistory({
    this.id = '',
    this.userId = '',
    this.plantName = '',
    this.commonName = '',
    this.familyName = '',
    this.confidence = 0.0,
    this.description = '',
    this.imageUrl = '',
    this.scanDate = 0,
  });

  PlantHistory copyWith({
    String? id,
    String? userId,
    String? plantName,
    String? commonName,
    String? familyName,
    double? confidence,
    String? description,
    String? imageUrl,
    int? scanDate,
  }) {
    return PlantHistory(
      id: id ?? this.id,
      userId: userId ?? this.userId,
      plantName: plantName ?? this.plantName,
      commonName: commonName ?? this.commonName,
      familyName: familyName ?? this.familyName,
      confidence: confidence ?? this.confidence,
      description: description ?? this.description,
      imageUrl: imageUrl ?? this.imageUrl,
      scanDate: scanDate ?? this.scanDate,
    );
  }

  /// Convierte desde un Map de Supabase
  factory PlantHistory.fromMap(Map<String, dynamic> map) {
    return PlantHistory(
      id: map['id']?.toString() ?? '',
      userId: map['user_id']?.toString() ?? '',
      plantName: map['plant_name']?.toString() ?? '',
      commonName: map['common_name']?.toString() ?? '',
      familyName: map['family_name']?.toString() ?? '',
      confidence: (map['confidence'] as num?)?.toDouble() ?? 0.0,
      description: map['description']?.toString() ?? '',
      imageUrl: map['image_url']?.toString() ?? '',
      scanDate: (map['scan_date'] as num?)?.toInt() ?? 0,
    );
  }

  /// Convierte a Map para insertar en Supabase
  Map<String, dynamic> toMap() {
    return {
      'user_id': userId,
      'plant_name': plantName,
      'common_name': commonName,
      'family_name': familyName,
      'confidence': confidence,
      'description': description,
      'image_url': imageUrl,
      'scan_date': scanDate,
    };
  }
}
