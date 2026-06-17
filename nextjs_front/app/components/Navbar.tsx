'use client';

import Link from 'next/link';
import styles from './Navbar.module.css';

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
          <Link href="/food" className={styles.navItem}>
            주변 맛집
          </Link>
          <Link href="/chatbot" className={styles.navItem}>
            AI 챗봇
          </Link>
          <Link href="/my/reservations" className={styles.navItem}>
            내 예약
          </Link>
        </nav>
      </div>
    </header>
  );
}
