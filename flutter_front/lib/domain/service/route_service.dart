import 'package:dio/dio.dart';
import 'package:flutter_front/domain/dto/route_dto.dart';

/// 🗺️ 이동경로 추적 서비스 (백엔드 /api/routes 호출)
///
/// ⚠️ baseUrl을 생성자로 받는 이유:
/// 이 서비스는 UI isolate뿐 아니라 flutter_foreground_task의 별도 isolate에서도 쓰인다.
/// 그 isolate에는 dotenv가 로드돼 있지 않으므로 AppConfig를 직접 참조하지 않고
/// 호출 측에서 baseUrl을 넘겨받는다.
class RouteService {
  final String baseUrl; // 예: http://10.0.2.2:8080/api
  final Dio _dio = Dio();

  RouteService(this.baseUrl);

  String get _root => '$baseUrl/routes';

  /// 추적 시작 — 새 세션 생성 후 sessionId 반환
  Future<int> startSession(int userId) async {
    final res = await _dio.post(
      '$_root/sessions',
      queryParameters: {'userId': userId},
    );
    return (res.data['sessionId'] as num).toInt();
  }

  /// 좌표 묶음 저장
  Future<int> addPoints(int sessionId, List<RoutePointData> points) async {
    if (points.isEmpty) return 0;
    final res = await _dio.post(
      '$_root/sessions/$sessionId/points',
      data: points.map((p) => p.toJson()).toList(),
    );
    return (res.data['saved'] as num?)?.toInt() ?? 0;
  }

  /// 추적 종료
  Future<void> endSession(int sessionId) async {
    await _dio.post('$_root/sessions/$sessionId/end');
  }

  /// 세션 좌표 조회 (폴리라인용)
  Future<List<RoutePointData>> getPoints(int sessionId) async {
    final res = await _dio.get('$_root/sessions/$sessionId/points');
    final List<dynamic> data = res.data as List<dynamic>;
    return data
        .map((e) => RoutePointData.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  /// 세션 이력 조회 (최신순)
  Future<List<RouteSessionData>> getSessions(int userId) async {
    final res = await _dio.get(
      '$_root/sessions',
      queryParameters: {'userId': userId},
    );
    final List<dynamic> data = res.data as List<dynamic>;
    return data
        .map((e) => RouteSessionData.fromJson(e as Map<String, dynamic>))
        .toList();
  }
}