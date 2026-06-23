"use client";

import { Suspense, useState, useEffect, useCallback } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { api } from "@/lib/api";
import { tokenStorage } from "@/lib/token";
import { UserResp } from "@/types/auth";
import { WaitingSubscriptionResp, MemberStatus } from "@/types/subscription";

const STATUS_LABEL: Record<MemberStatus, string> = {
  PENDING: "응답 대기",
  APPROVED: "동의 완료",
  REJECTED: "거절함",
};

const STATUS_BADGE: Record<MemberStatus, string> = {
  PENDING: "bg-amber-50 text-amber-700",
  APPROVED: "bg-[#EAF3DE] text-[#3B6D11]",
  REJECTED: "bg-red-50 text-red-600",
};

function InvitationsContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const justSent = searchParams.get("sent") === "true";

  const [user, setUser] = useState<UserResp | null>(null);
  const [invitations, setInvitations] = useState<WaitingSubscriptionResp[]>([]);
  const [loading, setLoading] = useState(true);
  const [processingId, setProcessingId] = useState<number | null>(null);

  // 👍 수정 포인트 1: fetchInvitations가 굳이 또 setLoading(true)을 강제해서
  // 동기적 연쇄 렌더링을 유발하지 않도록, 필요한 상황에서만 loading 상태를 켜도록 정리합니다.
  const fetchInvitations = useCallback(
    async (userId: number, showLoading = false) => {
      if (showLoading) setLoading(true);
      try {
        const data = await api.get<WaitingSubscriptionResp[]>(
          `/api/waiting/my/${userId}`,
        );
        setInvitations(
          [...data].sort(
            (a, b) =>
              new Date(b.requestedAt).getTime() -
              new Date(a.requestedAt).getTime(),
          ),
        );
      } finally {
        setLoading(false);
      }
    },
    [],
  );

  // 👍 수정 포인트 2: 문제의 주범이었던 useEffect([user, fetchInvitations]) 블록을 삭제하고,
  // 유저 정보 수신 성공(.then) 시점에 연쇄적으로 초대를 가져오도록 이벤트를 이어 붙입니다.
  useEffect(() => {
    if (!tokenStorage.get()) {
      router.replace("/login");
      return;
    }
    api
      .get<UserResp>("/api/users")
      .then((userData) => {
        setUser(userData);
        // 유저 정보 받아오기 성공 시점의 흐름을 타서 안전하게 가져옵니다.
        fetchInvitations(userData.userId);
      })
      .catch(() => router.replace("/login"));
  }, [router, fetchInvitations]);

  const handleApprove = async (waiting: WaitingSubscriptionResp) => {
    if (!user) return;
    setProcessingId(waiting.waitingId);
    try {
      await api.post(
        `/api/waiting/${waiting.subscriptionId}/approve/${user.userId}`,
        {},
      );
      // 승인/거절 후 새로고침할 때는 이미 user가 있으므로 showLoading을 활성화해도 안전합니다.
      await fetchInvitations(user.userId, true);
    } finally {
      setProcessingId(null);
    }
  };

  const handleReject = async (waiting: WaitingSubscriptionResp) => {
    if (!user) return;
    if (!confirm("정말 거절하시겠어요? 이 작업은 되돌릴 수 없어요.")) return;

    setProcessingId(waiting.waitingId);
    try {
      await api.post(
        `/api/waiting/${waiting.subscriptionId}/reject/${user.userId}`,
        {},
      );
      await fetchInvitations(user.userId, true);
    } finally {
      setProcessingId(null);
    }
  };

  if (!user || loading) {
    return (
      <div className="w-full h-screen flex items-center justify-center bg-[#F7F4EF]">
        <div className="w-8 h-8 border-2 border-[#3B6D11] border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#F7F4EF] py-10 px-4">
      <div className="max-w-xl mx-auto">
        <h1 className="text-lg font-medium text-[#2A2520] mb-1">
          내 초대 목록
        </h1>
        <p className="text-sm text-[#8C8178] mb-5">
          함께 쓸 숙소 구독에 초대받았어요
        </p>

        {justSent && (
          <div className="bg-[#EAF3DE] text-[#3B6D11] text-sm rounded-lg px-4 py-3 mb-5">
            초대를 보냈어요! 멤버들이 동의하면 관리자 승인 절차로 넘어가요.
          </div>
        )}

        {invitations.length === 0 ? (
          <div className="text-center py-16 text-[#B5ADA4] text-sm bg-white border border-[#E4DDD3] rounded-xl">
            아직 받은 초대가 없어요.
          </div>
        ) : (
          <div className="flex flex-col gap-3">
            {invitations.map((inv) => (
              <div
                key={inv.waitingId}
                className="bg-white border border-[#E4DDD3] rounded-xl p-5"
              >
                {/* 헤더 */}
                <div className="flex items-center gap-2.5 mb-3">
                  <div className="w-11 h-11 rounded-lg bg-gradient-to-br from-[#3B6D11] to-[#6AAF3A] flex-shrink-0" />
                  <div>
                    <p className="text-sm font-medium text-[#2A2520]">
                      숙소 ID: {inv.accommodationId}
                    </p>
                    <p className="text-xs text-[#8C8178] mt-0.5">
                      신청자: {inv.username}
                    </p>
                  </div>
                  <span
                    className={`ml-auto text-xs px-2.5 py-1 rounded-full font-medium ${STATUS_BADGE[inv.status]}`}
                  >
                    {STATUS_LABEL[inv.status]}
                  </span>
                </div>

                {/* 메타 정보 */}
                <div className="flex gap-4 py-2.5 border-y border-[#F0EBE5] mb-3 text-xs text-[#8C8178]">
                  <span>
                    초대일{" "}
                    <b className="text-[#2A2520] font-medium">
                      {new Date(inv.requestedAt).toLocaleDateString("ko-KR")}
                    </b>
                  </span>
                  {inv.respondedAt && (
                    <span>
                      응답일{" "}
                      <b className="text-[#2A2520] font-medium">
                        {new Date(inv.respondedAt).toLocaleDateString("ko-KR")}
                      </b>
                    </span>
                  )}
                  <span>
                    내 역할{" "}
                    <b className="text-[#2A2520] font-medium">
                      {inv.memberRole === "LEADER" ? "대표" : "멤버"}
                    </b>
                  </span>
                </div>

                {/* 액션 — PENDING일 때만 */}
                {inv.status === "PENDING" && (
                  <div className="flex gap-2">
                    <button
                      onClick={() => handleApprove(inv)}
                      disabled={processingId === inv.waitingId}
                      className="flex-1 h-9 bg-[#3B6D11] text-white text-sm font-medium rounded-lg hover:bg-[#2D5509] disabled:opacity-60"
                    >
                      {processingId === inv.waitingId
                        ? "처리 중..."
                        : "동의하기"}
                    </button>
                    <button
                      onClick={() => handleReject(inv)}
                      disabled={processingId === inv.waitingId}
                      className="flex-1 h-9 bg-white text-red-600 border border-red-200 text-sm font-medium rounded-lg hover:bg-red-50 disabled:opacity-60"
                    >
                      거절하기
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

export default function InvitationsPage() {
  return (
    <Suspense>
      <InvitationsContent />
    </Suspense>
  );
}
