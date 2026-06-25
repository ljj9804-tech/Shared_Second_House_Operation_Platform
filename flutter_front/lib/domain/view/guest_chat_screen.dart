import 'dart:async';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
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
  final List<Map<String, dynamic>> _messages = [];
  final ScrollController _scrollController = ScrollController();

  bool _isLoading = true;

  static const Color primaryNavy = Color(0xFF23399D);

  @override
  void initState() {
    super.initState();
    _initChatRoomSequence();
  }

  Future<void> _initChatRoomSequence() async {
    try {
      // 1. 과거 대화 내역 조회 (Next.js의 history API 페칭과 동일)
      List<Map<String, dynamic>> history = await _chatService.getChatHistory(widget.chatRoomId);

      if (mounted) {
        setState(() {
          _messages.addAll(history);
          _isLoading = false;
        });
        _scrollToBottom();
      }

      // 2. 웹소켓 실시간 구독 시작 (토큰 연동이 서비스 내부에서 처리되는지 확인 필요)
      _chatService.connectWebSocket(
        chatRoomId: widget.chatRoomId,
        onMessageReceived: (Map<String, dynamic> incomingMessage) {
          if (!mounted) return;

          // 🟩 [수정] 실시간으로 수신된 메시지를 리스트에 반영하도록 setState 추가
          setState(() {
            _messages.add(incomingMessage);
          });
          _scrollToBottom();
        },
      );
    } catch (e) {
      // 에러 핸들링 추가 (네트워크나 인증 에러 발생 시 익명 튕김 방지용 상태 제어)
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('채팅방을 불러오는 중 오류가 발생했습니다: $e')),
        );
      }
    }
  }

  @override
  void dispose() {
    _chatService.disconnect();
    _messageController.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  void _handleSend() {
    final text = _messageController.text.trim();
    if (text.isEmpty) return;

    _chatService.sendMessage(
      chatRoomId: widget.chatRoomId,
      senderId: widget.currentUserId,
      senderName: widget.currentUserName,
      content: text,
    );

    _messageController.clear();
  }

  String _formatTime(String? isoString) {
    if (isoString == null || isoString.isEmpty) return '';
    try {
      DateTime dateTime = DateTime.parse(isoString);
      return DateFormat('a h:mm').format(dateTime);
    } catch (e) {
      return '';
    }
  }


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
      backgroundColor: const Color(0xFFF4F6FA),
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
          ? const Center(child: CircularProgressIndicator(color: primaryNavy))
          : Column(
        children: [
          Expanded(
            child: ListView.builder(
              controller: _scrollController,
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              itemCount: _messages.length,
              itemBuilder: (context, index) {
                final chat = _messages[index];

                final String myId = widget.currentUserId.toString();
                final String senderId = (chat['senderId'] ?? chat['sender_id'] ?? '').toString();
                final bool isMe = (myId == senderId);

                final String messageContent = chat['content'] ?? chat['messageContent'] ?? '내용 없음';
                final String senderName = chat['senderName'] ?? '게스트';

                final String rawTime = chat['sentAt'] ?? chat['sent_at'] ?? '';
                final String formattedTime = _formatTime(rawTime);

                return Align(
                  alignment: isMe ? Alignment.centerRight : Alignment.centerLeft,
                  child: Row(
                    mainAxisAlignment: isMe ? MainAxisAlignment.end : MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.end,
                    children: [
                      if (!isMe) ...[
                        CircleAvatar(
                          radius: 18,
                          backgroundColor: primaryNavy.withOpacity(0.1),
                          child: const Icon(Icons.person, color: primaryNavy, size: 18),
                        ),
                        const SizedBox(width: 8),
                      ],

                      if (isMe && formattedTime.isNotEmpty) ...[
                        Text(formattedTime, style: const TextStyle(fontSize: 10, color: Colors.black38)),
                        const SizedBox(width: 6),
                      ],

                      // 🟩 [교정 완료] onLongPress 진입로를 null로 차단하여 롱클릭 모달창이 뜨지 않도록 완전 방어
                      GestureDetector(
                        onLongPress: null,
                        child: Column(
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
                                maxWidth: MediaQuery.of(context).size.width * 0.55,
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
                                    color: Colors.black.withOpacity(0.04),
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
                      ),

                      if (!isMe && formattedTime.isNotEmpty) ...[
                        const SizedBox(width: 6),
                        Text(formattedTime, style: const TextStyle(fontSize: 10, color: Colors.black38)),
                      ],
                    ],
                  ),
                );
              },
            ),
          ),

          SafeArea(
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 6),
              decoration: BoxDecoration(
                color: Colors.white,
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.05),
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