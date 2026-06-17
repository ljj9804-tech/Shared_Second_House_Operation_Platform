import 'package:dio/dio.dart';
import 'package:flutter_front/config/app_config.dart';
import 'package:flutter_front/domain/dto/stay_reservation_dto.dart';

class StayReservationService {
  final Dio _dio = Dio(BaseOptions(baseUrl: AppConfig.baseUrl));

  Future<List<StayReservationDto>> getMyReservations() async {
    final response = await _dio.get('/stay/reservations');
    return (response.data as List)
        .map((e) => StayReservationDto.fromJson(e))
        .toList();
  }

  Future<StayReservationDto> createReservation(StayReservationRequestDto req) async {
    final response = await _dio.post('/stay/reservations', data: req.toJson());
    return StayReservationDto.fromJson(response.data);
  }

  Future<void> cancelReservation(int id) async {
    await _dio.patch('/stay/reservations/$id/cancel');
  }
}
