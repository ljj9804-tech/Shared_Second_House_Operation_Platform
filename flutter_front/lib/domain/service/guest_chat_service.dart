import 'dart:convert';
import 'package:stomp_dart_client/stomp_dart_client.dart';
import 'package:http/http.dart' as http;

class GuestChatService {
  StompClient? _stompClient;
  bool _isConnected = false;

  // 백엔드 컴퓨터의 IP 주소 (안드로이드 에뮬레이터 기준 10.0.2.2)
  final String _wsUrl = 'ws://10.0.2.2:8080/ws-guest-chat';
  // 1) 에뮬레이터로 테스트할 때
  final String _baseUrl = 'http://10.0.2.2:8080/api/guest/chat';
  // 2) 실물 스마트폰으로 테스트할 때 (💡 본인 컴퓨터의 실제 IP 주소로 수정하세요!)
  // static const String _ip = '222.234.36.85:8080';

  bool get isConnected => _isConnected;

  // 채팅방의 과거 메시지 데이터 가져오기 (HTTP GET)==============================================================================================
  Future<List<Map<String, dynamic>>> getChatHistory(int chatRoomId) async {
    final url = Uri.parse('$_baseUrl/history/$chatRoomId');

    try {
      print('🌐 [플러터 HTTP] 과거 내역 요청 시작 -> $url');
      final response = await http.get(url);

      if (response.statusCode == 200) {
        // ✨ 한글 깨짐 방지를 위해 UTF-8로 먼저 디코딩 후 JSON 파싱합니다.
        final List<dynamic> decodedData = jsonDecode(utf8.decode(response.bodyBytes));

        // List<dynamic> 구조를 데이터 다루기 편하게 List<Map<String, dynamic>>으로 안전하게 성형하여 반환합니다.
        return decodedData.map((item) => item as Map<String, dynamic>).toList();
      } else {
        print('❌ [플러터 HTTP] 서버 에러 발생 (코드: ${response.statusCode})');
        return [];
      }
    } catch (e) {
      print('❌ [플러터 HTTP] 통신 중 네트워크 예외 에러 발생: $e');
      return [];
    }
  }

  // 웹소켓 서버 연결 작업 로직==============================================================================================
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