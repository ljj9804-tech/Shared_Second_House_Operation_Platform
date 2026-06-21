class StaySubscriptionDto {
  final int subscriptionId;
  final int userId;
  final int accommodationId;
  final int durationMonths;
  final String startDate;
  final String endDate;
  final String status; // PENDING, ACTIVE, EXPIRED, CANCELLED

  StaySubscriptionDto({
    required this.subscriptionId,
    required this.userId,
    required this.accommodationId,
    required this.durationMonths,
    required this.startDate,
    required this.endDate,
    required this.status,
  });

  factory StaySubscriptionDto.fromJson(Map<String, dynamic> json) {
    return StaySubscriptionDto(
      subscriptionId: json['subscriptionId'],
      userId: json['userId'],
      accommodationId: json['accommodationId'],
      durationMonths: json['durationMonths'] ?? 0,
      startDate: json['startDate'] ?? '',
      endDate: json['endDate'] ?? '',
      status: json['status'] ?? '',
    );
  }

  bool get isActive => status == 'ACTIVE';
}
