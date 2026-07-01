import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import '../models/plant_net_response.dart';
import '../utils/constants.dart';

class PlantNetService {
  static final PlantNetService _instance = PlantNetService._internal();
  factory PlantNetService() => _instance;
  PlantNetService._internal();

  /// Envía la imagen a PlantNet y retorna la respuesta parseada.
  /// Retorna null si ocurre un error.
  Future<PlantNetResponse?> identifyPlant(File imageFile) async {
    try {
      final uri = Uri.parse(
        '${Constants.plantNetBaseUrl}identify/all?api-key=${Constants.plantNetApiKey}',
      );

      final request = http.MultipartRequest('POST', uri);
      request.files.add(
        await http.MultipartFile.fromPath(
          'images',
          imageFile.path,
        ),
      );

      final streamed = await request.send();
      final response = await http.Response.fromStream(streamed);

      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        return PlantNetResponse.fromJson(json);
      } else {
        // ignore: avoid_print
        print('PlantNetService error ${response.statusCode}: ${response.body}');
        return null;
      }
    } catch (e) {
      // ignore: avoid_print
      print('PlantNetService exception: $e');
      return null;
    }
  }
}
