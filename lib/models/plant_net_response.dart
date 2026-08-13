class PlantNetResponse {
  final List<PlantNetResult> results;

  const PlantNetResponse({this.results = const []});

  factory PlantNetResponse.fromJson(Map<String, dynamic> json) {
    final list = (json['results'] as List<dynamic>? ?? [])
        .map((e) => PlantNetResult.fromJson(e as Map<String, dynamic>))
        .toList();
    return PlantNetResponse(results: list);
  }
}

class PlantNetResult {
  final double score;
  final Species species;

  const PlantNetResult({this.score = 0.0, this.species = const Species()});

  factory PlantNetResult.fromJson(Map<String, dynamic> json) {
    return PlantNetResult(
      score: (json['score'] as num?)?.toDouble() ?? 0.0,
      species: json['species'] != null
          ? Species.fromJson(json['species'] as Map<String, dynamic>)
          : const Species(),
    );
  }
}

class Species {
  final String scientificName;
  final List<String> commonNames;
  final Taxon? family;
  final Taxon? genus;

  const Species({
    this.scientificName = '',
    this.commonNames = const [],
    this.family,
    this.genus,
  });

  factory Species.fromJson(Map<String, dynamic> json) {
    return Species(
      scientificName: json['scientificName']?.toString() ?? '',
      commonNames: (json['commonNames'] as List<dynamic>? ?? [])
          .map((e) => e.toString())
          .toList(),
      family: json['family'] != null
          ? Taxon.fromJson(json['family'] as Map<String, dynamic>)
          : null,
      genus: json['genus'] != null
          ? Taxon.fromJson(json['genus'] as Map<String, dynamic>)
          : null,
    );
  }
}

class Taxon {
  final String scientificName;

  const Taxon({this.scientificName = ''});

  factory Taxon.fromJson(Map<String, dynamic> json) {
    return Taxon(scientificName: json['scientificName']?.toString() ?? '');
  }
}
