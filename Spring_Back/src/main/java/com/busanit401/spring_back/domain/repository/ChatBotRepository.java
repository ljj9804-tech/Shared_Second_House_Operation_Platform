package com.busanit401.spring_back.domain.repository;

import com.busanit401.spring_back.dto.ChatBotFaqDocDTO;

import java.util.List;
import java.util.function.BiConsumer;

public interface ChatBotRepository {
    /** 전체 문서를 로드한다. */
    List<ChatBotFaqDocDTO> findAll();

    /**
     * id(PK)로 단건 조회. 없으면 null.
     * <p>소규모 전제라 {@link #findAll()} 스캔으로 구현해도 되고, 효율이 필요하면 전용 쿼리로 구현한다.
     */
    ChatBotFaqDocDTO findById(String id);

    /**
     * (id, question)만 행 단위로 흘려보낸다. 자동완성 빌드처럼 <b>임베딩이 불필요</b>할 때 사용
     * (findAll은 임베딩 1536 floats까지 다 로드 → 낭비). 구현체는 가벼운 쿼리로 처리한다.
     */
    void forEachIdQuestion(BiConsumer<String, String> consumer);
}
