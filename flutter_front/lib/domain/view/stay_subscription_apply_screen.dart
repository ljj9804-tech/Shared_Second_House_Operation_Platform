import 'package:flutter/material.dart';
import 'package:flutter_front/common/constants/app_colors.dart';
import 'package:flutter_front/common/widget/app_base_layout.dart';
import 'package:flutter_front/common/widget/common_button.dart';
import 'package:flutter_front/config/app_config.dart';
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

  // TODO [인증]: JWT 연동 후 실제 userId로 교체
  final int _leaderId = AppConfig.tempUserId;

  final List<TextEditingController> _memberControllers = [TextEditingController()];
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
    setState(() => _memberControllers.add(TextEditingController()));
  }

  void _removeMember(int index) {
    setState(() {
      _memberControllers[index].dispose();
      _memberControllers.removeAt(index);
    });
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
        _showDialog(success: true);
      }
    } catch (e) {
      if (mounted) {
        _showDialog(success: false);
      }
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  void _showDialog({required bool success}) {
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text(success ? '신청 완료' : '신청 실패'),
        content: Text(
          success
              ? '구독 신청이 완료됐어요!\n관리자 승인을 기다려주세요.'
              : '구독 신청에 실패했어요.\n다시 시도해주세요.',
        ),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.pop(ctx);
              if (success) Navigator.pop(context);
            },
            child: const Text('확인'),
          ),
        ],
      ),
    );
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

            // 팀원 ID 목록
            _buildSectionTitle('함께할 팀원 ID'),
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
                        keyboardType: TextInputType.number,
                        decoration: InputDecoration(
                          hintText: '팀원 ${index + 1} ID 입력',
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
                    if (_memberControllers.length > 1) ...[
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
                items: List.generate(12, (i) => i + 1)
                    .map((n) => DropdownMenuItem(value: n, child: Text('$n 개월')))
                    .toList(),
                onChanged: (v) => setState(() => _durationMonths = v!),
              ),
            ),
            const SizedBox(height: 12),

            // 예상 금액 안내
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
    final teamCount = _memberControllers.length + 1; // 팀원 + 대표자
    final monthlyPerTeam = (item.monthlyPrice / teamCount).floor();

    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppColors.primaryBg,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: AppColors.primaryBorder),
      ),
      child: Column(
        children: [
          _priceRow('팀 인원', '$teamCount명 (대표자 포함)'),
          const SizedBox(height: 6),
          _priceRow('계약 기간', '$_durationMonths개월'),
          Divider(height: 20, color: AppColors.primaryBorder),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text('팀당 월세', style: TextStyle(fontSize: 14, fontWeight: FontWeight.w600, color: AppColors.primary)),
              Text(
                '${_fmt(monthlyPerTeam)}원 / 월',
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
