import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:flutter_front/common/constants/app_colors.dart';
import 'package:flutter_front/common/widget/app_base_layout.dart';
import 'package:flutter_front/common/widget/common_button.dart';
import 'package:flutter_front/domain/controller/stay_reservation_controller.dart';
import 'package:flutter_front/domain/dto/stay_reservation_dto.dart';

class StayMyReservationScreen extends StatefulWidget {
  const StayMyReservationScreen({super.key});

  @override
  State<StayMyReservationScreen> createState() => _StayMyReservationScreenState();
}

class _StayMyReservationScreenState extends State<StayMyReservationScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<StayReservationController>().loadMyReservations();
    });
  }

  @override
  Widget build(BuildContext context) {
    final ctrl = context.watch<StayReservationController>();

    return AppBaseLayout(
      title: '내 예약 목록',
      body: ctrl.isLoading
          ? const Center(child: CircularProgressIndicator(color: AppColors.primary))
          : ctrl.reservations.isEmpty
              ? _buildEmpty()
              : ListView.builder(
                  padding: const EdgeInsets.all(16),
                  itemCount: ctrl.reservations.length,
                  itemBuilder: (_, index) => _buildCard(ctrl, ctrl.reservations[index]),
                ),
    );
  }

  Widget _buildEmpty() => const Center(
    child: Column(mainAxisSize: MainAxisSize.min, children: [
      Icon(Icons.calendar_today_outlined, size: 56, color: AppColors.textHint),
      SizedBox(height: 16),
      Text('예약 내역이 없습니다.', style: TextStyle(fontSize: 15, color: AppColors.textSecondary)),
    ]),
  );

  Widget _buildCard(StayReservationController ctrl, StayReservationDto item) {
    final isCancelled = item.isCancelled;
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      decoration: BoxDecoration(
        color: AppColors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [BoxShadow(color: Colors.black.withValues(alpha: 0.05), blurRadius: 6, offset: const Offset(0, 2))],
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
                    item.accommodationName,
                    style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: isCancelled ? AppColors.textHint : AppColors.textPrimary),
                  ),
                ),
                _statusBadge(item.status),
              ],
            ),
            const SizedBox(height: 6),
            Row(children: [
              const Icon(Icons.location_on_outlined, size: 14, color: AppColors.textHint),
              const SizedBox(width: 4),
              Expanded(child: Text(item.accommodationAddress, style: const TextStyle(fontSize: 12, color: AppColors.textSecondary), maxLines: 1, overflow: TextOverflow.ellipsis)),
            ]),
            const SizedBox(height: 12),
            Row(children: [
              const Icon(Icons.calendar_month_outlined, size: 15, color: AppColors.primary),
              const SizedBox(width: 6),
              Text(
                '${item.startDate} ~ ${item.endDate}',
                style: TextStyle(fontSize: 14, fontWeight: FontWeight.w600, color: isCancelled ? AppColors.textHint : AppColors.primary),
              ),
            ]),
            if (!isCancelled) ...[
              const SizedBox(height: 12),
              CommonButton(
                label: '예약 취소',
                type: ButtonType.danger,
                onTap: () => _confirmCancel(ctrl, item.id),
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _statusBadge(String status) {
    final isCancelled = status == 'CANCELLED';
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      decoration: BoxDecoration(
        color: isCancelled ? AppColors.border : const Color(0xFFE8F5E9),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Text(
        isCancelled ? '취소됨' : '예약확정',
        style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: isCancelled ? AppColors.disabledText : AppColors.success),
      ),
    );
  }

  Future<void> _confirmCancel(StayReservationController ctrl, int id) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('예약 취소', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
        content: const Text('예약을 취소하시겠습니까?'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx, false), child: const Text('아니요')),
          TextButton(onPressed: () => Navigator.pop(ctx, true), child: const Text('취소하기', style: TextStyle(color: AppColors.danger))),
        ],
      ),
    );
    if (confirmed == true && mounted) {
      final success = await ctrl.cancelReservation(id);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text(success ? '예약이 취소되었습니다.' : ctrl.errorMessage ?? '취소에 실패했습니다.'),
          backgroundColor: success ? AppColors.success : AppColors.danger,
        ));
      }
    }
  }
}
