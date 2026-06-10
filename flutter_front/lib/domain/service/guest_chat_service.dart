import 'dart:convert';
import 'package:stomp_dart_client/stomp_dart_client.dart';

class GuestChatService {
  StompClient? _stompClient;
  bool _isConnected = false;

  // 백엔드 컴퓨터의 IP 주소 (안드로이드 에뮬레이터 기준 10.0.2.2)
  final String _wsUrl = 'ws://10.0.2.2:8080/ws-guest-chat';

  bool get isConnected => _isConnected;

  /// [1] 웹소켓 서버 초기화 및 연결 시작
  void connectWebSocket({
    required int chatRoomId,
    required Function(Map<String, dynamic>) onMessageReceived,
  }) {
    if (_stompClient != null && _isConnected) return;

    _stompClient = StompClient(
      config: StompConfig(
        url: _wsUrl,
        onConnect: (StompFrame frame) {
          _isConnected = true;
          print('🟩 [플러팅 웹소켓] 서버 연결 성공!');
          _subscribeRoom(chatRoomId, onMessageReceived);
        },

        // 아래 콜백들도 에러 메시지의 규칙에 맞게 인자 1개짜리 정석 타입으로 통일합니다.
        onDisconnect: (StompFrame frame) {
          _isConnected = false;
          print('🟥 [플러터 웹소켓] 서버 연결 끊김');
        },

        onStompError: (StompFrame frame) {
          print('⚠️ [플러터 웹소켓] STOMP 에러 발생: ${frame.body}');
        },

        // 웹소켓 에러는 관례상 타입을 생략하거나 dynamic 대신 공식 사양에 맞게 인자명만 적어줍니다.
        onWebSocketError: (error) {
          print('❌ [플러터 웹소켓] 웹소켓 통신 에러 발생: $error');
        },
      ),
    );

    _stompClient?.activate();
  }

  /// [2] 특정 채팅방 구독하기 (하행선)
  void _subscribeRoom(int chatRoomId, Function(Map<String, dynamic>) onMessageReceived) {
    _stompClient?.subscribe(
      destination: '/topic/guest/room/$chatRoomId',
      callback: (StompFrame frame) {
        if (frame.body != null) {
          Map<String, dynamic> messageData = jsonDecode(frame.body!);
          onMessageReceived(messageData);
        }
      },
    );
    print('📢 [플러터 웹소켓] 구독 완료 -> /topic/guest/room/$chatRoomId');
  }

  /// [3] 서버로 메시지 실시간 발송 (상행선)
  void sendMessage({
    required int chatRoomId,
    required int senderId,
    required String senderName,
    required String content,
  }) {
    if (_stompClient == null || !_isConnected) {
      print('❌ [플러터 웹소켓] 서버와 연결되어 있지 않습니다.');
      return;
    }

    Map<String, dynamic> chatDto = {
      'chatRoomId': chatRoomId,
      'senderId': senderId,
      'senderName': senderName,
      'content': content,
    };

    _stompClient?.send(
      destination: '/app/guest/chat/send',
      body: jsonEncode(chatDto),
    );
    print('🚀 [플러터 웹소켓] 메시지 발신 완료: $content');
  }

  /// [4] 채팅방을 나갈 때 연결 안전하게 해제
  void disconnect() {
    _stompClient?.deactivate();
    _stompClient = null;
    _isConnected = false;
    print('🔌 [플러터 웹소켓] 연결 안전하게 해제 완료');
  }
}