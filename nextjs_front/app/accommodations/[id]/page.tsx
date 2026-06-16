'use client';

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import styles from './page.module.css';
import { StayAccommodationDto, StayAccommodationPriceDto } from '../page';

import ImageSlider from './components/ImageSlider';
import PriceTable from './components/PriceTable';
import LocationMap from './components/LocationMap';
import HouseStructure from './components/HouseStructure';
import AmenityGrid from './components/AmenityGrid';
import WelcomeKit from './components/WelcomeKit';
import StorySection from './components/StorySection';

// 스토리 타입
export interface StayStoryDto {
  id: number;
  orderNum: number;
  title: string;
  content: string;
  imageUrl: string;
}

// 구독 상태 타입
type SubscriptionStatus = 'none' | 'waiting' | 'active' | 'expired';

// 팀당 월세 계산 함수
export function calcTeamPrice(
  monthlyPrice: number,
  prices: StayAccommodationPriceDto[],
  months: number,
  teams: number
): number {
  const priceInfo = prices.find(
    (p) =>
      months >= p.minMonths && (p.maxMonths === null || months < p.maxMonths)
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
    useState<SubscriptionStatus>('none');
  const [loading, setLoading] = useState(true);

  // 계산기 상태
  const [teams, setTeams] = useState(1);
  const [months, setMonths] = useState(1);

  // TODO [인증]: userId 하드코딩 → JWT 토큰에서 실제 userId 추출로 교체
  const userId = 1;

  useEffect(() => {
    if (!id) return;

    // 병렬 API 호출
    Promise.all([
      fetch(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/stay/accommodations/${id}`
      ).then((r) => r.json()),
      fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/stay/stories/${id}`).then(
        (r) => r.json()
      ),
      fetch(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/subscriptions/my/${userId}`
      ).then((r) => r.json()),
    ])
      .then(([accommodationData, storiesData, subscriptionData]) => {
        console.log('숙소 데이터:', accommodationData);
        console.log('스토리 데이터:', storiesData);
        console.log('구독 데이터:', subscriptionData);

        setAccommodation(accommodationData);
        setStories(storiesData);

        // 구독 상태 파싱
        // TODO [인증]: 실제 구독 API 응답 구조에 맞게 수정
        if (
          !subscriptionData ||
          !Array.isArray(subscriptionData) ||
          subscriptionData.length === 0
        ) {
          setSubscriptionStatus('none');
        } else {
          const latest = subscriptionData[0];
          if (latest.status === 'PENDING') {
            setSubscriptionStatus('waiting');
          } else if (latest.status === 'ACTIVE') {
            setSubscriptionStatus('active');
          } else if (latest.status === 'EXPIRED') {
            setSubscriptionStatus('expired');
          } else if (latest.status === 'CANCELLED') {
            setSubscriptionStatus('none');
          } else {
            setSubscriptionStatus('none');
          }
        }
      })
      .catch((err) => {
        console.log('상세 페이지 데이터 조회 실패:', err);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [id]);

  if (loading) return <div className={styles.loading}>불러오는 중...</div>;
  if (!accommodation)
    return <div className={styles.loading}>숙소를 찾을 수 없습니다.</div>;

  // 팀당 월세
  const teamPrice = calcTeamPrice(
    accommodation.monthlyPrice,
    accommodation.prices ?? [],
    months,
    teams
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
            address={accommodation.address}
            latitude={accommodation.latitude}
            longitude={accommodation.longitude}
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
            <h2 className={styles.sidebarTitle}>{accommodation.name}</h2>

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
                {Array.from({ length: 12 }, (_, i) => i + 1).map((n) => (
                  <option key={n} value={n}>
                    {n} 개월
                  </option>
                ))}
              </select>
            </div>

            {/* 팀당 월세 */}
            <div className={styles.priceWrap}>
              <span className={styles.priceLabel}>팀당 월세</span>
              <span className={styles.price}>
                {teamPrice > 0 ? `${teamPrice.toLocaleString()}원` : '-'}
              </span>
              <span className={styles.priceUnit}>/ 개월</span>
            </div>

            {/* 버튼 분기 */}
            {subscriptionStatus === 'none' && (
              <button
                className="btn-primary"
                onClick={() => router.push(`/subscribe/${id}`)}
              >
                구독하러가기
              </button>
            )}
            {subscriptionStatus === 'waiting' && (
              <button className="btn-disabled" disabled>
                승인 대기 중
              </button>
            )}
            {subscriptionStatus === 'active' && (
              <button
                className="btn-primary"
                onClick={() => router.push(`/reservations/${id}`)}
              >
                예약하기
              </button>
            )}
            {subscriptionStatus === 'expired' && (
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
