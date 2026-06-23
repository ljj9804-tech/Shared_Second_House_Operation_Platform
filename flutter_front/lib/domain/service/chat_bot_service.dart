import 'package:dio/dio.dart';
import 'package:flutter_front/config/app_config.dart';
import 'package:flutter_front/domain/dto/chat_bot_answer_dto.dart';

/// 🤖 Gemini RAG 챗봇 통신 서비스
/// 웹소켓이 아닌 HTTP 단발성 요청/응답 구조 (질문 1개 → 답변 1개)
class ChatBotService {

  // 안드로이드 에뮬레이터 전용 메인 백엔드 주소 (포트 8080)
  final String _baseUrl = AppConfig.baseUrl + "/chatBot";
  final Dio _dio = Dio();

  /// RAG 답변 생성 호출
  /// 백엔드: GET /api/chatBot/chat?q=<질문>&topK=<근거 개수>
  Future<ChatBotAnswerDto> ask(String question, {int topK = 3}) async {
    print("🚀 [챗봇 통신] 질문 요청 -> q: $question, topK: $topK");

    final response = await _dio.get(
      "$_baseUrl/chat",
      queryParameters: {
        "q": question,
        "topK": topK,
      },
    );

    print("🟢 [챗봇 통신 성공]: ${response.data}");
    return ChatBotAnswerDto.fromJson(response.data as Map<String, dynamic>);
  }
}