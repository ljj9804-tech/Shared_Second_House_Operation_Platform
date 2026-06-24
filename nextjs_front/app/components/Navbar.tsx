/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : app/components/Navbar.tsx
 * 역할  : 전체 앱 상단 네비게이션 바 (공통 레이아웃)
 * 사용처 : app/layout.tsx
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - Navbar.module.css  : 네비게이션 스타일
 * - lib/token.ts       : tokenStorage (로그인 상태 확인용)
 * - lib/api.ts         : fetch 기반 API 클라이언트
 * - types/auth.ts      : UserResp 타입
 * ----------------------------------------------------------------------------------
 * [기능 목록]
 * - 로고 클릭 시 홈(/)으로 이동
 * - 네비게이션 링크: 홈 / 숙소 목록 / 내 예약 / 마이페이지
 * - 로그인 상태에 따라 회원 닉네임 + 로그아웃 / 로그인 버튼 표시
 * - 로그아웃 시 토큰 삭제 후 홈으로 이동
 * ==================================================================================
 */

"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { useRouter, usePathname } from "next/navigation";
import styles from "./Navbar.module.css";
import { tokenStorage } from "@/lib/token";
import { api } from "@/lib/api";
import { UserResp } from "@/types/auth";

export default function Navbar() {
  const [isAdmin, setIsAdmin] = useState(false);
  const router = useRouter();
  const pathname = usePathname();
  const [user, setUser] = useState<UserResp | null>(null);
  const [authLoading, setAuthLoading] = useState(true);

  useEffect(() => {
    const token = tokenStorage.get();
    if (!token) {
      setUser(null);
      setAuthLoading(false);
      return;
    }

    api
      .get<UserResp>("/api/users")
      .then((data) => {
        console.log("[Navbar] 로그인 유저:", data);
        setIsAdmin(data.role === "ADMIN");
        setUser(data);
      })
      .catch(() => {
seonggyu
        // 토큰 만료 등 → lib/api.ts가 자동으로 /login 리다이렉트 처리
        setUser(null);
      })
      .finally(() => setAuthLoading(false));
  }, [pathname]);

  const handleLogout = () => {
    tokenStorage.remove();
    setUser(null);
    router.push('/');
  };
const isAdmin = true;
        setIsAdmin(false);
      })
      .finally(() => setAuthLoading(false));
  }, [pathname]);

  const handleLogout = () => {
    tokenStorage.remove();
    setUser(null);
    router.push("/");
  };

middle
  return (
    <header className={styles.header}>
      <div className={styles.headerInner}>
        <Link href="/" className={styles.logo}>
          세컨하우스
        </Link>
        <nav className={styles.nav}>
          <Link href="/" className={styles.navItem}>
            홈
          </Link>
          <Link href="/accommodations" className={styles.navItem}>
            숙소 목록
          </Link>
seonggyu
          <Link href="/my/reservations" className={styles.navItem}>
            내 예약
          </Link>
          <Link href="/product" className={styles.navItem}>
          상품 스토어
        </Link>
        <Link href="/cart" className={styles.navItem}>
          장바구니
        </Link>
        <Link href="/mypage" className={styles.navItem}>
=======
          {isAdmin && (
            <Link href="/accommodations" className={styles.navItem}>
              숙소 등록
            </Link>
          )}
          <Link href="/mypage" className={styles.navItem}>
 middle
            마이페이지
          </Link>
          
        {/* 👑 관리자 계정일 때만 배달 관리 콘솔 메뉴가 보임 */}
        {isAdmin && (
          <Link href="/delivery" className={styles.navItem} style={{ color: 'orange', fontWeight: 'bold' }}>
            배달 관리 ★
          </Link>
        )}
        
          {authLoading ? null : user ? (
            <>
              <span className={styles.navItem}>
                {/* 임시 테스트용 */}
                {user.nickname}님 환영합니다 (ID: {user.userId})
              </span>
              <button className={styles.navItem} onClick={handleLogout}>
                로그아웃
              </button>
            </>
          ) : (
            <Link href="/login" className={styles.navItem}>
              로그인
            </Link>
          )}
        </nav>
      </div>
    </header>
  );
}
