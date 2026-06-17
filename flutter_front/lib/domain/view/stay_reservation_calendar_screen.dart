import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:table_calendar/table_calendar.dart';
import 'package:flutter_front/common/constants/app_colors.dart';
import 'package:flutter_front/common/widget/app_base_layout.dart';
import 'package:flutter_front/domain/controller/stay_reservation_controller.dart';

class StayReservationCalendarScreen extends StatefulWidget {
  final int accommodationId;
  final String accommodationName;

  const StayReservationCalendarScreen({
    super.key,
    required this.accommodationId,
    required this.accommodationName,
  });

  @override
  State<StayReservationCalendarScreen> createState() => _StayReservationCalendarScreenState();
}

class _StayReservationCalendarScreenState extends State<StayReservationCalendarScreen> {


  DateTime _focusedDay = DateTime.now();
  DateTime? _rangeStart;
  DateTime? _rangeEnd;

  @override
  Widget build(BuildContext context) {
    final ctrl = context.watch<StayReservationController>();

    return AppBaseLayout(
      title: widget.accommodationName,
      actions: _rangeStart != null
          ? [
              IconButton(
                icon: const Icon(Icons.refresh, color: Colors.white),
                tooltip: '초기화',
                onPressed: () => setState(() {
                  _rangeStart = null;
                  _rangeEnd = null;
                }),
              ),
            ]
          : null,
      body: Column(
        children: [
          const Padding(
            padding: EdgeInsets.fromLTRB(16, 16, 16, 4),
            child: Text('예약 날짜를 선택해주세요', style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold)),
          ),
          // 달력
          Card(
            margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            elevation: 2,
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
            child: Padding(
              padding: const EdgeInsets.all(8),
              child: TableCalendar(
                firstDay: DateTime.now(),
                lastDay: DateTime.now().add(const Duration(days: 365)),
                focusedDay: _focusedDay,
                locale: 'ko_KR',
                rangeStartDay: _rangeStart,
                rangeEndDay: _rangeEnd,
                rangeSelectionMode: RangeSelectionMode.toggledOn,
                headerStyle: const HeaderStyle(
                  formatButtonVisible: false,
                  titleCentered: true,
                  titleTextStyle: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: AppColors.primary),
                ),
                calendarStyle: CalendarStyle(
                  rangeHighlightColor: AppColors.primary.withValues(alpha: 0.12),
                  rangeStartDecoration: const BoxDecoration(color: AppColors.primary, shape: BoxShape.circle),
                  rangeEndDecoration: const BoxDecoration(color: AppColors.primary, shape: BoxShape.circle),
                  todayDecoration: BoxDecoration(
                    color: AppColors.primary.withValues(alpha: 0.3),
                    shape: BoxShape.circle,
                  ),
                  selectedDecoration: const BoxDecoration(color: AppColors.primary, shape: BoxShape.circle),
                  outsideDaysVisible: false,
                ),
                onRangeSelected: (start, end, focusedDay) {
                  setState(() {
                    _rangeStart = start;
                    _rangeEnd = end;
                    _focusedDay = focusedDay;
                  });
                },
                onPageChanged: (focusedDay) {
                  _focusedDay = focusedDay;
                },
              ),
            ),
          ),

          // 선택된 날짜 요약
          if (_rangeStart != null)
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              child: Container(
                width: double.infinity,
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(color: AppColors.primary.withValues(alpha: 0.2)),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text('선택 기간', style: TextStyle(fontSize: 12, color: Colors.black45, fontWeight: FontWeight.w600)),
                    const SizedBox(height: 6),
                    Row(
                      children: [
                        const Icon(Icons.calendar_today, size: 16, color: AppColors.primary),
                        const SizedBox(width: 8),
                        Text(
                          '${_fmtDate(_rangeStart!)} ~ ${_rangeEnd != null ? _fmtDate(_rangeEnd!) : '종료일 선택'}',
                          style: const TextStyle(fontSize: 15, fontWeight: FontWeight.bold, color: AppColors.primary),
                        ),
                      ],
                    ),
                    if (_rangeStart != null && _rangeEnd != null) ...[
                      const SizedBox(height: 4),
                      Text(
                        '총 ${_rangeEnd!.difference(_rangeStart!).inDays}박',
                        style: const TextStyle(fontSize: 13, color: Colors.black54),
                      ),
                    ],
                  ],
                ),
              ),
            ),

          const Spacer(),

          // 예약 버튼
          SafeArea(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: (_rangeStart != null && _rangeEnd != null && !ctrl.isLoading)
                      ? () => _handleReservation(ctrl)
                      : null,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: AppColors.primary,
                    foregroundColor: Colors.white,
                    padding: const EdgeInsets.symmetric(vertical: 16),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                    disabledBackgroundColor: Colors.grey.shade300,
                  ),
                  child: ctrl.isLoading
                      ? const SizedBox(height: 20, width: 20, child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2))
                      : const Text('예약 확정', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Future<void> _handleReservation(StayReservationController ctrl) async {
    ctrl.selectDateRange(_rangeStart!, _rangeEnd!);
    final success = await ctrl.createReservation(widget.accommodationId);

    if (!mounted) return;
    if (success) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('예약이 완료되었습니다!'), backgroundColor: Color(0xFF00A878)),
      );
      Navigator.pop(context);
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(ctrl.errorMessage ?? '예약에 실패했습니다.'), backgroundColor: Colors.red),
      );
    }
  }

  String _fmtDate(DateTime date) => '${date.year}.${date.month.toString().padLeft(2, '0')}.${date.day.toString().padLeft(2, '0')}';
}
