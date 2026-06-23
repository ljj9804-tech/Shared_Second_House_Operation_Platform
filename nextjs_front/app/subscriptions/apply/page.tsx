"use client";

import { Suspense, useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { api } from "@/lib/api";
import { tokenStorage } from "@/lib/token";
import { UserResp } from "@/types/auth";
import {
  WaitingSubscriptionReq,
  WaitingSubscriptionResp,
} from "@/types/subscription";

const DURATION_OPTIONS = [3, 6, 12];

function ApplyContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const accommodationId = searchParams.get("accommodationId");

  const [user, setUser] = useState<UserResp | null>(null);
  const [duration, setDuration] = useState(6);
  const [memberInput, setMemberInput] = useState("");
  const [members, setMembers] = useState<string[]>([]);

  // 💡 버튼 클릭이나 입력 오류 같은 '동적인 에러' 전용 상태로 남겨둡니다.
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  // 로그인 확인 + 유저 정보
  useEffect(() => {
    if (!tokenStorage.get()) {
      router.replace("/login");
      return;
    }
    api
      .get<UserResp>("/api/users")
      .then(setUser)
      .catch(() => router.replace("/login"));
  }, [router]);

  // 👍 수정 포인트 1: 잘못된 접근(accommodationId 없음)을 컴포넌트 최상단에서 바로 걸러냅니다.
  // useEffect와 setError를 거치지 않으므로 연쇄 렌더링 에러가 원천 차단됩니다.
  if (!accommodationId) {
    return (
      <div className="min-h-screen bg-[#F7F4EF] flex items-center justify-center p-4">
        <div className="bg-white border border-[#E4DDD3] rounded-xl p-6 max-w-md text-center shadow-sm">
          <p className="text-red-600 font-medium mb-2">접근 오류</p>
          <p className="text-sm text-[#8C8178]">
            숙소 정보가 없어요. 숙소 상세 페이지에서 다시 시도해 주세요.
          </p>
        </div>
      </div>
    );
  }

  // 로딩 화면은 필수 검증(accommodationId)이 끝난 후에 보여줍니다.
  if (!user) {
    return (
      <div className="w-full h-screen flex items-center justify-center bg-[#F7F4EF]">
        <div className="w-8 h-8 border-2 border-[#3B6D11] border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  const handleAddMember = () => {
    const value = memberInput.trim();
    if (!value) return;
    if (members.includes(value)) {
      setError("이미 추가된 멤버예요.");
      return;
    }
    setMembers((prev) => [...prev, value]);
    setMemberInput("");
    setError("");
  };

  const handleRemoveMember = (target: string) => {
    setMembers((prev) => prev.filter((m) => m !== target));
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      e.preventDefault();
      handleAddMember();
    }
  };

  const handleSubmit = async () => {
    // 위에서 이미 체크했으므로 여기선 무조건 존재하지만, 타입 안정성을 위해 유지합니다.
    if (!accommodationId || !user) return;

    setError("");
    setLoading(true);

    try {
      const payload: WaitingSubscriptionReq = {
        accommodationId: Number(accommodationId),
        durationMonths: duration,
        memberIdentifiers: members,
      };

      await api.post<WaitingSubscriptionResp[]>(
        `/api/waiting/apply/${user.userId}`,
        payload,
      );

      router.push("/subscriptions/invitations?sent=true");
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "신청에 실패했어요.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#F7F4EF] py-10 px-4">
      <div className="max-w-xl mx-auto">
        <div className="bg-white border border-[#E4DDD3] rounded-xl p-8">
          <h1 className="text-lg font-medium text-[#2A2520] mb-1">
            구독 신청하기
          </h1>
          <p className="text-sm text-[#8C8178] mb-7">
            함께 쓸 멤버를 초대하고 구독을 시작해요
          </p>

          {/* 숙소 정보 */}
          <div className="flex items-center gap-3 bg-[#F7F4EF] rounded-lg p-3.5 mb-6">
            <div className="w-14 h-14 rounded-lg bg-gradient-to-br from-[#3B6D11] to-[#6AAF3A] flex-shrink-0" />
            <div>
              <p className="text-sm font-medium text-[#2A2520]">
                숙소 ID: {accommodationId}
              </p>
              <p className="text-xs text-[#8C8178] mt-0.5">
                숙소 상세 정보 연동 예정
              </p>
            </div>
          </div>

          {/* 구독 기간 */}
          <div className="mb-6">
            <label className="block text-sm font-medium text-[#2A2520] mb-2">
              구독 기간
            </label>
            <div className="flex gap-2">
              {DURATION_OPTIONS.map((m) => (
                <button
                  key={m}
                  type="button"
                  onClick={() => setDuration(m)}
                  className={`flex-1 h-10 rounded-lg text-sm font-medium transition-colors ${
                    duration === m
                      ? "bg-[#3B6D11] text-white"
                      : "bg-white border border-[#D6E4C8] text-[#8C8178]"
                  }`}
                >
                  {m}개월
                </button>
              ))}
            </div>
          </div>

          {/* 멤버 초대 */}
          <div className="mb-6">
            <label className="block text-sm font-medium text-[#2A2520] mb-2">
              함께 쓸 멤버 초대
            </label>
            <div className="flex gap-2 mb-2">
              <input
                value={memberInput}
                onChange={(e) => setMemberInput(e.target.value)}
                onKeyDown={handleKeyDown}
                placeholder="아이디 또는 이메일 입력"
                className="flex-1 h-10 px-3 text-sm border border-[#D6E4C8] rounded-lg outline-none focus:border-[#3B6D11]"
              />
              <button
                type="button"
                onClick={handleAddMember}
                className="w-10 h-10 flex items-center justify-center bg-[#3B6D11] text-white rounded-lg hover:bg-[#2D5509]"
                aria-label="멤버 추가"
              >
                +
              </button>
            </div>

            {members.length > 0 && (
              <div className="flex flex-wrap gap-1.5 mb-2">
                {members.map((m) => (
                  <span
                    key={m}
                    className="inline-flex items-center gap-1.5 bg-[#EAF3DE] text-[#3B6D11] text-xs px-2.5 py-1.5 rounded-full"
                  >
                    {m}
                    <button
                      type="button"
                      onClick={() => handleRemoveMember(m)}
                      className="hover:text-red-500"
                      aria-label={`${m} 제거`}
                    >
                      ×
                    </button>
                  </span>
                ))}
              </div>
            )}

            <p className="text-xs text-[#8C8178] bg-[#F7F4EF] rounded-lg px-3 py-2.5 leading-relaxed">
              💡 초대된 멤버 전원이 동의해야 관리자에게 신청이 전달돼요.
              본인(대표자)은 자동으로 결제자로 등록돼요.
            </p>
          </div>

          {/* 요약 */}
          <div className="bg-[#EAF3DE] rounded-lg p-4 mb-6">
            <div className="flex justify-between text-sm text-[#5F7A4A] mb-1">
              <span>구독 기간</span>
              <span>{duration}개월</span>
            </div>
            <div className="flex justify-between text-sm font-medium text-[#3B6D11]">
              <span>총 인원</span>
              <span>
                {members.length + 1}명 (대표 1명 + 멤버 {members.length}명)
              </span>
            </div>
          </div>

          {error && <p className="text-xs text-red-500 mb-4">{error}</p>}

          <button
            onClick={handleSubmit}
            disabled={loading}
            className="w-full h-12 bg-[#3B6D11] hover:bg-[#2D5509] text-white text-sm font-medium rounded-lg transition-colors disabled:opacity-50"
          >
            {loading ? "신청 중..." : "멤버에게 초대 보내기"}
          </button>
        </div>
      </div>
    </div>
  );
}

export default function SubscriptionApplyPage() {
  return (
    <Suspense>
      <ApplyContent />
    </Suspense>
  );
}
