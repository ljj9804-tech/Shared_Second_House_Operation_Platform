/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : app/accommodations/[id]/page.tsx
 * 역할  : 숙소 상세 페이지 (이미지 슬라이더 + 섹션들 + 우측 고정 계산기)
 * 사용처 : /accommodations/{id} 진입 시 렌더링
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - ImageSlider.tsx     : 이미지 슬라이더 컴포넌트
 * - PriceTable.tsx      : 장기 계약 할인 가격표
 * - LocationMap.tsx     : 주변 맛집 지도
 * - HouseStructure.tsx  : 집 구조 정보
 * - AmenityGrid.tsx     : 구성용품 아이콘 그리드
 * - StorySection.tsx    : 스토리 섹션
 * - lib/api.ts          : fetch 기반 API 클라이언트 (Bearer 토큰 자동 첨부)
 * - lib/token.ts        : tokenStorage (localStorage accessToken)
 * - types/auth.ts       : UserResp 타입 (userId 획득용)
 * - app/lib/constants.ts : MONTH_OPTIONS
 * - Spring: StayAccommodationController.java : GET /api/stay/accommodations/{id}  (permitAll)
 * - Spring: StayStoryController.java         : GET /api/stay/stories/{id}         (permitAll)
 * - Spring: SubscriptionsController.java     : GET /api/subscriptions/my/{userId} (인증 필요)
 * ----------------------------------------------------------------------------------
 * [기능 목록]
 * - 숙소 상세 + 스토리 + 구독 상태 병렬 API 조회 (Promise.all)
 * - 구독 상태(none/waiting/active/expired) 별 버튼 분기
 * - 우측 사이드바: 팀수·개월수 선택 → 팀당 월세 실시간 계산
 * - calcTeamPrice() 함수 export → AccommodationCard, SubscribePage에서 import
 * ----------------------------------------------------------------------------------
 * [파일 흐름과 순서]
 * 진입 → tokenStorage 확인
 *   [비로그인] → 숙소+스토리만 조회 → subscriptionStatus='none' (구독하러가기 버튼)
 *   [로그인]   → 숙소+스토리+userId 병렬 조회 → subscriptions 조회 → 상태 분기
 * → 팀수/개월수 변경 → calcTeamPrice() → 팀당 월세 표시
 * ----------------------------------------------------------------------------------
 * [주의사항 / 참고]
 * - lib/api.ts는 401 시 리프레시 시도 후 /login 리다이렉트 → 비로그인 판별은 tokenStorage로
 * ==================================================================================
 */

"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import styles from "./page.module.css";
import { StayAccommodationDto, StayAccommodationPriceDto } from "../page";

import { api } from "@/lib/api";
import { tokenStorage } from "@/lib/token";
import { UserResp } from "@/types/auth";
import { MONTH_OPTIONS } from "@/app/lib/constants";
import ImageSlider from "./components/ImageSlider";
import PriceTable from "./components/PriceTable";
import HouseStructure from "./components/HouseStructure";
import AmenityGrid from "./components/AmenityGrid";
import StorySection from "./components/StorySection";
import LocationMap from "./components/LocationMap";

// 스토리 타입
export interface StayStoryDto {
  id: number;
  orderNum: number;
  title: string;
  content: string;
  imageUrl: string;
}

// 구독 상태 타입
type SubscriptionStatus = "none" | "waiting" | "active" | "expired";

interface SubscriptionItemDto {
  accommodationId: number;
  status: string;
}

// 팀당 월세 계산 함수
export function calcTeamPrice(
  monthlyPrice: number,
  prices: StayAccommodationPriceDto[],
  months: number,
  teams: number,
): number {
  const priceInfo = prices.find(
    (p) =>
      months >= p.minMonths && (p.maxMonths === null || months < p.maxMonths),
  );
  if (!priceInfo) return 0;
  return Math.floor((monthlyPrice * (1 - priceInfo.discountRate)) / teams);
}

