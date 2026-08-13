class Constants {
  // PlantNet API
  // Se puede sobreescribir sin tocar el código:
  //   flutter run --dart-define=PLANTNET_API_KEY=tu_key
  // Si no se pasa nada, usa el valor de defaultValue (para que siga
  // funcionando de una vez sin configuración extra).
  static const String plantNetApiKey = String.fromEnvironment(
    'PLANTNET_API_KEY',
    defaultValue: '2b10iSg4GTucfjDDNsChd6F',
  );
  static const String plantNetBaseUrl = 'https://my-api.plantnet.org/v2/';

  // ── Supabase ────────────────────────────────────────────────────────────────
  // Reemplaza defaultValue con los de tu proyecto en https://supabase.com
  // Settings → API → Project URL  y  Settings → API → anon/public key
  static const String supabaseUrl = String.fromEnvironment(
    'SUPABASE_URL',
    defaultValue: 'https://kttiizmgifxiohatmrwt.supabase.co',
  );
  static const String supabaseAnonKey = String.fromEnvironment(
    'SUPABASE_ANON_KEY',
    defaultValue: 'sb_publishable_ufmxBczip7j-x4hh5mW1MQ_sNiutyce',
  );

  // Nombre de la tabla y del bucket de Storage en Supabase
  static const String plantHistoryTable = 'plant_history';
  static const String plantImagesBucket = 'plant-images';
}
