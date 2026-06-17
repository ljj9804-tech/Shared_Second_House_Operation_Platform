class StayReservationRequestDto {
  final int accommodationId;
  final int userId;
  final String startDate; // yyyy-MM-dd
  final String endDate;   // yyyy-MM-dd

  StayReservationRequestDto({
    required this.accommodationId,
    required this.userId,
    required this.startDate,
    required this.endDate,
  });

  Map<String, dynamic> toJson() => {
    'accommodationId': accommodationId,
    'userId': userId,
    'startDate': startDate,
    'endDate': endDate,
  };
}

class StayReservationDto {
  final int id;
  final int accommodationId;
  final String accommodationName;
  final String accommodationAddress;
  final String startDate;
  final String endDate;
  final String status; // CONFIRMED / CANCELLED

  StayReservationDto({
    required this.id,
    required this.accommodationId,
    required this.accommodationName,
    required this.accommodationAddress,
    required this.startDate,
    required this.endDate,
    required this.status,
  });

  factory StayReservationDto.fromJson(Map<String, dynamic> json) {
    return StayReservationDto(
      id: json['id'],
      accommodationId: json['accommodationId'],
      accommodationName: json['accommodationName'] ?? '',
      accommodationAddress: json['accommodationAddress'] ?? '',
      startDate: json['startDate'] ?? '',
      endDate: json['endDate'] ?? '',
      status: json['status'] ?? 'CONFIRMED',
    );
  }

  bool get isCancelled => status == 'CANCELLED';
}
