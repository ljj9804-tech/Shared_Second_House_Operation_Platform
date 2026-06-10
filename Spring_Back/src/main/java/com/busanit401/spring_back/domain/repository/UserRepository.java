package com.busanit401.spring_back.domain.repository;

import com.busanit401.spring_back.domain.User;
import com.busanit401.spring_back.enums.Role;
import com.busanit401.spring_back.domain.repository.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    // 로그인 시 사용
    Optional<User> findByUsername(String username);

    // 소셜 로그인 시 사용
    Optional<User> findByEmail(String email);

    // 중복 체크
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);

    // 역할별 유저 목록 (관리자 페이지)
    List<User> findAllByRole(Role role);

    // 아이디 또는 이메일 목록으로 유저 한 번에 조회
    @Query("SELECT u FROM User u WHERE u.username IN :identifiers OR u.email IN :identifiers")
    List<User> findAllByUsernameInOrEmailIn(@Param("identifiers") List<String> identifiers);
}