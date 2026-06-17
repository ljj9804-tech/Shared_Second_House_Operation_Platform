import 'package:dio/dio.dart';
import 'package:flutter_front/domain/dto/place_dto.dart';

/// 🍽️ 주변 맛집 검색 서비스 (Google Places 기반 백엔드 호출)
/// HTTP 단발성 요청/응답 구조
class PlacesService {
  // 안드로이드 에뮬레이터 전용 메인 백엔드 주소 (포트 8080)
  final String _baseUrl = "http://10.0.2.2:8080/api/places";
  final Dio _dio = Dio();

  /// 좌표+반경 기준 주변 맛집 목록 조회
  /// 백엔드: GET /api/places/restaurants?lat=&lng=&radius=&limit=
  Future<List<PlaceDto>> nearbyRestaurants({
    required double lat,
    required double lng,
    int radius = 1000,
    int limit = 10,
  }) async {
    print("🚀 [맛집 검색] lat: $lat, lng: $lng, radius: $radius, limit: $limit");

    final response = await _dio.get(
      "$_baseUrl/restaurants",
      queryParameters: {
        "lat": lat,
        "lng": lng,
        "radius": radius,
        "limit": limit,
      },
    );

    final List<dynamic> data = response.data as List<dynamic>;
    print("🟢 [맛집 검색 성공]: ${data.length}건");
    return data
        .map((e) => PlaceDto.fromJson(e as Map<String, dynamic>))
        .toList();
  }
}