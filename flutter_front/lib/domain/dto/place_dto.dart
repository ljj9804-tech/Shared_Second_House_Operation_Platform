/// 🍽️ 주변 맛집 1건 DTO
/// 백엔드 GET /api/places/restaurants 의 PlaceDTO 규격과 1:1 매칭
class PlaceDto {
  final String id;          // Places 고유 id (place id)
  final String name;        // 가게 이름
  final String address;     // 주소
  final double latitude;    // 위도
  final double longitude;   // 경도
  final double? rating;     // 평점 0~5 (없으면 null)
  final int? userRatingCount; // 평점 개수 (없으면 null)
  final String? priceLevel; // 가격대 enum 문자열 (없으면 null)
  final String googleMapsUri; // 구글지도 장소 페이지 URL
  final String? primaryTypeName; // 대표 업종명 (예: 한식당)
  final String? businessStatus;  // 영업 상태 (예: OPERATIONAL)

  PlaceDto({
    required this.id,
    required this.name,
    required this.address,
    required this.latitude,
    required this.longitude,
    this.rating,
    this.userRatingCount,
    this.priceLevel,
    this.googleMapsUri = '',
    this.primaryTypeName,
    this.businessStatus,
  });

  factory PlaceDto.fromJson(Map<String, dynamic> json) {
    return PlaceDto(
      id: json['id']?.toString() ?? '',
      name: json['name']?.toString() ?? '이름 없음',
      address: json['address']?.toString() ?? '',
      latitude: (json['latitude'] as num?)?.toDouble() ?? 0.0,
      longitude: (json['longitude'] as num?)?.toDouble() ?? 0.0,
      rating: (json['rating'] as num?)?.toDouble(),
      userRatingCount: (json['userRatingCount'] as num?)?.toInt(),
      priceLevel: json['priceLevel']?.toString(),
      googleMapsUri: json['googleMapsUri']?.toString() ?? '',
      primaryTypeName: json['primaryTypeName']?.toString(),
      businessStatus: json['businessStatus']?.toString(),
    );
  }
}