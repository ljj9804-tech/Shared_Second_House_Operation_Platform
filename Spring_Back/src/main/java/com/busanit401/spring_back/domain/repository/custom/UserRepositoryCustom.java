package com.busanit401.spring_back.domain.repository.custom;

import com.busanit401.spring_back.domain.User;
import java.util.List;

public interface UserRepositoryCustom {

    // 이름 또는 닉네임으로 유저 검색 (관리자 페이지)
    List<User> searchByKeyword(String keyword);
}