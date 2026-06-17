// lib/domain/view/delivery_admin_screen.dart
import 'package:flutter/material.dart';
import 'package:flutter_front/domain/service/delivery_service.dart'; // 프로젝트 경로 기준

class DeliveryAdminScreen extends StatefulWidget {
  final int orderId; // 외부에서 주문 ID를 넘겨받을 수 있도록 설계

  const DeliveryAdminScreen({
    Key? key,
    this.orderId = 6, // 기본값은 테스트하기 편하게 6번 주문으로 세팅!
  }) : super(key: key);

  @override
  State<DeliveryAdminScreen> createState() => _DeliveryAdminScreenState();
}

class _DeliveryAdminScreenState extends State<DeliveryAdminScreen> {
  final DeliveryService _deliveryService = DeliveryService();
  String _currentStatus = "조회 중...";
  bool _isLoading = false;

  // 상태 변경 API 호출 및 UI 갱신 함수
  Future<void> _changeStatus(int orderId, String status) async {
    setState(() {
      _isLoading = true;
    });

    // 백엔드와 통신하는 서비스 레이어 호출 (PUT)
    bool success = await _deliveryService.updateDeliveryStatus(orderId, status);

    if (success) {
      setState(() {
        _currentStatus = status;
      });
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("$status 업데이트 실패. 서버 설정을 확인하세요.")),
      );
    }

    setState(() {
      _isLoading = false;
    });
  }

  @override
  void initState() {
    super.initState();
    // 초기 뷰 로딩 시 상태값 기본 세팅
    _currentStatus = "주문 대기 중";
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
              Text(
                "현재 [${widget.orderId}번 주문] 배달 상태",
                style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 10),

              // 배달 상태를 보여주는 텍스트 존
              Text(
                _currentStatus,
                style: TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                  color: _currentStatus == "배송중"
                      ? Colors.orange
                      : (_currentStatus == "배송완료" ? Colors.green : Colors.black),
                ),
              ),
              const SizedBox(height: 40),

              // 로딩 중일 때 인디케이터 표시
              if (_isLoading) const CircularProgressIndicator(),

              if (!_isLoading) ...[
                // 버튼 1: 배송중 변경
                SizedBox(
                  width: double.infinity,
                  height: 55,
                  child: ElevatedButton.icon(
                    style: ElevatedButton.styleFrom(backgroundColor: Colors.orange),
                    onPressed: () => _changeStatus(widget.orderId, "배송중"),
                    icon: const Icon(Icons.delivery_dining, color: Colors.white),
                    label: const Text("배송 시작하기 (배송중)", style: TextStyle(color: Colors.white, fontSize: 16)),
                  ),
                ),
                const SizedBox(height: 15),

                // 버튼 2: 배송완료 변경
                SizedBox(
                  width: double.infinity,
                  height: 55,
                  child: ElevatedButton.icon(
                    style: ElevatedButton.styleFrom(backgroundColor: Colors.green),
                    onPressed: () => _changeStatus(widget.orderId, "배송완료"),
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