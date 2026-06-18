"use client";

import { useSearchParams } from "next/navigation";
import { useEffect, useState, useRef, Suspense } from "react"; // 🟩 Suspense 추가
import { Client } from "@stomp/stompjs";

interface ChatMessage {
  chatId?: number;
  chatRoomId: number;
  senderId: number;
  senderName: string;
  content: string;
  sentAt?: string;
  messageContent?: string;
  writer?: string;
  senderNickname?: string;
  userName?: string;
}

// 🟩 [해결 핵심] 실제 채팅방 본체 컴포넌트
function GuestChatContent() {
  const searchParams = useSearchParams();
  const chatRoomId = Number(searchParams.get("roomId"));

  const currentUserId = 100;
  const currentUserName = "넥스트게스트";

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
    const text = inputText.trim();
    if (!text || !chatRoomId) {
      console.log("⚠️ 텍스트가 비어있거나 방 번호가 없습니다.");
      return;
    }

    // 🟩 현재 웹소켓 연결 상태를 콘솔에 찍어봅니다.
    console.log("🔗 현재 STOMP 클라이언트 객체:", stompClientRef.current);
    console.log(
      "🟢 현재 웹소켓 연결 상태(connected):",
      stompClientRef.current?.connected,
    );

    if (!stompClientRef.current?.connected) {
      console.error(
        "❌ 에러: 웹소켓이 아직 서버와 연결되지 않은 상태입니다! 발송을 차단합니다.",
      );
      return; // ⚠️ 여기서 튕겨 나가고 있었을 확률이 99%입니다.
    }

    const chatDto = {
      type: "TALK",
      chatRoomId: chatRoomId,
      senderId: currentUserId,
      senderName: currentUserName,
      content: text,
    };

    stompClientRef.current.publish({
      destination: "/app/guest/chat/send",
      body: JSON.stringify(chatDto),
    });

    setInputText("");
  };

  if (!chatRoomId) {
    return (
      <div className="flex h-screen items-center justify-center text-red-500 font-bold">
        잘못된 접근입니다. 방 번호(roomId)가 없습니다.
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="flex h-screen items-center justify-center text-gray-500">
        채팅방 로딩 중...
      </div>
    );
  }

  return (
    <div className="flex flex-col h-screen bg-slate-50 max-w-md mx-auto border-x border-gray-200 shadow-lg">
      <header className="bg-[#23399D] text-white p-4 text-center font-bold text-lg sticky top-0 shadow-sm">
        {chatRoomId}번 게스트 단체방 (Web - Query)
      </header>

      <div ref={scrollRef} className="flex-1 p-4 overflow-y-auto space-y-3">
        <p className="text-center text-xs text-gray-400 bg-gray-100 py-1 rounded-full w-2/3 mx-auto">
          📢 실시간 단체 채팅방에 입장하셨습니다.
        </p>

        {messages.map((msg, index) => {
          const isMe = msg.senderId === currentUserId;
          const displayContent =
            msg.content || msg.messageContent || "내용 없음";

          return (
            <div
              key={msg.chatId ?? index}
              className={`flex ${isMe ? "justify-end" : "justify-start"}`}
            >
              <div
                className={`max-w-[75%] rounded-2xl px-4 py-2.5 text-sm shadow-sm ${
                  isMe
                    ? "bg-[#D6E4FF] text-gray-800 rounded-tr-none"
                    : "bg-white text-gray-800 rounded-tl-none border border-gray-100"
                }`}
              >
                {!isMe && (
                  <div className="text-xs font-bold text-[#23399D] mb-1">
                    {/* 🟩 any 없이 깔끔하고 안전하게 이름 필드 검사 */}
                    {msg.senderName ||
                      msg.writer ||
                      msg.senderNickname ||
                      msg.userName ||
                      "게스트"}
                  </div>
                )}
                <div className="break-all leading-relaxed">
                  {displayContent}
                </div>
              </div>
            </div>
          );
        })}
      </div>

      <div className="p-3 bg-white border-t border-gray-200 flex items-center gap-2">
        <input
          type="text"
          value={inputText}
          onChange={(e) => setInputText(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && handleSendMessage()}
          placeholder="메시지를 입력하세요..."
          className="flex-1 bg-gray-50 border border-gray-300 rounded-full px-4 py-2 text-sm focus:outline-none focus:border-[#23399D]"
        />
        <button
          onClick={handleSendMessage}
          className="bg-[#23399D] text-white p-2 rounded-full"
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

// 🟩 Next.js가 최초에 낚아채는 대문 대장 함수 (Default Export)
// useSearchParams를 안전하게 감싸기 위해 Suspense로 본체를 보조합니다.
export default function GuestChatRoomPage() {
  return (
    <Suspense
      fallback={
        <div className="flex h-screen items-center justify-center text-gray-400">
          페이지 준비 중...
        </div>
      }
    >
      <GuestChatContent />
    </Suspense>
  );
}
