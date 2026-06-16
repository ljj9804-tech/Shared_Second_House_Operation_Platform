import 'package:flutter/material.dart';
import 'guest_chat_screen.dart'; // 💡 기존에 만든 채팅 화면과 연결

class GuestChatMainScreen extends StatefulWidget {
  const GuestChatMainScreen({super.key});

  @override
  State<GuestChatMainScreen> createState() => _GuestChatMainScreenState();
}

class _GuestChatMainScreenState extends State<GuestChatMainScreen> {
  // 🎨 기획서 테마 색상 정의 (네이비 & 민트)
  static const Color primaryNavy = Color(0xFF23399D);
  static const Color accentMint = Color(0xFF00E5A3);
  static const Color backgroundBg = Color(0xFFF8F9FA);

  // 테스트용 고정 임시 데이터 (나중에 로그인 정보/유저 정보와 연동)
  final int _currentUserId = 100;
  final String _currentUserName = '홍길동';
  final int _mockHouseId = 1; // 1번 숙소 상품방 가정

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: backgroundBg,
      appBar: AppBar(
        title: const Text(
          'Re: Born · Lax · Vital',
          style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 18),
        ),
        centerTitle: true,
        backgroundColor: primaryNavy,
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.notifications_none, color: Colors.white),
            onPressed: () {},
          )
        ],
      ),
      body: Column(
        children: [
          // 🏞️ [중앙 영역] 숙소 리스트 및 숙소 상세내용 (와이어프레임 반영)
          Expanded(
            flex: 6,
            child: Container(
              margin: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: Colors.grey[300], // 와이어프레임의 회색 영역 표현
                borderRadius: BorderRadius.circular(16),
                image: const DecorationImage(
                  image: NetworkImage('https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=600&auto=format&fit=crop'), // 프리미엄 숙소 예시 이미지
                  fit: BoxFit.cover,
                ),
              ),
              child: Stack(
                children: [
                  // ✨ 최신 플러터 문법 반영: withOpacity 대신 withValues 사용
                  Container(
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(16),
                      gradient: LinearGradient(
                        begin: Alignment.topCenter,
                        end: Alignment.bottomCenter,
                        colors: [Colors.transparent, Colors.black.withValues(alpha: 0.7)],
                      ),
                    ),
                  ),
                  // 숙소 상세 텍스트 정보 및 구독 버튼 (오른쪽 배치)
                  Positioned(
                    left: 20,
                    bottom: 20,
                    right: 20,
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      crossAxisAlignment: CrossAxisAlignment.end,
                      children: [
                        const Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Text(
                                '남해 프리미엄 오션뷰 세컨하우스',
                                style: TextStyle(color: Colors.white, fontSize: 20, fontWeight: FontWeight.bold),
                              ),
                              SizedBox(height: 6),
                              Text(
                                '버려진 공간의 재탄생, 온전한 워케이션을 즐기세요.',
                                style: TextStyle(color: Colors.white70, fontSize: 13),
                              ),
                            ],
                          ),
                        ),
                        // 💳 [구독 버튼] - 우측 배치
                        ElevatedButton(
                          onPressed: () {
                            _navigateToChatRoom(context, _mockHouseId);
                          },
                          // ✨ fontWeight 오류 해결: textStyle 내부에 선언
                          style: ElevatedButton.styleFrom(
                            backgroundColor: accentMint,
                            foregroundColor: primaryNavy,
                            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
                            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                            textStyle: const TextStyle(fontWeight: FontWeight.bold),
                          ),
                          child: const Text('구독'),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ),

          // 🗂️ [기능 명세서 반영] 중간 카테고리 퀵 메뉴 목록
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                _buildQuickMenu(Icons.map_outlined, '관광지'),
                _buildQuickMenu(Icons.calendar_month_outlined, '숙소예약'),
                _buildQuickMenu(Icons.local_shipping_outlined, '배송정보'),
                _buildQuickMenu(Icons.people_outline, '커뮤니티'),
              ],
            ),
          ),

          const Spacer(),

          // 📱 [하단 영역] 회원가입 / 로그인 / 챗봇 버튼 바 (와이어프레임 반영)
          SafeArea(
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Row(
                children: [
                  // 회원가입 버튼
                  Expanded(
                    child: OutlinedButton(
                      onPressed: () {},
                      style: OutlinedButton.styleFrom(
                        side: const BorderSide(color: primaryNavy),
                        padding: const EdgeInsets.symmetric(vertical: 14),
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                      ),
                      child: const Text('회원가입', style: TextStyle(color: primaryNavy, fontWeight: FontWeight.bold)),
                    ),
                  ),
                  const SizedBox(width: 8),
                  // 로그인 버튼
                  Expanded(
                    child: ElevatedButton(
                      onPressed: () {},
                      style: ElevatedButton.styleFrom(
                        backgroundColor: primaryNavy,
                        foregroundColor: Colors.white,
                        padding: const EdgeInsets.symmetric(vertical: 14),
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                      ),
                      child: const Text('로그인', style: TextStyle(fontWeight: FontWeight.bold)),
                    ),
                  ),
                  const SizedBox(width: 8),
                  // 🤖 챗봇 버튼
                  ElevatedButton.icon(
                    onPressed: () {
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(content: Text('Gemini RAG 기반 QnA 챗봇을 준비 중입니다.')),
                      );
                    },
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.black87,
                      foregroundColor: Colors.white,
                      padding: const EdgeInsets.symmetric(vertical: 14, horizontal: 16),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                    ),
                    icon: const Icon(Icons.smart_toy_outlined, size: 18, color: accentMint),
                    label: const Text('챗봇'),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  /// 📦 퀵 메뉴 아이템 생성 빌더
  Widget _buildQuickMenu(IconData icon, String label) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        // ✨ 최신 플러터 문법 반영: withValues 사용
        CircleAvatar(
          radius: 26,
          backgroundColor: primaryNavy.withValues(alpha: 0.08),
          child: Icon(icon, color: primaryNavy, size: 24),
        ),
        const SizedBox(height: 8),
        // ✨ 오타 및 const 규칙 해결 (blackDE -> black87)
        Text(
          label,
          style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w600, color: Colors.black87),
        ),
      ],
    );
  }

  /// 🚀 공통 채팅방 이동 함수
  void _navigateToChatRoom(BuildContext context, int roomId) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => GuestChatScreen(
          chatRoomId: roomId,
          currentUserId: _currentUserId,
          currentUserName: _currentUserName,
        ),
      ),
    );
  }
}