import 'package:flutter_front/domain/dto/stay_accommodation_dto.dart';

class PriceCalculator {
  // 공유문서 공식: (월세 × (1 - 할인율) / 팀수).floor()
  static int calculateTeamPrice({
    required int monthlyPrice,
    required int months,
    required int teams,
    required List<StayAccommodationPriceDto> prices,
  }) {
    if (prices.isEmpty || teams <= 0) return monthlyPrice;

    try {
      final priceInfo = prices.firstWhere(
        (p) => months >= p.minMonths && (p.maxMonths == null || months < p.maxMonths!),
      );
      return (monthlyPrice * (1 - priceInfo.discountRate) / teams).floor();
    } catch (_) {
      // 구간 미매칭 → 할인 없이 팀 분할만
      return (monthlyPrice / teams).floor();
    }
  }

  // 총 비용 = 팀당 월세 × 개월수
  static int calculateTotalPrice({
    required int monthlyPrice,
    required int months,
    required int teams,
    required List<StayAccommodationPriceDto> prices,
  }) {
    return calculateTeamPrice(
          monthlyPrice: monthlyPrice,
          months: months,
          teams: teams,
          prices: prices,
        ) *
        months;
  }

  // 해당 개월수의 할인율 반환 (없으면 0.0)
  static double getDiscountRate({
    required int months,
    required List<StayAccommodationPriceDto> prices,
  }) {
    try {
      final priceInfo = prices.firstWhere(
        (p) => months >= p.minMonths && (p.maxMonths == null || months < p.maxMonths!),
      );
      return priceInfo.discountRate;
    } catch (_) {
      return 0.0;
    }
  }
}
