class DeliveryOrderDto {
  final String status;

  DeliveryOrderDto({required this.status});

  // 백엔드가 읽을 수 있도록 자바 Object(Map) 형태로 변환해주는 함수
  Map<String, dynamic> toJson() {
    return {
      'status': status,
    };
  }
}