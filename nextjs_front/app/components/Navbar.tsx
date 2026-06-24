"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import styles from "./Navbar.module.css";
import { TEMP_USER_ID } from "@/app/lib/auth";
import { api } from "@/lib/api";
import { UserResp } from "@/types/auth";

export default function Navbar() {
  const [isAdmin, setIsAdmin] = useState(false);

  useEffect(() => {
    api
      .get<UserResp>("/api/users")
      .then((user) => {
        setIsAdmin(user.role === "ADMIN");
      })
      .catch(() => {
        // 비로그인 상태 등 - admin 아님으로 처리, Navbar는 정상 노출
        setIsAdmin(false);
      });
  }, []);

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
          {isAdmin && (
            <Link href="/accommodations" className={styles.navItem}>
              숙소 등록
            </Link>
          )}
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
