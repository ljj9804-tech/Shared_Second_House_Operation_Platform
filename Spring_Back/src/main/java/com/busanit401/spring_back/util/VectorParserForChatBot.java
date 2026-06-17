package com.busanit401.spring_back.util;

/**
 * 벡터 텍스트 파서. MariaDB {@code VEC_ToText()}가 돌려주는 {@code "[a,b,c]"} 형태 문자열을
 * {@code float[]}로 바꾼다. (DB 접근과 무관한 순수 문자열 처리라 유틸로 분리)
 */
public final class VectorParserForChatBot {   //변경을 막기 위해

    private VectorParserForChatBot() {    //생성자 만들지 못하게 private로 생성자 정의
    }

    /** {@code "[0.018,0.014,-0.027]"} → {@code float[]}. null/빈 문자열/{@code "[]"}는 빈 배열. */
    public static float[] parse(String text) {
        if (text == null || text.isBlank()) {
            return new float[0];
        }
        String body = text.trim();
        if (body.startsWith("[")) {
            body = body.substring(1);
        }
        if (body.endsWith("]")) {
            body = body.substring(0, body.length() - 1);
        }
        if (body.isBlank()) {
            return new float[0];
        }
        String[] parts = body.split(",");
        float[] vec = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            vec[i] = Float.parseFloat(parts[i].trim());
        }
        return vec;
    }
}