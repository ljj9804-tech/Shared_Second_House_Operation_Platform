import 'package:flutter/material.dart';
import 'package:flutter_front/domain/view/main_screen.dart';
import 'package:flutter_front/domain/view/team_test_screen.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

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
      home: const TeamTestScreen(), //작업중: 테스트용 스크린 사용, 이후 main_screen.dart파일 경로로 수정
    );
  }
}