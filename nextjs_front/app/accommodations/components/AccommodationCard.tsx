'use client';

import { useRouter } from 'next/navigation';
import styles from './AccommodationCard.module.css';
import { StayAccommodationDto, StayAccommodationPriceDto } from '../page';

interface AccommodationCardProps {
  accommodation: StayAccommodationDto;
  teams: number;
  months: number;
}

// 팀당 월세 계산 함수
function calcTeamPrice(
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

export default function AccommodationCard({
  accommodation,
  teams,
  months,
}: AccommodationCardProps) {
  const router = useRouter();

  // 대표 이미지 (첫 번째 이미지)
  const firstImage = accommodation.imageUrl
    ? `${process.env.NEXT_PUBLIC_BASE_URL}${accommodation.imageUrl.split(',')[0].trim()}`
    : null;

  // 팀당 월세 계산
  const teamPrice = calcTeamPrice(
    accommodation.monthlyPrice,
    accommodation.prices ?? [],
    months,
    teams
  );

  return (
    <div
      className={styles.card}
      onClick={() => router.push(`/accommodations/${accommodation.id}`)}
    >
      {/* 숙소 이미지 */}
      <div className={styles.imageWrap}>
        {firstImage ? (
          <img
            src={firstImage}
            alt={accommodation.name}
            className={styles.image}
          />
        ) : (
          <div className={styles.imagePlaceholder}>이미지 없음</div>
        )}
        {/* 상태 배지 */}
        {accommodation.status === 'MAINTENANCE' && (
          <span className={styles.badge}>점검 중</span>
        )}
      </div>

      {/* 숙소 정보 */}
      <div className={styles.info}>
        <h3 className={styles.name}>{accommodation.name}</h3>
        <p className={styles.address}>{accommodation.address}</p>
        <p className={styles.description}>{accommodation.description}</p>

        {/* 계산된 팀당 월세 */}
        <div className={styles.priceWrap}>
          {teamPrice > 0 ? (
            <>
              <span className={styles.priceLabel}>팀당 월세</span>
              <span className={styles.price}>
                {teamPrice.toLocaleString()}원
              </span>
              <span className={styles.priceUnit}>/ 개월</span>
            </>
          ) : (
            <span className={styles.priceEmpty}>가격 정보 없음</span>
          )}
        </div>
      </div>
    </div>
  );
}
