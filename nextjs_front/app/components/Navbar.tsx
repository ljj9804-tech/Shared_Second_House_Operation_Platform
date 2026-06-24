/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : app/components/Navbar.tsx
 * 역할  : 전체 앱 상단 네비게이션 바 (공통 레이아웃)
 * 사용처 : app/layout.tsx
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - Navbar.module.css : 네비게이션 스타일
 * - app/lib/auth.ts   : TEMP_USER_ID (임시 userId 표시용)
 * ----------------------------------------------------------------------------------
 * [기능 목록]
 * - 로고 클릭 시 홈(/)으로 이동
 * - 네비게이션 링크: 홈 / 숙소 목록 / 내 예약
 * - 임시 userId 표시 (로그인 연동 전 확인용)
 * ----------------------------------------------------------------------------------
 * [주의사항 / 참고]
 * ⚠️ [TODO] 로그인 연동 후: userId 표시 제거, 로그인 정보(이름·프로필) 표시로 교체
 * ==================================================================================
 */

"use client";

import Link from "next/link";
import styles from "./Navbar.module.css";

export default function Navbar() {
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
          <Link href="/mypage" className={styles.navItem}>
            마이페이지
          </Link>
          <Link href="/my/reservations" className={styles.navItem}>
            내 예약
          </Link>
        </nav>
      </div>
    </header>
  );
}
