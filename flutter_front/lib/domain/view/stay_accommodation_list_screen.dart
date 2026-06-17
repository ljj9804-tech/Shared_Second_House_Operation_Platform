import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:flutter_front/common/constants/app_colors.dart';
import 'package:flutter_front/common/widget/app_base_layout.dart';
import 'package:flutter_front/common/widget/bottom_sheet_selector.dart';
import 'package:flutter_front/domain/controller/stay_accommodation_controller.dart';
import 'package:flutter_front/domain/dto/stay_accommodation_dto.dart';
import 'package:flutter_front/domain/view/stay_accommodation_detail_screen.dart';
import 'package:flutter_front/util/price_calculator.dart';

class StayAccommodationListScreen extends StatefulWidget {
  final int? initialId;
  const StayAccommodationListScreen({super.key, this.initialId});

  @override
  State<StayAccommodationListScreen> createState() => _StayAccommodationListScreenState();
}

class _StayAccommodationListScreenState extends State<StayAccommodationListScreen> {
  int _months = 1;
  int _teams = 1;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      final ctrl = context.read<StayAccommodationController>();
      if (ctrl.accommodations.isEmpty) ctrl.loadAccommodationsAndFaqs();
    });
  }

  @override
  Widget build(BuildContext context) {
    final ctrl = context.watch<StayAccommodationController>();

    return AppBaseLayout(
      title: '숙소 목록',
      body: ctrl.isLoadingList
          ? const Center(child: CircularProgressIndicator(color: AppColors.primary))
          : Column(
              children: [
                _buildPriceCalculator(),
                Expanded(
                  child: ctrl.accommodations.isEmpty
                      ? const Center(child: Text('등록된 숙소가 없습니다.'))
                      : ListView.builder(
                          padding: const EdgeInsets.all(16),
                          itemCount: ctrl.accommodations.length,
                          itemBuilder: (_, index) => _buildAccommodationCard(ctrl.accommodations[index]),
                        ),
                ),
              ],
            ),
    );
  }

  Widget _buildPriceCalculator() {
    return Container(
      color: AppColors.white,
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('가격 계산기', style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold, color: AppColors.primary)),
          const SizedBox(height: 10),
          Row(
            children: [
              Expanded(
                child: BottomSheetSelector(
                  label: '개월수',
                  value: _months,
                  options: List.generate(12, (i) => i + 1),
                  displayText: (v) => '$v개월',
                  onSelected: (v) => setState(() => _months = v),
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: BottomSheetSelector(
                  label: '팀수',
                  value: _teams,
                  options: List.generate(12, (i) => i + 1),
                  displayText: (v) => '$v팀',
                  onSelected: (v) => setState(() => _teams = v),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildAccommodationCard(StayAccommodationDto item) {
    final teamPrice = PriceCalculator.calculateTeamPrice(monthlyPrice: item.monthlyPrice, months: _months, teams: _teams, prices: item.prices);
    final discountRate = PriceCalculator.getDiscountRate(months: _months, prices: item.prices);
    final isAvailable = item.status == 'AVAILABLE';

    return GestureDetector(
      onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => StayAccommodationDetailScreen(accommodationId: item.id))),
      child: Container(
        margin: const EdgeInsets.only(bottom: 16),
        decoration: BoxDecoration(
          color: AppColors.white,
          borderRadius: BorderRadius.circular(16),
          boxShadow: [BoxShadow(color: Colors.black.withValues(alpha: 0.06), blurRadius: 8, offset: const Offset(0, 2))],
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            ClipRRect(
              borderRadius: const BorderRadius.vertical(top: Radius.circular(16)),
              child: item.firstImageUrl != null
                  ? Image.network(item.firstImageUrl!, height: 180, width: double.infinity, fit: BoxFit.cover,
                      errorBuilder: (context, err, _) => _imagePlaceholder())
                  : _imagePlaceholder(),
            ),
            Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Expanded(child: Text(item.name, style: const TextStyle(fontSize: 17, fontWeight: FontWeight.bold))),
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                        decoration: BoxDecoration(
                          color: isAvailable ? AppColors.primaryLight : AppColors.border,
                          borderRadius: BorderRadius.circular(6),
                        ),
                        child: Text(
                          isAvailable ? '예약가능' : '점검중',
                          style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: isAvailable ? AppColors.success : AppColors.disabledText),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 4),
                  Text(item.address, style: const TextStyle(fontSize: 13, color: AppColors.textSecondary)),
                  const SizedBox(height: 12),
                  Row(
                    crossAxisAlignment: CrossAxisAlignment.end,
                    children: [
                      Text('월 ${_fmt(teamPrice)}원', style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: AppColors.primary)),
                      const SizedBox(width: 8),
                      const Text('/ 팀당', style: TextStyle(fontSize: 13, color: AppColors.textHint)),
                      const Spacer(),
                      if (discountRate > 0)
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                          decoration: BoxDecoration(color: AppColors.dangerBg, borderRadius: BorderRadius.circular(6)),
                          child: Text('${(discountRate * 100).toInt()}% 할인', style: const TextStyle(fontSize: 12, color: AppColors.danger, fontWeight: FontWeight.bold)),
                        ),
                    ],
                  ),
                  const SizedBox(height: 4),
                  Text('기준: $_months개월 · $_teams팀 기준가', style: const TextStyle(fontSize: 11, color: AppColors.textHint)),
                  const SizedBox(height: 12),
                  Wrap(
                    spacing: 8,
                    children: [
                      if (item.roomCount != null) _specChip('방 ${item.roomCount}개'),
                      if (item.bathroomCount != null) _specChip('화장실 ${item.bathroomCount}개'),
                      if (item.parkingCount != null) _specChip('주차 ${item.parkingCount}대'),
                    ],
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _imagePlaceholder() => Container(
    height: 180, color: AppColors.border,
    child: const Center(child: Icon(Icons.home_outlined, size: 48, color: AppColors.textHint)),
  );

  Widget _specChip(String text) => Container(
    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
    decoration: BoxDecoration(color: AppColors.primaryBg, borderRadius: BorderRadius.circular(20)),
    child: Text(text, style: const TextStyle(fontSize: 11, color: AppColors.primary)),
  );

  String _fmt(int price) => price.toString().replaceAllMapped(
    RegExp(r'(\d{1,3})(?=(\d{3})+(?!\d))'), (m) => '${m[1]},');
}
