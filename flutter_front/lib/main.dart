import 'package:flutter/foundation.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:intl/date_symbol_data_local.dart';
import 'package:flutter/material.dart';
import 'package:flutter_front/config/app_config.dart';
import 'package:flutter_front/config/app_router.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await dotenv.load(fileName: '.env');
  await initializeDateFormatting('ko_KR', null);

  debugPrint('🔑 현재 테스트 userId: ${AppConfig.tempUserId}');

  runApp(const AppRouter());
}
