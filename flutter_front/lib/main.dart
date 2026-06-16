// lib/main.dart
import 'package:flutter/material.dart';
import 'domain/service/delivery_service.dart'; // 방금 만든 서비스 임포트

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: '배달 프로젝트 리더 전용 앱',
      theme: ThemeData(primarySwatch: Colors.blue),
      home: const DeliveryTestScreen(),
    );
  }
}

class DeliveryTestScreen extends StatefulWidget {
  const DeliveryTestScreen({super.key});

  @override
  State<DeliveryTestScreen> createState() => _DeliveryTestScreenState();
}

class _DeliveryTestScreenState extends State<DeliveryTestScreen> {
  final DeliveryService _deliveryService = DeliveryService();
  String _currentStatus = "주문 대기 중";
  bool _isLoading = false;

  // 버튼 누를 때 실행할 비동기 핸들러 함수
  void _changeStatus(int orderId, String targetStatus) async {
    setState(() {
      _isLoading = true;
    });

    // 서버로 배송 상태 변경 요청 전송!
    bool success = await _deliveryService.updateDeliveryStatus(orderId, targetStatus);

    setState(() {
      _isLoading = false;
      if (success) {
        _currentStatus = targetStatus;
      } else {
        _currentStatus = "통신 실패 ❌ (도커나 서버를 확인하세요!)";
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("🚚 배달 주문 상태 제어판"),
        centerTitle: true,
      ),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(20.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Text(
                "현재 [6번 주문] 배달 상태",
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 10),

              // 현재 상태 표시 텍스트
              Text(
                _currentStatus,
                style: TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                    color: _currentStatus == "배송중" ? Colors.orange : Colors.green
                ),
              ),
              const SizedBox(height: 40),

              if (_isLoading) const CircularProgressIndicator(),

              if (!_isLoading) ...[
                // 1. 배송중으로 변경하는 버튼
                SizedBox(
                  width: double.infinity,
                  height: 55,
                  child: ElevatedButton.icon(
                    style: ElevatedButton.styleFrom(backgroundColor: Colors.orange),
                    onPressed: () => _changeStatus(6, "배송중"),
                    icon: const Icon(Icons.delivery_dining, color: Colors.white),
                    label: const Text("배송 시작하기 (배송중)", style: TextStyle(color: Colors.white, fontSize: 16)),
                  ),
                ),
                const SizedBox(height: 15),

                // 2. 배송완료로 변경하는 버튼
                SizedBox(
                  width: double.infinity,
                  height: 55,
                  child: ElevatedButton.icon(
                    style: ElevatedButton.styleFrom(backgroundColor: Colors.green),
                    onPressed: () => _changeStatus(6, "배송완료"),
                    icon: const Icon(Icons.check_circle, color: Colors.white),
                    label: const Text("배송 완료 처리", style: TextStyle(color: Colors.white, fontSize: 16)),
                  ),
                ),
              ]
            ],
          ),
        ),
      ),
    );
  }
}