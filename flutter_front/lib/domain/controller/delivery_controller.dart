// lib/domain/controller/delivery_controller.dart
import 'package:flutter/material.dart';
import 'package:flutter_front/domain/service/delivery_service.dart';

class DeliveryController extends ChangeNotifier {
  final DeliveryService _deliveryService = DeliveryService();

  // 현재 화면에 보여줄 배달 상태 변수 (기본값)
  String _currentStatus = "주문완료";
  bool _isLoading = false;

  // 외부에 데이터를 안전하게 제공하기 위한 Getter
  String get currentStatus => _currentStatus;
  bool get isLoading => _isLoading;

  /// 화면에서 "배송 시작" 버튼 등을 눌렀을 때 호출할 함수
  Future<void> changeDeliveryStatus(int orderId, String targetStatus) async {
    // 1. 로딩 상태 시작 알림
    _isLoading = true;
    notifyListeners(); // 이 함수를 호출해야 화면(View)이 갱신됩니다!

    // 2. 서비스 레이어를 통해 백엔드 서버와 통신 실행
    bool isSuccess = await _deliveryService.updateDeliveryStatus(orderId, targetStatus);

    // 3. 통신 결과에 따라 상태 업데이트 및 로딩 종료
    if (isSuccess) {
      _currentStatus = targetStatus;
      debugPrint("🟢 [컨트롤러] 상태 변경 성공 -> $targetStatus"); // print 대신 debugPrint!
    } else {
      debugPrint("🔴 [컨트롤러] 서버 통신 실패로 상태 변경 안 됨"); // print 대신 debugPrint!
    }

    _isLoading = false;
    notifyListeners(); // 최종 상태를 화면에 다시 전송!
  }
}