/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : lib/domain/view/stay_my_subscription_screen.dart
 * 역할  : 내 구독 목록 화면 (구독 카드 + 채팅/예약하기 버튼)
 * 사용처 : app_router.dart → '/my/subscriptions' 라우트
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - stay_subscription_controller.dart      : 구독 목록 상태 (Provider)
 * - stay_subscription_dto.dart             : 구독 모델
 * - stay_reservation_calendar_screen.dart  : 예약하기 버튼 이동
 * - Spring: SubscriptionsController.java   : GET /subscriptions/my/{userId}
 * ----------------------------------------------------------------------------------
 * [기능 목록]
 * - 내 구독 목록 조회 및 표시
 * - 구독 상태별 배지 (ACTIVE / PENDING / EXPIRED / CANCELLED)
 * - ACTIVE 구독만 채팅 + 예약하기 버튼 표시
 * ==================================================================================
 */

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:flutter_front/common/constants/app_colors.dart';
import 'package:flutter_front/common/widget/app_base_layout.dart';
import 'package:flutter_front/features/auth/provider/auth_provider.dart';
import 'package:flutter_front/domain/controller/stay_subscription_controller.dart';
import 'package:flutter_front/domain/dto/stay_subscription_dto.dart';
import 'package:flutter_front/domain/view/stay_reservation_calendar_screen.dart';

class StayMySubscriptionScreen extends StatefulWidget {
  const StayMySubscriptionScreen({super.key});

  @override
  State<StayMySubscriptionScreen> createState() => _StayMySubscriptionScreenState();
}

class _StayMySubscriptionScreenState extends State<StayMySubscriptionScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<StaySubscriptionController>().loadMySubscriptions(
        context.read<AuthProvider>().userId!,
      );
    });
  }

  @override
  Widget build(BuildContext context) {
    final ctrl = context.watch<StaySubscriptionController>();

    return AppBaseLayout(
      title: '내 구독 목록',
      body: ctrl.isLoading
          ? const Center(child: CircularProgressIndicator(color: AppColors.primary))
          : ctrl.subscriptions.isEmpty
              ? _buildEmpty()
              : ListView.builder(
                  padding: const EdgeInsets.all(16),
                  itemCount: ctrl.subscriptions.length,
                  itemBuilder: (_, i) => _buildCard(ctrl, ctrl.subscriptions[i]),
                ),
    );
  }

  Widget _buildEmpty() => const Center(
    child: Column(mainAxisSize: MainAxisSize.min, children: [
      Icon(Icons.home_outlined, size: 56, color: AppColors.textHint),
      SizedBox(height: 16),
      Text('구독 내역이 없습니다.', style: TextStyle(fontSize: 15, color: AppColors.textSecondary)),
    ]),
  );

  Widget _buildCard(StaySubscriptionController ctrl, StaySubscriptionDto sub) {
    final accom = ctrl.accommodationCache[sub.accommodationId];

    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      decoration: BoxDecoration(
        color: AppColors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [BoxShadow(color: Colors.black.withValues(alpha: 0.12), blurRadius: 10, offset: const Offset(0, 3))],
      ),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Expanded(
                  child: Text(
                    accom?.name ?? '숙소 정보 로딩 중...',
                    style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: AppColors.textPrimary),
                  ),
                ),
                _statusBadge(sub.status),
              ],
            ),
            if (accom != null) ...[
              const SizedBox(height: 6),
              Row(children: [
                const Icon(Icons.location_on_outlined, size: 14, color: AppColors.textHint),
                const SizedBox(width: 4),
                Expanded(child: Text(accom.address, style: const TextStyle(fontSize: 12, color: AppColors.textSecondary), maxLines: 1, overflow: TextOverflow.ellipsis)),
              ]),
            ],
            const SizedBox(height: 8),
            Row(children: [
              const Icon(Icons.calendar_month_outlined, size: 14, color: AppColors.primary),
              const SizedBox(width: 4),
              Expanded(child: Text('${sub.startDate} ~ ${sub.endDate}', style: const TextStyle(fontSize: 13, color: AppColors.textSecondary), maxLines: 1, overflow: TextOverflow.ellipsis)),
            ]),
            const SizedBox(height: 4),
            Row(children: [
              const Icon(Icons.schedule_outlined, size: 14, color: AppColors.textHint),
              const SizedBox(width: 4),
              Expanded(child: Text('구독 기간: ${sub.durationMonths}개월', style: const TextStyle(fontSize: 13, color: AppColors.textSecondary))),
            ]),
            if (sub.isActive) ...[
              const SizedBox(height: 12),
              Row(
                children: [
                  Expanded(
                    child: OutlinedButton(
                      onPressed: () {
                        // TODO: 채팅 팀원 라우트 연동 시 Navigator.push 로 교체
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(content: Text('채팅 기능은 준비 중입니다.')),
                        );
                      },
                      style: OutlinedButton.styleFrom(
                        foregroundColor: AppColors.primary,
                        side: const BorderSide(color: AppColors.primary),
                        padding: const EdgeInsets.symmetric(vertical: 10),
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                      ),
                      child: const Text('채팅', style: TextStyle(fontWeight: FontWeight.bold)),
                    ),
                  ),
                  const SizedBox(width: 8),
                  Expanded(
                    child: ElevatedButton(
                      onPressed: () => Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (_) => StayReservationCalendarScreen(
                            accommodationId: sub.accommodationId,
                            accommodationName: accom?.name ?? '',
                            subscriptionStartDate: DateTime.tryParse(sub.startDate),
                            subscriptionEndDate: DateTime.tryParse(sub.endDate),
                          ),
                        ),
                      ),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: AppColors.primary,
                        foregroundColor: Colors.white,
                        padding: const EdgeInsets.symmetric(vertical: 10),
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                      ),
                      child: const Text('예약하기', style: TextStyle(fontWeight: FontWeight.bold)),
                    ),
                  ),
                ],
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _statusBadge(String status) {
    switch (status) {
      case 'ACTIVE':
        return Container(
          padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
          decoration: const BoxDecoration(color: Color(0xFFE8F5E9), borderRadius: BorderRadius.all(Radius.circular(20))),
          child: const Text('구독 중', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: AppColors.success)),
        );
      case 'PENDING':
        return Container(
          padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
          decoration: const BoxDecoration(color: Color(0xFFFFF8E6), borderRadius: BorderRadius.all(Radius.circular(20))),
          child: const Text('승인 대기', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Color(0xFFB07D1A))),
        );
      default:
        return Container(
          padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
          decoration: const BoxDecoration(color: AppColors.border, borderRadius: BorderRadius.all(Radius.circular(20))),
          child: Text(
            status == 'EXPIRED' ? '만료됨' : '취소됨',
            style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: AppColors.disabledText),
          ),
        );
    }
  }
}
