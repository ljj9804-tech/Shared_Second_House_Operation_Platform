import 'package:dio/dio.dart';
import 'package:flutter_front/domain/dto/delivery_order_dto.dart';

class DeliveryService {
  // 안드로이드 에뮬레이터 전용 백엔드 주소 (포트 8082)
  final String _baseUrl = "http://10.0.2.2:8082/api/delivery";
  final Dio _dio = Dio();

  /// 배달 상태 변경 API 호출 함수
  Future<bool> updateDeliveryStatus(int orderId, String status) async {
    try {
      print("🚀 [배달서버 통신] 상태 변경 요청 -> 주문번호: $orderId, 상태: $status");

      // 방금 만든 DTO 규격에 맞게 객체 생성
      final dto = DeliveryOrderDto(status: status);

      // PUT 요청 보내기
      final response = await _dio.put(
        "$_baseUrl/$orderId",
        data: dto.toJson(), // Map 데이터로 변환하여 전송
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        print("🟢 [배달서버 통신 성공]: ${response.data}");
        return true;
      }
      return false;
    } on DioException catch (e) {
      print("🔴 [배달서버 통신 실패]: ${e.message}");
      return false;
    }
  }
}