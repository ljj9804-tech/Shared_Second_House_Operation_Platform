import 'package:flutter/material.dart';
import 'package:flutter_front/domain/dto/stay_accommodation_dto.dart';
import 'package:flutter_front/domain/dto/stay_story_dto.dart';
import 'package:flutter_front/domain/dto/stay_subscription_dto.dart';
import 'package:flutter_front/domain/service/stay_accommodation_service.dart';
import 'package:flutter_front/domain/service/stay_story_service.dart';
import 'package:flutter_front/domain/service/stay_subscription_service.dart';

class StayAccommodationController extends ChangeNotifier {
  final StayAccommodationService _accommodationService = StayAccommodationService();
  final StayStoryService _storyService = StayStoryService();
  final SubscriptionService _subscriptionService = SubscriptionService();

  List<StayAccommodationDto> accommodations = [];
  StayAccommodationDto? selectedAccommodation;
  List<StayStoryDto> stories = [];
  List<StaySubscriptionDto> mySubscriptions = [];

  bool isLoadingList = false;
  bool isLoadingDetail = false;
  String? errorMessage;

  List<StayAccommodationDto> get mySubscribedAccommodations {
    final activeIds = mySubscriptions
        .where((s) => s.isActive)
        .map((s) => s.accommodationId)
        .toSet();
    return accommodations.where((a) => activeIds.contains(a.id)).toList();
  }

  // 숙소 목록 + 내 구독 목록 로드 (홈화면용)
  Future<void> loadAccommodationsAndFaqs({int? userId}) async {
    isLoadingList = true;
    errorMessage = null;
    notifyListeners();

    try {
      final futures = <Future>[
        _accommodationService.getAccommodations(),
        if (userId != null) _subscriptionService.getMySubscriptions(userId),
      ];
      final results = await Future.wait(futures);
      accommodations = results[0] as List<StayAccommodationDto>;
      if (userId != null) {
        final raw = results[1] as List<dynamic>;
        mySubscriptions = raw.map((e) => StaySubscriptionDto.fromJson(e as Map<String, dynamic>)).toList();
      }
    } catch (e) {
      errorMessage = '데이터를 불러오지 못했습니다.';
      debugPrint('❌ [숙소 컨트롤러] $e');
    }

    isLoadingList = false;
    notifyListeners();
  }

  // 숙소 상세 + 스토리 로드 (상세 화면용)
  Future<void> loadAccommodationDetail(int id) async {
    isLoadingDetail = true;
    selectedAccommodation = null;
    stories = [];
    errorMessage = null;
    notifyListeners();

    try {
      final results = await Future.wait([
        _accommodationService.getAccommodation(id),
        _storyService.getStories(id),
      ]);
      selectedAccommodation = results[0] as StayAccommodationDto;
      stories = results[1] as List<StayStoryDto>;
    } catch (e) {
      errorMessage = '숙소 정보를 불러오지 못했습니다.';
      debugPrint('❌ [숙소 상세 컨트롤러] $e');
    }

    isLoadingDetail = false;
    notifyListeners();
  }
}
