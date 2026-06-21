import 'package:flutter/foundation.dart';
import 'package:flutter_front/domain/dto/place_dto.dart';
import 'package:flutter_front/domain/service/places_service.dart';

/// 🍽️ 맛집 상태 관리 컨트롤러 (ChangeNotifier)
/// 받아온 맛집 목록을 캐싱해, 화면 재진입 시 백엔드(=Google Places, 호출당 과금)를
/// 다시 부르지 않는다. 비용 절감 + 즉시 표시.
class RestaurantController extends ChangeNotifier {
  final PlacesService _placesService = PlacesService();

  List<PlaceDto> _places = [];
  bool _isLoading = false;
  bool _loaded = false; // 한 번이라도 성공적으로 받아왔는지 (캐시 여부)
  String? _error;

  List<PlaceDto> get places => _places;
  bool get isLoading => _isLoading;
  String? get error => _error;

  /// 맛집 목록 로드. 이미 받아온 적 있으면(캐시) 재호출하지 않는다.
  Future<void> loadRestaurants({
    required double lat,
    required double lng,
    bool forceRefresh = false,
  }) async {
    if (_loaded && !forceRefresh) return; // 캐시 재사용 → API 비용 절약

    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _places = await _placesService.nearbyRestaurants(lat: lat, lng: lng);
      _loaded = true;
    } catch (e) {
      _error = '맛집 정보를 가져오지 못했어요. 😢';
      debugPrint('🔴 [맛집 컨트롤러] 로드 실패: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }
}