import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:intl/date_symbol_data_local.dart';
import 'package:provider/provider.dart';
import 'package:flutter_front/common/constants/app_colors.dart';
import 'package:flutter_front/domain/controller/chat_bot_controller.dart';
import 'package:flutter_front/domain/controller/restaurant_controller.dart';
import 'package:flutter_front/domain/controller/stay_accommodation_controller.dart';
import 'package:flutter_front/domain/controller/stay_reservation_controller.dart';
import 'package:flutter_front/domain/view/team_test_screen.dart';

// TODO [팀 병합 시]: 아래 주석 해제 후 위 import 제거
// import 'package:flutter_front/config/app_router.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await dotenv.load(fileName: '.env');
  await initializeDateFormatting('ko_KR', null);

  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => StayAccommodationController()),
        ChangeNotifierProvider(create: (_) => StayReservationController()),
        ChangeNotifierProvider(create: (_) => ChatBotController()),
        ChangeNotifierProvider(create: (_) => RestaurantController()),
      ],

      child: MaterialApp(
        title: '세컨하우스 - 팀 테스트',
        debugShowCheckedModeBanner: false,
        theme: ThemeData(
          colorScheme: ColorScheme.fromSeed(seedColor: AppColors.primary),
          useMaterial3: true,
          scaffoldBackgroundColor: AppColors.background,
        ),
        home: const TeamTestScreen(),
        // TODO [팀 병합 시]: home 대신 AppRouter() 사용
      ),
    ),
  );
}
