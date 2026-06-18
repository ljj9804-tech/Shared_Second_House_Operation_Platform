'use client';

import { useState } from 'react';
import styles from './FloatingChatbot.module.css';
import ChatWindow from './ChatWindow';

/// 🟦 모든 페이지 우하단에 고정되는 플로팅 챗봇 위젯.
/// 버튼을 누르면 페이지 이동 없이 그 자리에서 챗봇창이 열린다.
export default function FloatingChatbot() {
  const [open, setOpen] = useState(false);

  return (
    <>
      {open && (
        <div className={styles.panel}>
          <ChatWindow onClose={() => setOpen(false)} />
        </div>
      )}

      <button
        type="button"
        className={styles.fab}
        onClick={() => setOpen((v) => !v)}
        aria-label={open ? '챗봇 닫기' : '챗봇 열기'}
      >
        {open ? '✕' : '💬'}
      </button>
    </>
  );
}
