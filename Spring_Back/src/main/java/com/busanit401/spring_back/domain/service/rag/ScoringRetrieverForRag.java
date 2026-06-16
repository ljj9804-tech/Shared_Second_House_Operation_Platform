package com.busanit401.spring_back.domain.service.rag;
import com.busanit401.spring_back.dto.ChatBotScoredDocDTO;

import java.util.List;

/**
 * RAG 검색기 공통 계약 (점수 포함). top-k 결과를 점수와 함께 반환하므로
 * 융합(가중합/RRF)의 입력으로도 쓸 수 있다. (단어/의미 검색기 모두 이 인터페이스만 구현)
 */
public interface ScoringRetrieverForRag {

    /** 결과표/로그용 이름. 예: "lucene_bm25", "embedding" */
    String name();

    /** top-k 결과를 점수와 함께 내림차순으로 반환(점수 0 이하 제외). */
    List<ChatBotScoredDocDTO> searchScored(String query, int k);
}