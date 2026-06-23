"use client";

import Link from "next/link";
import styles from "./Navbar.module.css";
import { TEMP_USER_ID } from "@/app/lib/auth";

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
            내 예약 (userId: {TEMP_USER_ID})
          </Link>
        </nav>
      </div>
    </header>
  );
}
