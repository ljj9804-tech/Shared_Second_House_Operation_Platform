/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : lib/domain/view/stay_subscription_apply_screen.dart
 * 역할  : 구독 신청 화면 (대표자 + 팀원 입력 → 구독 신청 요청)
 * 사용처 : StayAccommodationDetailScreen 에서 "구독 신청하기" 버튼 탭 시 push
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - stay_subscription_service.dart      : applySubscription() 호출
 * - stay_accommodation_dto.dart         : 숙소 정보 props
 * - stay_constants.dart                 : kMonthOptionValues, monthOptionLabel
 * - Spring: SubscriptionsController.java : POST /waiting/apply/{leaderId}
 * ----------------------------------------------------------------------------------
 * [기능 목록]
 * - 숙소 정보 카드 표시 (이름, 주소, 월세)
 * - 대표자 자동 설정 (로그인한 사용자 userId)
 * - 팀원 추가 / 삭제 (TextEditingController 동적 관리)
 * - 계약 개월수 드롭다운 선택
 * - 팀당 월세 실시간 계산 (_calcTeamPrice)
 * - 구독 신청 → 완료 시 이전 화면으로 pop
 * ----------------------------------------------------------------------------------
 * [파일 흐름과 순서]
 * 진입(accommodation props) → 대표자 자동 설정
 * → 팀원 추가 → TextEditingController 생성 → 팀 인원 증가 → 가격 재계산
 * → "구독 신청하기" → _handleSubmit() → POST /waiting/apply/{leaderId}
 * → 성공: SnackBar + pop / 실패: 에러 SnackBar
 * ==================================================================================
 */

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:flutter_front/common/constants/app_colors.dart';
import 'package:flutter_front/common/constants/stay_constants.dart';
import 'package:flutter_front/common/widget/app_base_layout.dart';
import 'package:flutter_front/common/widget/common_button.dart';
import 'package:flutter_front/features/auth/provider/auth_provider.dart';
import 'package:flutter_front/domain/dto/stay_accommodation_dto.dart';
import 'package:flutter_front/domain/service/stay_subscription_service.dart';

class StaySubscriptionApplyScreen extends StatefulWidget {
  final StayAccommodationDto accommodation;

  const StaySubscriptionApplyScreen({super.key, required this.accommodation});

  @override
  State<StaySubscriptionApplyScreen> createState() => _StaySubscriptionApplyScreenState();
}

class _StaySubscriptionApplyScreenState extends State<StaySubscriptionApplyScreen> {
  final SubscriptionService _service = SubscriptionService();

  int get _leaderId => context.read<AuthProvider>().userId!;

  final List<TextEditingController> _memberControllers = [];
  int _durationMonths = 1;
  bool _isLoading = false;

  @override
  void dispose() {
    for (final c in _memberControllers) {
      c.dispose();
    }
    super.dispose();
  }

  void _addMember() {
    final ctrl = TextEditingController();
    ctrl.addListener(() => setState(() {}));
    setState(() => _memberControllers.add(ctrl));
  }

  void _removeMember(int index) {
    setState(() {
      _memberControllers[index].dispose();
      _memberControllers.removeAt(index);
    });
  }

  // Next.js calcTeamPrice와 동일: discountRate 적용 후 팀 인원으로 나눔
  int _calcTeamPrice(StayAccommodationDto item, int months, int teams) {
    StayAccommodationPriceDto? priceInfo;
    for (final p in item.prices) {
      final maxMonths = p.maxMonths;
      if (months >= p.minMonths && (maxMonths == null || months < maxMonths)) {
        priceInfo = p;
        break;
      }
    }
    if (priceInfo == null) return (item.monthlyPrice / teams).floor();
    return (item.monthlyPrice * (1 - priceInfo.discountRate) / teams).floor();
  }

