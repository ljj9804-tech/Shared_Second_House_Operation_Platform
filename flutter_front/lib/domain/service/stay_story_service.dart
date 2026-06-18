import 'package:dio/dio.dart';
import 'package:flutter_front/config/app_config.dart';
import 'package:flutter_front/domain/dto/stay_story_dto.dart';

class StayStoryService {
  final Dio _dio = Dio(BaseOptions(baseUrl: AppConfig.baseUrl));

  Future<List<StayStoryDto>> getStories(int accommodationId) async {
    final response = await _dio.get('/stay/stories/$accommodationId');
    return (response.data as List)
        .map((e) => StayStoryDto.fromJson(e))
        .toList();
  }
}
