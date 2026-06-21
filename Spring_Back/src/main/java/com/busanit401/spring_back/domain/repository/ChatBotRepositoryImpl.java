package com.busanit401.spring_back.domain.repository;

import com.busanit401.spring_back.dto.ChatBotFaqDocDTO;
import com.busanit401.spring_back.util.VectorParserForChatBot;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.function.BiConsumer;

@Repository
@RequiredArgsConstructor
public class ChatBotRepositoryImpl implements ChatBotRepository {

    /** spring-boot-starter-data-jpa가 자동 등록하는 JdbcTemplate 빈을 주입.
     * 임베딩(MariaDB VECTOR)은 JPA로 매핑이 안 돼 VEC_ToText()로 문자열을 받아 파싱한다. */
    private final JdbcTemplate jdbc;

    @Override
    public List<ChatBotFaqDocDTO> findAll() {
        String sql = "SELECT id, question, answer, VEC_ToText(embedding) AS emb FROM sh_faq_docs";
        return jdbc.query(sql, (rs, rowNum) -> ChatBotFaqDocDTO.builder()
                .id(String.valueOf(rs.getInt("id")))
                .question(rs.getString("question"))
                .answer(rs.getString("answer"))
                .embedding(VectorParserForChatBot.parse(rs.getString("emb")))
                .build());
    }

    /** id(PK)로 단건 조회 (WHERE id=? — 임베딩은 답변 표시에 불필요해 제외). 없으면 null. */
    @Override
    public ChatBotFaqDocDTO findById(String id) {
        String sql = "SELECT id, question, answer FROM sh_faq_docs WHERE id = ?";
        List<ChatBotFaqDocDTO> rows = jdbc.query(sql, (rs, rowNum) -> ChatBotFaqDocDTO.builder()
                .id(String.valueOf(rs.getInt("id")))
                .question(rs.getString("question"))
                .answer(rs.getString("answer"))
                .embedding(new float[0])
                .build(), id);
        return rows.isEmpty() ? null : rows.get(0);
    }

    /** (id, question)만 행 단위로 콜백 — 임베딩 컬럼을 아예 SELECT 안 해 메모리 낭비를 막는다.
     * List로해도 되지만 그렇게 되면 데이터가 많을때 모두 메모리에 쌓아놓게 되므로 메모리에 쌓지 않는 consumer를 사용
     */
    @Override
    public void forEachIdQuestion(BiConsumer<String, String> consumer) {
        String sql = "SELECT id, question FROM sh_faq_docs";
        jdbc.query(sql, (RowCallbackHandler) rs ->
                consumer.accept(String.valueOf(rs.getInt("id")), rs.getString("question")));
    }
}