export default function AccommodationDetailPage() {
  const params = useParams();
  const router = useRouter();
  const id = params.id as string;

  const [accommodation, setAccommodation] =
    useState<StayAccommodationDto | null>(null);
  const [stories, setStories] = useState<StayStoryDto[]>([]);
  const [subscriptionStatus, setSubscriptionStatus] =
    useState<SubscriptionStatus>("none");
  const [loading, setLoading] = useState(true);
  // 서버가 인정한 유효 로그인 여부 (/api/users 성공 시 true) — LocationMap 지도 노출/과금 게이트
  const [authed, setAuthed] = useState(false);

  // 계산기 상태
  const [teams, setTeams] = useState(1);
  const [months, setMonths] = useState(1);

  useEffect(() => {
    if (!id) return;

    const token = tokenStorage.get();

    if (token) {
      // 로그인 상태: 숙소 + 스토리 + userId 병렬 조회 후 구독 상태 확인
      Promise.all([
        api.get<StayAccommodationDto>(`/api/stay/accommodations/${id}`),
        api.get<StayStoryDto[]>(`/api/stay/stories/${id}`),
        api.get<UserResp>("/api/users"),
      ])
        .then(([accommodationData, storiesData, userData]) => {
          setAccommodation(accommodationData);
          setStories(storiesData);
          setAuthed(true);
          return api.get<SubscriptionItemDto[]>(
            `/api/subscriptions/my/${userData.userId}`,
          );
        })
        .then((subscriptionData) => {
          const matched = Array.isArray(subscriptionData)
            ? subscriptionData.find((s) => s.accommodationId === Number(id))
            : null;

          if (!matched) setSubscriptionStatus("none");
          else if (matched.status === "PENDING")
            setSubscriptionStatus("waiting");
          else if (matched.status === "ACTIVE") setSubscriptionStatus("active");
          else if (matched.status === "EXPIRED")
            setSubscriptionStatus("expired");
          else setSubscriptionStatus("none");
        })
        .catch((err) => {
          console.log("상세 페이지 데이터 조회 실패:", err);
        })
        .finally(() => setLoading(false));
    } else {
      // 비로그인 상태: 공개 데이터만 조회 (구독 상태는 none)
      Promise.all([
        api.get<StayAccommodationDto>(`/api/stay/accommodations/${id}`),
        api.get<StayStoryDto[]>(`/api/stay/stories/${id}`),
      ])
        .then(([accommodationData, storiesData]) => {
          setAccommodation(accommodationData);
          setStories(storiesData);
          setSubscriptionStatus("none");
        })
        .catch((err) => {
          console.log("상세 페이지 데이터 조회 실패:", err);
        })
        .finally(() => setLoading(false));
    }
  }, [id]);

  if (loading) return <div className={styles.loading}>불러오는 중...</div>;
  if (!accommodation)
    return <div className={styles.loading}>숙소를 찾을 수 없습니다.</div>;

  // 팀당 월세
  const teamPrice = calcTeamPrice(
    accommodation.monthlyPrice,
    accommodation.prices ?? [],
    months,
    teams,
  );

  return (
    <div className={styles.container}>
      {/* 이미지 슬라이더 (전체 너비) */}
      <ImageSlider
        imageUrl={accommodation.imageUrl}
        name={accommodation.name}
      />

      {/* 본문 + 우측 고정 계산기 */}
      <div className={styles.body}>
        {/* 좌측 본문 */}
        <div className={styles.content}>
          <h1 className={styles.title}>{accommodation.name}</h1>
          <p className={styles.description}>{accommodation.description}</p>

          {/* 섹션1: 장기 계약 할인 가격표 */}
          <PriceTable
            monthlyPrice={accommodation.monthlyPrice}
            prices={accommodation.prices ?? []}
          />

          {/* 섹션2: 위치 및 주변 시설 */}
          <LocationMap
            accommodationId={accommodation.id}
            latitude={accommodation.latitude}
            longitude={accommodation.longitude}
            address={accommodation.address}
            showMap={authed}
          />

          {/* 섹션3: 집 구조 */}
          <HouseStructure
            roomCount={accommodation.roomCount}
            bathroomCount={accommodation.bathroomCount}
            floorCount={accommodation.floorCount}
            parkingCount={accommodation.parkingCount}
            landArea={accommodation.landArea}
            buildingArea={accommodation.buildingArea}
          />

          {/* 섹션4: 구성용품 */}
          <AmenityGrid amenities={accommodation.amenities} />

          {/* 섹션5: 웰컴키트 */}
          {/* <WelcomeKit /> */}

          {/* 섹션6: 스토리 */}
          <StorySection stories={stories} />
        </div>

        {/* 우측 고정 계산기 */}
        <aside className={styles.sidebar}>
          <div className={styles.sidebarInner}>
            <h2 className={styles.sidebarTitle}>
              월세 자동 계산기{/* {accommodation.name} */}
            </h2>

            {/* 팀수 선택 */}
            <div className={styles.selectGroup}>
              <label className={styles.selectLabel}>같이 사용할 팀 수</label>
              <select
                className={styles.select}
                value={teams}
                onChange={(e) => setTeams(Number(e.target.value))}
              >
                {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12].map((n) => (
                  <option key={n} value={n}>
                    {n} 팀
                  </option>
                ))}
              </select>
            </div>

            {/* 개월수 선택 */}
            <div className={styles.selectGroup}>
              <label className={styles.selectLabel}>계약 월 수</label>
              <select
                className={styles.select}
                value={months}
                onChange={(e) => setMonths(Number(e.target.value))}
              >
                {MONTH_OPTIONS.map((opt) => (
                  <option key={opt.value} value={opt.value}>
                    {opt.label}
                  </option>
                ))}
              </select>
            </div>

            {/* 팀당 월세 */}
            <div className={styles.priceWrap}>
              <span className={styles.priceLabel}>팀당 월세</span>
              <span className={styles.price}>
                {teamPrice > 0 ? `${teamPrice.toLocaleString()}원` : "-"}
              </span>
              <span className={styles.priceUnit}>/ 개월</span>
            </div>

            {/* 버튼 분기 */}
            {subscriptionStatus === "none" && (
              <button
                className="btn-primary"
                onClick={() => router.push(`/subscribe/${id}`)}
              >
                구독하러가기
              </button>
            )}
            {subscriptionStatus === "waiting" && (
              <button className="btn-disabled" disabled>
                승인 대기 중
              </button>
            )}
            {subscriptionStatus === "active" && (
              <button
                className="btn-primary"
                onClick={() => router.push(`/reservations/${id}`)}
              >
                예약하기
              </button>
            )}
            {subscriptionStatus === "expired" && (
              <button
                className="btn-primary"
                onClick={() => router.push(`/subscribe/${id}`)}
              >
                재구독하기
              </button>
            )}
          </div>
        </aside>
      </div>
    </div>
  );
}
