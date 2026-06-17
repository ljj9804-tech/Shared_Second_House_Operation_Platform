'use client';

import { useEffect, useRef, useState } from 'react';
import styles from './page.module.css';

/// 📚 답변의 근거가 된 FAQ 1건 (백엔드 SearchHit 매칭)
interface ChatBotSource {
  id: string;
  score: number;
  question: string;
  answer: string;
}

/// 🤖 챗봇 RAG 답변 응답 (GET /api/chatBot/chat 의 AnswerResult 규격)
interface ChatBotAnswer {
  query: string;
  answer: string;
  sources: ChatBotSource[];
}

/// 💬 화면에 뿌릴 챗봇 메시지 1건 (사용자 질문 / 봇 답변 공용)
interface ChatMessage {
  text: string;
  isMe: boolean; // true=내 질문, false=봇 답변
  sources: ChatBotSource[]; // 봇 답변의 근거 FAQ
}

const WELCOME: ChatMessage = {
  text: '안녕하세요! 세컨하우스 운영 FAQ 챗봇입니다. 궁금한 점을 물어보세요. 🙂',
  isMe: false,
  sources: [],
};

// 대화 내역을 세션 저장소에 보관 → 화면을 나갔다 다시 들어와도 대화가 유지된다.
const STORAGE_KEY = 'chatbot_messages';

export default function ChatBotPage() {
  // 세션에 저장된 대화 복원 (최초 1회). 없으면 환영 메시지로 시작.
  const [messages, setMessages] = useState<ChatMessage[]>(() => {
    if (typeof window === 'undefined') return [WELCOME];
    const saved = window.sessionStorage.getItem(STORAGE_KEY);
    if (!saved) return [WELCOME];
    try {
      return JSON.parse(saved) as ChatMessage[];
    } catch {
      return [WELCOME];
    }
  });
  const [input, setInput] = useState('');
  const [isSending, setIsSending] = useState(false);

  const listRef = useRef<HTMLDivElement>(null);

  // 대화가 바뀔 때마다 세션 저장 + 맨 아래로 스크롤
  useEffect(() => {
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(messages));
    listRef.current?.scrollTo({
      top: listRef.current.scrollHeight,
      behavior: 'smooth',
    });
  }, [messages, isSending]);

  // 🚀 질문 전송 → 백엔드 RAG 답변 수신
  const handleSend = async () => {
    const q = input.trim();
    if (!q || isSending) return;

    setInput('');
    setMessages((prev) => [...prev, { text: q, isMe: true, sources: [] }]);
    setIsSending(true);

    try {
      const res = await fetch(
        `${process.env.NEXT_PUBLIC_SERVER_URL}/api/chatBot/chat?q=${encodeURIComponent(
          q
        )}&topK=3`
      );
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = (await res.json()) as ChatBotAnswer;
      setMessages((prev) => [
        ...prev,
        {
          text: data.answer ?? '답변을 가져오지 못했습니다.',
          isMe: false,
          sources: data.sources ?? [],
        },
      ]);
    } catch (err) {
      console.log('🔴 [챗봇] 통신 실패:', err);
      setMessages((prev) => [
        ...prev,
        {
          text: '답변을 가져오지 못했어요. 잠시 후 다시 시도해 주세요. 😢',
          isMe: false,
          sources: [],
        },
      ]);
    } finally {
      setIsSending(false);
    }
  };

  return (
    <div className={styles.screen}>
      {/* 상단 헤더 */}
      <header className={styles.appBar}>AI 챗봇</header>

      {/* 💬 메시지 리스트 */}
      <div className={styles.list} ref={listRef}>
        {messages.map((msg, i) => (
          <MessageBubble key={i} msg={msg} />
        ))}
        {isSending && <TypingBubble />}
      </div>

      {/* ⌨️ 하단 입력창 */}
      <div className={styles.inputBar}>
        <input
          className={styles.input}
          value={input}
          disabled={isSending}
          placeholder="궁금한 점을 입력하세요..."
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === 'Enter') handleSend();
          }}
        />
        <button
          className={styles.sendBtn}
          disabled={isSending || !input.trim()}
          onClick={handleSend}
          aria-label="전송"
        >
          ➤
        </button>
      </div>
    </div>
  );
}

/// 💬 메시지 버블 1개 (질문/답변 공용)
function MessageBubble({ msg }: { msg: ChatMessage }) {
  const { isMe } = msg;
  return (
    <div className={`${styles.row} ${isMe ? styles.rowMe : styles.rowBot}`}>
      {!isMe && <div className={styles.avatar}>🤖</div>}
      <div className={styles.bubbleColumn}>
        <div className={`${styles.bubble} ${isMe ? styles.bubbleMe : styles.bubbleBot}`}>
          {msg.text}
        </div>
        {/* 📚 봇 답변의 근거 FAQ 칩 (있을 때만) */}
        {!isMe && msg.sources.length > 0 && (
          <div className={styles.sources}>
            <span className={styles.sourcesTitle}>📚 참고한 FAQ</span>
            <div className={styles.chips}>
              {msg.sources.map((s) => (
                <span key={s.id} className={styles.chip}>
                  {s.question}
                </span>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

/// ⏳ 답변 작성 중 로딩 버블
function TypingBubble() {
  return (
    <div className={`${styles.row} ${styles.rowBot}`}>
      <div className={styles.avatar}>🤖</div>
      <div className={`${styles.bubble} ${styles.bubbleBot}`}>
        <span className={styles.spinner} />
      </div>
    </div>
  );
}
