import 'dart:convert';
import 'package:stomp_dart_client/stomp_dart_client.dart';
import 'package:http/http.dart' as http;

class GuestChatService {
  StompClient? _stompClient;
  bool _isConnected = false;

  // ==============================================================================================
  // ⚙️ [테스트 환경 스위치 호스트] 테스트 환경에 맞춰 아래 IP 주소 한 줄만 변경하세요!
  // ==============================================================================================
  static const String _ipAddress = '10.0.2.2:8080';       // ① 안드로이드 에뮬레이터 테스트 시
  // static const String _ipAddress = '10.100.201.245:8080'; // ② 실물 스마트폰 및 리액트 연동 테스트 시

  // 호스트 IP를 기반으로 웹소켓과 HTTP 주소를 자동 동기화합니다.
  final String _wsUrl = 'ws://$_ipAddress/ws-guest-chat';
  final String _baseUrl = 'http://$_ipAddress/api/guest/chat';

  bool get isConnected => _isConnected;

  // 채팅방의 과거 메시지 데이터 가져오기 (HTTP GET)=========================================================================
  Future<List<Map<String, dynamic>>> getChatHistory(int chatRoomId) async {
    final url = Uri.parse('$_baseUrl/history/$chatRoomId');

    try {
      print('🌐 [플러터 HTTP] 과거 내역 요청 시작 -> $url');
      final response = await http.get(url);

      if (response.statusCode == 200) {
        final List<dynamic> decodedData = jsonDecode(utf8.decode(response.bodyBytes));
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
        url: _wsUrl, // ✨ 이제 실물 기기 IP 주소로 완벽하게 꽂힙니다!
        onConnect: (StompFrame frame) {
          _isConnected = true;
          print('🟩 [플러팅 웹소켓] 서버 연결 성공!');
          _subscribeRoom(chatRoomId, onMessageReceived);
        },
        onDisconnect: (StompFrame frame) {
          _isConnected = false;
          print('🟥 [플러터 웹소켓] 서버 연결 끊김');
        },
        onStompError: (StompFrame frame) {
          print('⚠️ [플러터 웹소켓] STOMP 에러 발생: ${frame.body}');
        },
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

  /// [3-0] 서버로 메시지 실시간 발송 (상행선)
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
      headers: {'content-type': 'application/json'},
    );
    print('🚀 [플러터 웹소켓] 메시지 발신 완료: $content');
  }

  // /// ✨ [3-1] 서버로 메시지 실시간 수정 요청 (상행선 - 수정)
  // void editMessage({
  //   required int chatId,         // 어떤 메시지를 고칠지 고유 ID (디비 PK)
  //   required int chatRoomId,
  //   required String newContent,   // 새로 변경할 채팅 내용
  // }) {
  //   if (_stompClient == null || !_isConnected) {
  //     print('❌ [플러터 웹소켓] 서버와 연결되어 있지 않습니다.');
  //     return;
  //   }
  //
  //   Map<String, dynamic> editDto = {
  //     'type': 'EDIT',            // ✨ 수정 타입 지정
  //     'chatId': chatId,
  //     'chatRoomId': chatRoomId,
  //     'content': newContent,
  //   };
  //
  //   _stompClient?.send(
  //     destination: '/app/guest/chat/edit', // 백엔드 @MessageMapping("/guest/chat/edit")와 매핑
  //     body: jsonEncode(editDto),
  //   );
  //   print('📝 [플러터 웹소켓] 메시지 수정 요청 완료 (ID: $chatId) -> $newContent');
  // }
  //
  // /// ✨ [3-2] 서버로 메시지 실시간 삭제 요청 (상행선 - 삭제)
  // void deleteMessage({
  //   required int chatId,         // 어떤 메시지를 지울지 고유 ID
  //   required int chatRoomId,
  // }) {
  //   if (_stompClient == null || !_isConnected) {
  //     print('❌ [플러터 웹소켓] 서버와 연결되어 있지 않습니다.');
  //     return;
  //   }
  //
  //   Map<String, dynamic> deleteDto = {
  //     'type': 'DELETE',          // ✨ 삭제 타입 지정
  //     'chatId': chatId,
  //     'chatRoomId': chatRoomId,
  //   };
  //
  //   _stompClient?.send(
  //     destination: '/app/guest/chat/delete', // 백엔드 @MessageMapping("/guest/chat/delete")와 매핑
  //     body: jsonEncode(deleteDto),
  //   );
  //   print('🗑️ [플러터 웹소켓] 메시지 삭제 요청 완료 (ID: $chatId)');
  // }

  /// [4] 채팅방을 나갈 때 연결 안전하게 해제
  void disconnect() {
    _stompClient?.deactivate();
    _stompClient = null;
    _isConnected = false;
    print('🔌 [플러터 웹소켓] 연결 안전하게 해제 완료');
  }
}