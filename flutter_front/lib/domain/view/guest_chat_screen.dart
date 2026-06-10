import 'dart:async';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_front/domain/service/guest_chat_service.dart';

class GuestChatScreen extends StatefulWidget {
  final int chatRoomId;
  final int currentUserId;
  final String currentUserName;

  const GuestChatScreen({
    super.key,
    required this.chatRoomId,
    required this.currentUserId,
    required this.currentUserName,
  });

  @override
  State<GuestChatScreen> createState() => _GuestChatScreenState();
}

class _GuestChatScreenState extends State<GuestChatScreen> {
  final GuestChatService _chatService = GuestChatService();
  final TextEditingController _messageController = TextEditingController();
  final List<Map<String, dynamic>> _messages = []; // 대화 내역 통합 바구니
  final ScrollController _scrollController = ScrollController();

  bool _isLoading = true; // 과거 내역 조회 로딩 상태

  // 🎨 플랫폼 시그니처 딥 네이비 테마 컬러
  static const Color primaryNavy = Color(0xFF23399D);

  @override
  void initState() {
    super.initState();
    // 🟩 화면 진입 시 과거 내역 다운로드 후 웹소켓 연결 순차 가동
    _initChatRoomSequence();
  }

  /// 🔄 과거 내역 로드 완료 후 웹소켓을 연결하는 정석 시퀀스
  Future<void> _initChatRoomSequence() async {
    // ① 백엔드 DB로부터 과거 데이터 호출
    List<Map<String, dynamic>> history = await _chatService.getChatHistory(widget.chatRoomId);

    if (mounted) {
      setState(() {
        _messages.addAll(history); // 옛날 기록 바구니에 탑승
        _isLoading = false;        // 로딩 서클 종료
      });
      _scrollToBottom(); // 대화 최하단으로 스크롤 이동
    }

    // ② 정리가 끝난 직후 실시간 통신 웹소켓 연결
    _chatService.connectWebSocket(
      chatRoomId: widget.chatRoomId,
      onMessageReceived: (Map<String, dynamic> incomingMessage) {
        if (mounted) {
          setState(() {
            _messages.add(incomingMessage); // 실시간 톡 추가
          });
          _scrollToBottom(); // 새 메시지 오면 아래로 밀어주기
        }
      },
    );
  }

  @override
  void dispose() {
    // 🟥 연결 해제 및 메모리 자원 반납
    _chatService.disconnect();
    _messageController.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  /// 🚀 전송 버튼 클릭 시 백엔드로 메시지 발송
  void _handleSend() {
    final text = _messageController.text.trim();
    if (text.isEmpty) return;

    _chatService.sendMessage(
      chatRoomId: widget.chatRoomId,
      senderId: widget.currentUserId,
      senderName: widget.currentUserName,
      content: text,
    );

    _messageController.clear(); // 입력창 비우기
  }

  /// 📜 리스트를 맨 아래로 부드럽게 스크롤
  void _scrollToBottom() {
    Future.delayed(const Duration(milliseconds: 150), () {
      if (_scrollController.hasClients) {
        _scrollController.animateTo(
          _scrollController.position.maxScrollExtent,
          duration: const Duration(milliseconds: 200),
          curve: Curves.easeOut,
        );
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF4F6FA), // 깔끔한 연회색 배경
      appBar: AppBar(
        title: Text(
          '${widget.chatRoomId}번 채팅방',
          style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
        ),
        centerTitle: true,
        backgroundColor: primaryNavy,
        iconTheme: const IconThemeData(color: Colors.white),
        elevation: 0,
      ),
      body: _isLoading
          ? const Center(
        child: CircularProgressIndicator(color: primaryNavy),
      )
          : Column(
        children: [
          // 💬 메시지 리스트 영역
          Expanded(
            child: ListView.builder(
              controller: _scrollController,
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              itemCount: _messages.length,
              itemBuilder: (context, index) {
                final chat = _messages[index];

                // ① [ID 매칭 비교 전처리]
                final String myId = widget.currentUserId.toString();
                final String senderId = (chat['senderId'] ?? chat['sender_id'] ?? '').toString();
                final bool isMe = (myId == senderId);

                // ② [대화 내용 Key 다중 매핑 분석]
                // ✨ 로그에서 확인된 'messageContent'를 가장 최우선순위로 파싱하도록 심었습니다!
                final String messageContent = chat['messageContent'] ??
                    chat['content'] ??
                    chat['message'] ??
                    chat['chatContent'] ??
                    chat['text'] ??
                    chat['msg'] ?? '내용 없음';

                // ③ [보낸이 이름 Key 다중 매핑 분석]
                final String senderName = chat['senderName'] ??
                    chat['sender_name'] ??
                    chat['chatSenderName'] ?? '알 수 없음';

                return Align(
                  alignment: isMe ? Alignment.centerRight : Alignment.centerLeft,
                  child: Row(
                    mainAxisAlignment: isMe ? MainAxisAlignment.end : MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      if (!isMe) ...[
                        CircleAvatar(
                          radius: 18,
                          backgroundColor: primaryNavy.withValues(alpha: 0.1),
                          child: const Icon(Icons.person, color: primaryNavy, size: 18),
                        ),
                        const SizedBox(width: 8),
                      ],
                      Column(
                        crossAxisAlignment: isMe ? CrossAxisAlignment.end : CrossAxisAlignment.start,
                        children: [
                          if (!isMe) ...[
                            Text(
                              senderName,
                              style: const TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Colors.black54),
                            ),
                            const SizedBox(height: 4),
                          ],
                          Container(
                            margin: const EdgeInsets.symmetric(vertical: 2),
                            padding: const EdgeInsets.symmetric(vertical: 10, horizontal: 14),
                            constraints: BoxConstraints(
                              maxWidth: MediaQuery.of(context).size.width * 0.65,
                            ),
                            decoration: BoxDecoration(
                              color: isMe ? const Color(0xFFD6E4FF) : Colors.white,
                              borderRadius: BorderRadius.only(
                                topLeft: const Radius.circular(12),
                                topRight: const Radius.circular(12),
                                bottomLeft: isMe ? const Radius.circular(12) : const Radius.circular(2),
                                bottomRight: isMe ? const Radius.circular(2) : const Radius.circular(12),
                              ),
                              boxShadow: [
                                BoxShadow(
                                  color: Colors.black.withValues(alpha: 0.04),
                                  blurRadius: 3,
                                  offset: const Offset(0, 1),
                                )
                              ],
                            ),
                            child: Text(
                              messageContent,
                              style: const TextStyle(fontSize: 14, color: Colors.black87, height: 1.3),
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                );
              },
            ),
          ),

          // ⌨️ 하단 입력창 영역
          SafeArea(
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 6),
              decoration: BoxDecoration(
                color: Colors.white,
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withValues(alpha: 0.05),
                    blurRadius: 4,
                    offset: const Offset(0, -2),
                  )
                ],
              ),
              child: Row(
                children: [
                  Expanded(
                    child: TextField(
                      controller: _messageController,
                      style: const TextStyle(fontSize: 14),
                      decoration: const InputDecoration(
                        hintText: '메시지를 입력하세요...',
                        border: InputBorder.none,
                        contentPadding: EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                      ),
                      onSubmitted: (_) => _handleSend(),
                    ),
                  ),
                  IconButton(
                    icon: const Icon(Icons.send, color: primaryNavy),
                    onPressed: _handleSend,
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}