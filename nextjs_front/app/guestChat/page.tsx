"use client";

import { useSearchParams } from "next/navigation";
import { useEffect, useState, useRef, Suspense } from "react";
import { Client } from "@stomp/stompjs";

interface ChatMessage {
  chatId?: number;
  chatRoomId: number;
  senderId: number;
  senderName: string;
  content?: string;
  messageContent?: string;
  sentAt?: string;
  writer?: string;
  senderNickname?: string;
  userName?: string;
}

function GuestChatContent() {
  const searchParams = useSearchParams();
  const chatRoomId = Number(searchParams.get("roomId"));

  const senderId = 100;
  const senderName = "string2";

  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [inputText, setInputText] = useState("");
  const [isLoading, setIsLoading] = useState(true);

  const stompClientRef = useRef<Client | null>(null);
  const scrollRef = useRef<HTMLDivElement | null>(null);

  // 1. 과거 메시지 내역 조회 (HTTP GET)
  useEffect(() => {
    if (!chatRoomId) return;

    const fetchChatHistory = async () => {
      try {
        const response = await fetch(
          `${process.env.NEXT_PUBLIC_SERVER_URL}/api/guest/chat/history/${chatRoomId}`,
        );
        if (response.ok) {
          const historyData = await response.json();
          setMessages(historyData);
        }
      } catch (error) {
        console.error("❌ 과거 채팅 내역 로드 실패:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchChatHistory();
  }, [chatRoomId]);

  // 2. STOMP 웹소켓 연결
  useEffect(() => {
    if (!chatRoomId) return;

    const socketUrl = process.env.NEXT_PUBLIC_WEBSOCKET_URL;
    if (!socketUrl) return;

    const client = new Client({
      brokerURL: socketUrl,
      reconnectDelay: 5000,
      debug: (str) => console.log("🔄 [STOMP 디버그]:", str),
      onConnect: () => {
        console.log(`🚀 [웹 웹소켓] 연결 성공! ${chatRoomId}번 방 구독 시작`);
        client.subscribe(`/topic/guest/room/${chatRoomId}`, (message) => {
          const incoming: ChatMessage = JSON.parse(message.body);
          setMessages((prev) => [...prev, incoming]);
        });
      },
    });

    client.activate();
    stompClientRef.current = client;

    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.deactivate();
        console.log("🔌 [웹 웹소켓] 연결 해제");
      }
    };
  }, [chatRoomId]);

  // 3. 스크롤 제어
  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [messages]);

  // 4. 발신
  const handleSendMessage = () => {
    const content = inputText.trim();
    if (!content || !chatRoomId) {
      console.log("⚠️ 텍스트가 비어있거나 방 번호가 없습니다.");
      return;
    }

    if (!stompClientRef.current?.connected) {
      console.error(
        "❌ 에러: 웹소켓이 아직 서버와 연결되지 않은 상태입니다! 발송을 차단합니다.",
      );
      return;
    }

    const chatDto = {
      chatRoomId: chatRoomId,
      senderId: senderId,
      senderName: senderName,
      content: content,
    };

    stompClientRef.current.publish({
      destination: "/app/guest/chat/send",
      body: JSON.stringify(chatDto),
    });

    setInputText("");
  };

  if (!chatRoomId) {
    return (
      <div
        className="flex h-screen items-center justify-center font-bold"
        style={{ color: "var(--color-danger)" }}
      >
        잘못된 접근입니다. 방 번호(roomId)가 없습니다.
      </div>
    );
  }

  if (isLoading) {
    return (
      <div
        className="flex h-screen items-center justify-center"
        style={{
          color: "var(--color-text-muted)",
          fontSize: "var(--font-size-base)",
        }}
      >
        채팅방 로딩 중...
      </div>
    );
  }

  return (
    <div
      className="flex flex-col h-screen mx-auto border-x shadow-lg"
      style={{
        maxWidth: "var(--max-width)", // 레이아웃 규격 맞춤 (필요시 모바일 전용 모듈은 max-w-md 고정 가능)
        backgroundColor: "var(--color-background)",
        borderColor: "var(--color-border)",
      }}
    >
      {/* 상단 헤더: 브랜드 메인 컬러 상속 및 공통 sticky 구조 연동 */}
      <header
        className="text-white p-4 text-center font-bold sticky top-0 z-50 shadow-sm"
        style={{
          backgroundColor: "var(--color-primary)",
          fontSize: "var(--font-size-lg)",
        }}
      >
        {chatRoomId}번 게스트 단체방 (Web - Query)
      </header>

      {/* 채팅 메시지 공간 */}
      <div ref={scrollRef} className="flex-1 p-4 overflow-y-auto space-y-3">
        <p
          className="text-center text-xs py-1 rounded-full w-2/3 mx-auto"
          style={{
            backgroundColor: "var(--color-primary-light)",
            color: "var(--color-primary)",
            fontSize: "var(--font-size-sm)",
          }}
        >
          📢 실시간 단체 채팅방에 입장하셨습니다.
        </p>

        {messages.map((msg, index) => {
          const isMe = msg.senderId === senderId;
          const displayContent =
            msg.content || msg.messageContent || "내용 없음";

          return (
            <div
              key={msg.chatId ?? index}
              className={`flex ${isMe ? "justify-end" : "justify-start"}`}
            >
              <div
                className={`max-w-[75%] px-4 py-2.5 shadow-sm`}
                style={{
                  fontSize: "var(--font-size-base)",
                  lineHeight: "1.6",
                  borderRadius: "var(--radius-md)",
                  borderTopRightRadius: isMe ? "0px" : "var(--radius-md)",
                  borderTopLeftRadius: isMe ? "var(--radius-md)" : "0px",
                  backgroundColor: isMe
                    ? "var(--color-primary-light)"
                    : "var(--color-card-bg)",
                  color: "var(--color-foreground)",
                  border: isMe ? "none" : "1px solid var(--color-border)",
                }}
              >
                {!isMe && (
                  <div
                    className="font-bold mb-1"
                    style={{
                      color: "var(--color-primary)",
                      fontSize: "var(--font-size-sm)",
                    }}
                  >
                    {msg.senderName ||
                      msg.writer ||
                      msg.senderNickname ||
                      msg.userName ||
                      "게스트"}
                  </div>
                )}
                <div className="break-all">{displayContent}</div>
              </div>
            </div>
          );
        })}
      </div>

      {/* 하단 입력 폼 영역 */}
      <div
        className="p-3 flex items-center gap-2 border-t"
        style={{
          backgroundColor: "var(--color-background)",
          borderColor: "var(--color-border)",
        }}
      >
        <input
          type="text"
          value={inputText}
          onChange={(e) => setInputText(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && handleSendMessage()}
          placeholder="메시지를 입력하세요..."
          className="flex-1 px-4 py-2 focus:outline-none transition-colors"
          style={{
            backgroundColor: "var(--color-card-bg)",
            border: "1px solid var(--color-border)",
            borderRadius: "var(--radius-lg)",
            fontSize: "var(--font-size-base)",
            color: "var(--color-foreground)",
          }}
        />
        {/* 발송 버튼: .btn-sm 공통 클래스 기반에 형태 유지를 위한 일부 패딩 조정 */}
        <button
          onClick={handleSendMessage}
          className="btn-sm flex items-center justify-center"
          style={{
            width: "36px",
            height: "36px",
            padding: "0",
            borderRadius: "50%" /* 원형 유지 */,
            backgroundColor: "var(--color-primary)",
          }}
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            strokeWidth={2}
            stroke="currentColor"
            className="w-4 h-4"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              d="M6 12L3.269 3.126A59.768 59.768 0 0121.485 12 59.77 59.77 0 013.27 20.876L5.999 12zm0 0h7.5"
            />
          </svg>
        </button>
      </div>
    </div>
  );
}

export default function GuestChatRoomPage() {
  return (
    <Suspense
      fallback={
        <div
          className="flex h-screen items-center justify-center"
          style={{ color: "var(--color-text-muted)" }}
        >
          페이지 준비 중...
        </div>
      }
    >
      <GuestChatContent />
    </Suspense>
  );
}
