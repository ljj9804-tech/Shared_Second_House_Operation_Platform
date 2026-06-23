/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : lib/domain/view/main_screen.dart
 * 역할  : 앱 메인 화면 — 홈 탭 / 내 예약 탭 / AI 챗봇 탭을 IndexedStack으로 관리
 * 사용처 : app_router.dart → '/' 루트
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - stay_accommodation_controller.dart  : 숙소 목록 + 구독 목록 (Provider)
 * - stay_accommodation_detail_screen.dart : 숙소 카드 탭 시 이동
 * - stay_reservation_calendar_screen.dart : 구독 카드 "예약하기" 탭 시 이동
 * - stay_my_reservation_screen.dart       : BottomNavigationBar [내 예약] 탭
 * - guest_chat_bot_screen.dart            : BottomNavigationBar [AI 챗봇] 탭
 * ----------------------------------------------------------------------------------
 * [홈 탭 구성]
 * ① 내 구독 숙소 섹션
 *    - ACTIVE 구독만 표시 (PENDING·EXPIRED·CANCELLED 숨김)
 *    - ACTIVE 없으면 섹션 전체 숨김
 *    - PENDING 건수는 "⏳ 승인 대기 중 N건" 텍스트로만 안내
 *    - 1개 → 100% 너비 카드 / 복수 → 가로 슬라이드
 *    - 예약하기 버튼에 subscriptionStartDate, subscriptionEndDate 전달 (달력 범위 제한용)
 * ② 우리가 머물 집 섹션 — 전체 숙소 가로 슬라이드 (최대 5개)
 * ----------------------------------------------------------------------------------
 * [주의사항]
 * ⚠️ [TODO] 로그인 연동 후 AppConfig.tempUserId → 실제 userId로 교체
 * ⚠️ DevScreenLinks 위젯은 개발 완료 후 삭제 예정
 * ==================================================================================
 */

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:flutter_front/common/constants/app_colors.dart';
import 'package:flutter_front/config/app_config.dart';
import 'package:flutter_front/domain/controller/stay_accommodation_controller.dart';
import 'package:flutter_front/domain/dto/stay_accommodation_dto.dart';
import 'package:flutter_front/domain/view/stay_accommodation_detail_screen.dart';
import 'package:flutter_front/domain/view/stay_accommodation_list_screen.dart';
import 'package:flutter_front/domain/view/stay_reservation_calendar_screen.dart';
import 'package:flutter_front/domain/view/stay_my_reservation_screen.dart';
import 'package:flutter_front/domain/view/guest_chat_bot_screen.dart';
// TODO: [개발 완료 시 삭제]
import 'package:flutter_front/domain/view/dev_screen_links.dart';

