import 'package:dio/dio.dart';
import 'package:flutter_front/config/app_config.dart';
import 'package:flutter_front/domain/dto/stay_accommodation_dto.dart';

class StayAccommodationService {
  final Dio _dio = Dio(BaseOptions(baseUrl: AppConfig.baseUrl));

  Future<List<StayAccommodationDto>> getAccommodations() async {
    final response = await _dio.get('/stay/accommodations');
    return (response.data as List)
        .map((e) => StayAccommodationDto.fromJson(e))
        .toList();
  }

  Future<StayAccommodationDto> getAccommodation(int id) async {
    final response = await _dio.get('/stay/accommodations/$id');
    return StayAccommodationDto.fromJson(response.data);
  }
}
