/// 🤖 챗봇 RAG 답변 응답 DTO
/// 백엔드 GET /api/chatBot/chat 의 AnswerResult(record) 규격과 1:1 매칭
class ChatBotAnswerDto {
  final String query;   // 사용자가 던진 질문 원문
  final String answer;  // Gemini가 생성한 최종 답변
  final List<ChatBotSourceDto> sources; // 답변 근거로 쓰인 FAQ들

  ChatBotAnswerDto({
    required this.query,
    required this.answer,
    required this.sources,
  });

  factory ChatBotAnswerDto.fromJson(Map<String, dynamic> json) {
    final rawSources = json['sources'] as List<dynamic>? ?? [];
    return ChatBotAnswerDto(
      query: json['query']?.toString() ?? '',
      answer: json['answer']?.toString() ?? '답변을 가져오지 못했습니다.',
      sources: rawSources
          .map((e) => ChatBotSourceDto.fromJson(e as Map<String, dynamic>))
          .toList(),
    );
  }
}

/// 📚 답변의 근거가 된 FAQ 1건 (SearchHit record 매칭)
class ChatBotSourceDto {
  final String id;
  final double score;
  final String question;
  final String answer;

  ChatBotSourceDto({
    required this.id,
    required this.score,
    required this.question,
    required this.answer,
  });

  factory ChatBotSourceDto.fromJson(Map<String, dynamic> json) {
    return ChatBotSourceDto(
      id: json['id']?.toString() ?? '',
      score: (json['score'] as num?)?.toDouble() ?? 0.0,
      question: json['question']?.toString() ?? '',
      answer: json['answer']?.toString() ?? '',
    );
  }
}