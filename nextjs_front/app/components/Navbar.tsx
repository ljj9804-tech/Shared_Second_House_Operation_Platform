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

'use client';

import Link from 'next/link';
import { useEffect, useState } from 'react';
import { useRouter, usePathname } from 'next/navigation';
import styles from './Navbar.module.css';
import { tokenStorage } from '@/lib/token';
import { api } from '@/lib/api';
import { UserResp } from '@/types/auth';


export default function Navbar() {
  const router = useRouter();

  const [user, setUser] = useState<UserResp | null>(null);
  const [isAdmin, setIsAdmin] = useState(false);
  const [authLoading, setAuthLoading] = useState(true);
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  useEffect(() => {
    const token = tokenStorage.get();
    if (!token) {
      Promise.resolve().then(() => setAuthLoading(false));
      return;
    }

    api
      .get<UserResp>("/api/users")
      .then((res) => {
        setUser(res);
        setIsAdmin(res.role === "ADMIN");
      })
      .catch(() => {
        // 비로그인 상태 등 - 로그인 안 한 것으로 처리, Navbar는 정상 노출
        setUser(null);
        setIsAdmin(false);
      })
      .finally(() => {
        setAuthLoading(false);
      });
  }, []);

  // 라우트 변경 시 메뉴 닫기
  useEffect(() => {
    setIsMenuOpen(false);
  }, [pathname]);

  const handleLogout = () => {
    // TODO: tokenStorage의 삭제 메서드 이름이 clear()가 아니라면 여기 수정 필요
    tokenStorage.remove();
    setUser(null);
    setIsAdmin(false);
    router.push("/");
  };

  return (
    <>
      {isMenuOpen && (
        <div className={styles.overlay} onClick={() => setIsMenuOpen(false)} />
      )}
      <header className={styles.header}>
        <div className={styles.headerInner}>
          <Link href="/" className={styles.logo}>
            세컨하우스
          </Link>
          <button
            className={styles.hamburger}
            onClick={() => setIsMenuOpen((prev) => !prev)}
            aria-label="메뉴"
          >
            {isMenuOpen ? '✕' : '☰'}
          </button>
          <nav className={`${styles.nav} ${isMenuOpen ? styles.navOpen : ''}`}>
          {/* <Link href="/" className={styles.navItem}>
            홈
          </Link> */}
          <Link href="/accommodations" className={styles.navItem}>
            숙소 목록
          </Link>
          <Link href="/my/reservations" className={styles.navItem}>
            내 예약
          </Link>
          <Link href="/product" className={styles.navItem}>
            상품 스토어
          </Link>
          <Link href="/cart" className={styles.navItem}>
            장바구니
          </Link>
          <Link href="/tours" className={styles.navItem}>
            관광지
          </Link>
          {isAdmin && (
            <Link href="/accommodations" className={styles.navItem}>
              숙소 등록
            </Link>
          )}
          <Link href="/mypage" className={styles.navItem}>
            마이페이지
          </Link>

          {/* 👑 관리자 계정일 때만 배달 관리 콘솔 메뉴가 보임 */}
          {isAdmin && (
            <Link
              href="/delivery"
              className={styles.navItem}
              style={{ color: 'orange', fontWeight: 'bold' }}
            >
              배달 관리 ★
            </Link>
          )}

          {authLoading ? null : user ? (
            <>
              <span className={styles.navItem}>
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
    </>
  );
}