  Future<void> _handleSubmit() async {
    final memberIds = _memberControllers
        .map((c) => c.text.trim())
        .where((id) => id.isNotEmpty)
        .toList();

    setState(() => _isLoading = true);

    try {
      await _service.applySubscription(
        leaderId: _leaderId,
        accommodationId: widget.accommodation.id,
        durationMonths: _durationMonths,
        memberIdentifiers: memberIds,
      );
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('구독 신청이 완료됐어요! 관리자 승인을 기다려주세요.'),
            backgroundColor: AppColors.success,
          ),
        );
        Navigator.pop(context);
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('구독 신청에 실패했어요. 다시 시도해주세요.'),
            backgroundColor: AppColors.danger,
          ),
        );
      }
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final item = widget.accommodation;

    return AppBaseLayout(
      title: '구독 신청',
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // 숙소 정보 카드
            _buildAccommodationInfo(item),
            const SizedBox(height: 24),

            // 대표자 ID
            _buildSectionTitle('대표자 ID'),
            const SizedBox(height: 8),
            _buildReadonlyField('$_leaderId', hint: '※ 로그인한 유저가 대표자로 자동 설정됩니다.'),
            const SizedBox(height: 20),

            // 팀원 아이디 또는 이메일 목록
            _buildSectionTitle('함께할 팀원 아이디 또는 이메일'),
            const SizedBox(height: 8),
            ..._memberControllers.asMap().entries.map((entry) {
              final index = entry.key;
              final controller = entry.value;
              return Padding(
                padding: const EdgeInsets.only(bottom: 10),
                child: Row(
                  children: [
                    Expanded(
                      child: TextField(
                        controller: controller,
                        decoration: InputDecoration(
                          hintText: '팀원 ${index + 1} 아이디 또는 이메일 입력',
                          hintStyle: const TextStyle(color: AppColors.textHint, fontSize: 14),
                          contentPadding: const EdgeInsets.symmetric(horizontal: 14, vertical: 13),
                          enabledBorder: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(8),
                            borderSide: const BorderSide(color: AppColors.border),
                          ),
                          focusedBorder: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(8),
                            borderSide: const BorderSide(color: AppColors.primary, width: 1.5),
                          ),
                        ),
                      ),
                    ),
                    const SizedBox(width: 8),
                    GestureDetector(
                      onTap: () => _removeMember(index),
                      child: Container(
                        padding: const EdgeInsets.all(10),
                        decoration: BoxDecoration(color: AppColors.dangerBg, borderRadius: BorderRadius.circular(8)),
                        child: const Icon(Icons.close, size: 18, color: AppColors.danger),
                      ),
                    ),
                  ],
                ),
              );
            }),
            CommonButton(
              label: '+ 팀원 추가',
              type: ButtonType.secondary,
              icon: null,
              onTap: _addMember,
            ),
            const SizedBox(height: 24),

            // 계약 개월수
            _buildSectionTitle('계약 개월수'),
            const SizedBox(height: 8),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 4),
              decoration: BoxDecoration(
                border: Border.all(color: AppColors.border),
                borderRadius: BorderRadius.circular(8),
              ),
              child: DropdownButton<int>(
                value: _durationMonths,
                isExpanded: true,
                underline: const SizedBox.shrink(),
                items: kMonthOptionValues
                    .map((n) => DropdownMenuItem(value: n, child: Text(monthOptionLabel(n))))
                    .toList(),
                onChanged: (v) => setState(() => _durationMonths = v!),
              ),
            ),
            const SizedBox(height: 12),

            // 구독 요약
            _buildPriceSummary(item),
            const SizedBox(height: 32),

            // 신청 버튼
            CommonButton(
              label: '구독 신청하기',
              type: ButtonType.primary,
              isLoading: _isLoading,
              onTap: _handleSubmit,
            ),
            const SizedBox(height: 20),
          ],
        ),
      ),
    );
  }

  Widget _buildAccommodationInfo(StayAccommodationDto item) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppColors.primaryBg,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: AppColors.primaryBorder),
      ),
      child: Row(
        children: [
          ClipRRect(
            borderRadius: BorderRadius.circular(8),
            child: item.firstImageUrl != null
                ? Image.network(item.firstImageUrl!, width: 64, height: 64, fit: BoxFit.cover,
                    errorBuilder: (context, err, _) => _imageFallback())
                : _imageFallback(),
          ),
          const SizedBox(width: 14),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(item.name, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: AppColors.textPrimary)),
                const SizedBox(height: 4),
                Text(item.address, style: const TextStyle(fontSize: 12, color: AppColors.textSecondary), maxLines: 1, overflow: TextOverflow.ellipsis),
                const SizedBox(height: 4),
                Text('월 ${_fmt(item.monthlyPrice)}원', style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w600, color: AppColors.primary)),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _imageFallback() => Container(
    width: 64, height: 64, color: AppColors.border,
    child: const Icon(Icons.home_outlined, color: AppColors.textHint),
  );

  Widget _buildSectionTitle(String title) => Text(
    title, style: const TextStyle(fontSize: 14, fontWeight: FontWeight.bold, color: AppColors.textPrimary),
  );

  Widget _buildReadonlyField(String value, {String? hint}) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          width: double.infinity,
          padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 13),
          decoration: BoxDecoration(
            color: AppColors.border,
            borderRadius: BorderRadius.circular(8),
          ),
          child: Text(value, style: const TextStyle(fontSize: 14, color: AppColors.textSecondary)),
        ),
        if (hint != null) ...[
          const SizedBox(height: 4),
          Text(hint, style: const TextStyle(fontSize: 11, color: AppColors.textHint)),
        ],
      ],
    );
  }

  Widget _buildPriceSummary(StayAccommodationDto item) {
    final filledCount = _memberControllers.where((c) => c.text.trim().isNotEmpty).length;
    final teamCount = filledCount + 1; // 실제 입력된 팀원 + 대표자
    final teamPrice = _calcTeamPrice(item, _durationMonths, teamCount);

    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppColors.primaryBg,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: AppColors.primaryBorder),
      ),
      child: Column(
        children: [
          _priceRow('원래 월세', '${_fmt(item.monthlyPrice)}원 / 월'),
          const SizedBox(height: 6),
          _priceRow('팀 인원', '$teamCount명 (대표자 포함, 입력 완료 기준)'),
          const SizedBox(height: 6),
          _priceRow('계약 기간', monthOptionLabel(_durationMonths)),
          Divider(height: 20, color: AppColors.primaryBorder),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text('팀당 월세', style: TextStyle(fontSize: 14, fontWeight: FontWeight.w600, color: AppColors.primary)),
              Text(
                teamPrice > 0 ? '${_fmt(teamPrice)}원 / 월' : '-',
                style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold, color: AppColors.primary),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _priceRow(String label, String value) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(label, style: const TextStyle(fontSize: 13, color: AppColors.textSecondary)),
        Text(value, style: const TextStyle(fontSize: 13, color: AppColors.textPrimary)),
      ],
    );
  }

  String _fmt(int price) => price.toString().replaceAllMapped(
    RegExp(r'(\d{1,3})(?=(\d{3})+(?!\d))'), (m) => '${m[1]},');
}
