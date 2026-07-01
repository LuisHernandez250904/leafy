class ScanResult {
  final String? commonName;
  final String? scientificName;
  final double? confidence;
  final String? description;

  const ScanResult({
    this.commonName,
    this.scientificName,
    this.confidence,
    this.description,
  });
}
