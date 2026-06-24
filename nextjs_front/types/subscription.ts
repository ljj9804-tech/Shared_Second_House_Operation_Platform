export type SubscriptionStatus = "PENDING" | "ACTIVE" | "EXPIRED" | "CANCELLED";

export interface SubscriptionsUserResp {
  subscriptionId: number;
  userId: number;
  username: string;
  accommodationId: number;
  durationMonths: number;
  startDate: string; // ISO date string
  endDate: string;
  status: SubscriptionStatus;
  createdAt: string;
}

export interface SubscriptionSearchCondition {
  username?: string;
  status?: SubscriptionStatus;
  startDate?: string;
  endDate?: string;
}

export type MemberRole = "LEADER" | "MEMBER";
export type MemberStatus = "PENDING" | "APPROVED" | "REJECTED";

export interface SubscriptionsUserResp {
  subscriptionId: number;
  userId: number;
  username: string;
  accommodationId: number;
  durationMonths: number;
  startDate: string;
  endDate: string;
  status: SubscriptionStatus;
  createdAt: string;
}

export interface SubscriptionSearchCondition {
  username?: string;
  status?: SubscriptionStatus;
  startDate?: string;
  endDate?: string;
}

// 구독 신청 요청
export interface WaitingSubscriptionReq {
  accommodationId: number;
  durationMonths: number;
  memberIdentifiers: string[];
}

// 초대 응답
export interface WaitingSubscriptionResp {
  waitingId: number;
  subscriptionId: number;
  accommodationId: number;
  userId: number;
  username: string;
  memberRole: MemberRole;
  status: MemberStatus;
  requestedAt: string;
  respondedAt: string | null;
}