class MainScreen extends StatefulWidget {
  const MainScreen({super.key});

  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  int _currentIndex = 0;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<StayAccommodationController>().loadAccommodationsAndFaqs(userId: AppConfig.tempUserId);
    });
  }

  @override
  Widget build(BuildContext context) {
    final ctrl = context.watch<StayAccommodationController>();

    return Scaffold(
      backgroundColor: AppColors.background,
      body: IndexedStack(
        index: _currentIndex,
        children: [
          _buildHomeScaffold(ctrl),
          const StayMyReservationScreen(),
          const GuestChatBotScreen(),
        ],
      ),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _currentIndex,
        onTap: (index) => setState(() => _currentIndex = index),
        selectedItemColor: AppColors.primary,
        unselectedItemColor: AppColors.textHint,
        backgroundColor: Colors.white,
        elevation: 8,
        type: BottomNavigationBarType.fixed,
        items: const [
          BottomNavigationBarItem(
            icon: Icon(Icons.home_outlined),
            activeIcon: Icon(Icons.home),
            label: '홈',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.calendar_month_outlined),
            activeIcon: Icon(Icons.calendar_month),
            label: '내 예약',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.smart_toy_outlined),
            activeIcon: Icon(Icons.smart_toy),
            label: 'AI 챗봇',
          ),
        ],
      ),
    );
  }

  Widget _buildHomeScaffold(StayAccommodationController ctrl) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: const Text(
          '세컨하우스',
          style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 18),
        ),
        centerTitle: true,
        backgroundColor: AppColors.primary,
        elevation: 0,
        iconTheme: const IconThemeData(color: Colors.white),
        // actions: [
        //   IconButton(
        //     icon: const Icon(Icons.notifications_none, color: Colors.white),
        //     onPressed: () {},
        //   ),
        // ],
      ),
      body: ctrl.isLoadingList
          ? const Center(child: CircularProgressIndicator(color: AppColors.primary))
          : ctrl.errorMessage != null
              ? Center(child: Text(ctrl.errorMessage!, style: const TextStyle(color: AppColors.danger)))
              : _buildBody(ctrl),
    );
  }

  Widget _buildBody(StayAccommodationController ctrl) {
    final accommodations = ctrl.accommodations.take(5).toList();
    final subscribedAccommodations = ctrl.mySubscribedAccommodations;
    final pendingCount = ctrl.mySubscriptions.where((s) => s.status == 'PENDING').length;

    return SingleChildScrollView(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const SizedBox(height: 20),

          // 내 구독 숙소 섹션 (ACTIVE만 표시)
          if (subscribedAccommodations.isNotEmpty) ...[
            const Padding(
              padding: EdgeInsets.symmetric(horizontal: 16),
              child: Text('내 구독 숙소', style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: AppColors.textPrimary)),
            ),
            // const SizedBox(height: 12),
            if (pendingCount > 0)
              Padding(
                padding: const EdgeInsets.fromLTRB(16, 8, 16, 0),
                child: Row(
                  children: [
                    const Spacer(),
                    const Text('⏳', style: TextStyle(fontSize: 13)),
                    const SizedBox(width: 6),
                    Text(
                      '승인 대기 중 $pendingCount건',
                      style: const TextStyle(fontSize: 13, color: AppColors.textSecondary),
                    ),
                  ],
                ),
              ),
            const SizedBox(height: 12),
            if (subscribedAccommodations.length == 1)
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 16),
                child: _buildSubscribedCard(ctrl, subscribedAccommodations.first, fullWidth: true),
              )
            else
              SizedBox(
                height: 260,
                child: ListView.builder(
                  scrollDirection: Axis.horizontal,
                  padding: const EdgeInsets.symmetric(horizontal: 16),
                  itemCount: subscribedAccommodations.length,
                  itemBuilder: (_, index) => _buildSubscribedCard(ctrl, subscribedAccommodations[index]),
                ),
              ),

            const SizedBox(height: 28),
          ],

          // 숙소 목록 섹션
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                const Text('우리가 머물 집', style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: AppColors.textPrimary)),
                TextButton(
                  onPressed: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const StayAccommodationListScreen())),
                  style: TextButton.styleFrom(
                    foregroundColor: AppColors.primary,
                    padding: EdgeInsets.zero,
                    minimumSize: Size.zero,
                    tapTargetSize: MaterialTapTargetSize.shrinkWrap,
                  ),
                  child: const Row(
                    children: [
                      Text('전체보기', style: TextStyle(fontSize: 13, fontWeight: FontWeight.w500)),
                      Icon(Icons.chevron_right, size: 18),
                    ],
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),
          SizedBox(
            height: 240,
            child: ListView.builder(
              scrollDirection: Axis.horizontal,
              padding: const EdgeInsets.symmetric(horizontal: 16),
              itemCount: accommodations.length,
              itemBuilder: (_, index) => _buildAccommodationCard(accommodations[index]),
            ),
          ),
          const SizedBox(height: 32),

          // TODO: [개발 완료 시 삭제] 팀원 화면 링크
          const Padding(
            padding: EdgeInsets.symmetric(horizontal: 16),
            child: Text('개발용 화면 링크', style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: AppColors.textPrimary)),
          ),
          const SizedBox(height: 12),
          const DevScreenLinks(),
        ],
      ),
    );
  }

  Widget _buildSubscribedCard(StayAccommodationController ctrl, StayAccommodationDto item, {bool fullWidth = false}) {
    final activeSub = ctrl.activeSubscriptionFor(item.id);
    final subStart = activeSub != null ? _parseDate(activeSub.startDate) : null;
    final subEnd = activeSub != null ? _parseDate(activeSub.endDate) : null;

    return GestureDetector(
      onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => StayAccommodationDetailScreen(accommodationId: item.id))),
      child: Container(
        width: fullWidth ? double.infinity : 220,
        margin: fullWidth ? EdgeInsets.zero : const EdgeInsets.only(right: 12),
        decoration: BoxDecoration(
          color: AppColors.white,
          borderRadius: BorderRadius.circular(16),
          boxShadow: [BoxShadow(color: Colors.black.withValues(alpha: 0.07), blurRadius: 8, offset: const Offset(0, 2))],
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            ClipRRect(
              borderRadius: const BorderRadius.vertical(top: Radius.circular(16)),
              child: item.firstImageUrl != null
                  ? Image.network(item.firstImageUrl!, height: fullWidth ? 180 : 130, width: double.infinity, fit: BoxFit.cover,
                      errorBuilder: (context, error, stack) => _imagePlaceholder(height: fullWidth ? 180 : 130))
                  : _imagePlaceholder(height: fullWidth ? 180 : 130),
            ),
            Padding(
              padding: const EdgeInsets.all(12),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(item.name, style: const TextStyle(fontSize: 14, fontWeight: FontWeight.bold, color: AppColors.textPrimary), maxLines: 1, overflow: TextOverflow.ellipsis),
                  const SizedBox(height: 4),
                  Row(
                    children: [
                      const Icon(Icons.location_on_outlined, size: 12, color: Colors.black45),
                      const SizedBox(width: 2),
                      Expanded(child: Text(item.address, style: const TextStyle(fontSize: 11, color: AppColors.textSecondary), maxLines: 1, overflow: TextOverflow.ellipsis)),
                    ],
                  ),
                  const SizedBox(height: 10),
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton(
                      onPressed: () => Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (_) => StayReservationCalendarScreen(
                            accommodationId: item.id,
                            accommodationName: item.name,
                            subscriptionStartDate: subStart,
                            subscriptionEndDate: subEnd,
                          ),
                        ),
                      ),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: AppColors.primary,
                        foregroundColor: Colors.white,
                        padding: const EdgeInsets.symmetric(vertical: 8),
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                        textStyle: const TextStyle(fontSize: 13, fontWeight: FontWeight.bold),
                      ),
                      child: const Text('예약하기'),
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  DateTime? _parseDate(String dateStr) {
    if (dateStr.isEmpty) return null;
    try {
      return DateTime.parse(dateStr);
    } catch (_) {
      return null;
    }
  }

  Widget _buildAccommodationCard(StayAccommodationDto item) {
    final tags = (item.amenities ?? '').split(',').where((t) => t.trim().isNotEmpty).take(2).toList();

    return GestureDetector(
      onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => StayAccommodationDetailScreen(accommodationId: item.id))),
      child: Container(
        width: 200,
        margin: const EdgeInsets.only(right: 12),
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
              child: Stack(
                children: [
                  item.firstImageUrl != null
                      ? Image.network(item.firstImageUrl!, height: 130, width: double.infinity, fit: BoxFit.cover,
                          errorBuilder: (context, error, stackTrace) => _imagePlaceholder())
                      : _imagePlaceholder(),
                  Positioned(
                    left: 10, bottom: 8,
                    child: Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                      decoration: BoxDecoration(
                        color: Colors.black.withValues(alpha: 0.5),
                        borderRadius: BorderRadius.circular(20),
                      ),
                      child: Text(
                        item.address.split(' ').take(2).join(' '),
                        style: const TextStyle(color: Colors.white, fontSize: 11, fontWeight: FontWeight.w600),
                      ),
                    ),
                  ),
                ],
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(12),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(item.name, style: const TextStyle(fontSize: 14, fontWeight: FontWeight.bold, color: AppColors.textPrimary), maxLines: 1, overflow: TextOverflow.ellipsis),
                  const SizedBox(height: 4),
                  Text(item.address, style: const TextStyle(fontSize: 11, color: AppColors.textSecondary), maxLines: 1, overflow: TextOverflow.ellipsis),
                  if (tags.isNotEmpty) ...[
                    const SizedBox(height: 8),
                    Wrap(
                      spacing: 4,
                      children: tags.map((tag) => Text('#${tag.trim()}', style: const TextStyle(fontSize: 11, color: AppColors.primary, fontWeight: FontWeight.w500))).toList(),
                    ),
                  ],
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _imagePlaceholder({double height = 130}) => Container(
    height: height, color: AppColors.border,
    child: const Center(child: Icon(Icons.home_outlined, size: 40, color: AppColors.textHint)),
  );
}
