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
    List<Map<String, dynamic>> history = await _chatService.getChatHistory(widget.chatRoomId);

    if (mounted) {
      setState(() {
        _messages.addAll(history);
        _isLoading = false;
      });
      _scrollToBottom();
    }

    _chatService.connectWebSocket(
      chatRoomId: widget.chatRoomId,
      onMessageReceived: (Map<String, dynamic> incomingMessage) {
        if (!mounted) return;

        // // 🟩 [주석 처리 반영] 현재는 일반 대화(TALK)만 처리하므로 분기 로직은 백업 주석화
        // final String type = incomingMessage['type'] ?? 'TALK';
        // // final int targetChatId = incomingMessage['chatId'] ?? 0;
        //
        // setState(() {
        //   if (type == 'TALK') {
        //     // 일반 대화는 리스트에 순수 추가
        //     _messages.add(incomingMessage);
        //   }
        //   /* [일정 단축으로 인한 수정/삭제 기능 일시 주석 처리]
        //   else if (type == 'EDIT') {
        //     final index = _messages.indexWhere((m) => (m['chatId'] ?? m['id']) == targetChatId);
        //     if (index != -1) {
        //       _messages[index]['content'] = incomingMessage['content'];
        //       _messages[index]['messageContent'] = incomingMessage['content'];
        //     }
        //   }
        //   else if (type == 'DELETE') {
        //     _messages.removeWhere((m) => (m['chatId'] ?? m['id']) == targetChatId);
        //   }
        //   */
        // });
        _scrollToBottom();
      },
    );
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

  /* [일정 단축으로 인한 수정/삭제 모달 및 익스텐션 UI 관련 메서드 전체 주석 처리]
  void _showChatOptions(BuildContext context, Map<String, dynamic> chat) {
    final int messageId = chat['chatId'] ?? chat['id'] ?? 0;
    final String currentText = chat['content'] ?? chat['messageContent'] ?? '';

    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      builder: (context) {
        return SafeArea(
          child: Wrap(
            children: [
              ListTile(
                leading: const Icon(Icons.edit, color: primaryNavy),
                title: const Text('메시지 수정'),
                onTap: () {
                  Navigator.pop(context);
                  _showEditDialog(messageId, currentText);
                },
              ),
              ListTile(
                leading: const Icon(Icons.delete_outline, color: Colors.redAccent),
                title: const Text('메시지 삭제', style: TextStyle(color: Colors.redAccent)),
                onTap: () {
                  Navigator.pop(context);
                  _processDeleteMessage(messageId);
                },
              ),
            ],
          ),
        );
      },
    );
  }

  void _showEditDialog(int messageId, String oldText) {
    final textController = TextEditingController(text: oldText);
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('메시지 수정', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
        content: TextField(
          controller: textController,
          decoration: const InputDecoration(hintText: "수정할 내용을 입력하세요"),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('취소')),
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              _processEditMessage(messageId, textController.text.trim());
            },
            child: const Text('수정', style: TextStyle(color: primaryNavy)),
          ),
        ],
      ),
    );
  }

  void _processEditMessage(int messageId, String newContent) {
    if (newContent.isEmpty) return;
    // _chatService.editMessage(
    //   chatId: messageId,
    //   chatRoomId: widget.chatRoomId,
    //   newContent: newContent,
    // );
  }

  void _processDeleteMessage(int messageId) {
    // _chatService.deleteMessage(
    //   chatId: messageId,
    //   chatRoomId: widget.chatRoomId,
    // );
  }
  */

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