import 'package:flutter/material.dart';
import 'package:flutter_front/domain/view/main_screen.dart';
import 'package:flutter_front/domain/view/team_test_screen.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Flutter App',
      theme: ThemeData(
        useMaterial3: true,
      ),
      home: const TeamTestScreen(), //작업중: 테스트용 스크린 사용, 이후 main_screen.dart파일 경로로 수정
    );
  }
}