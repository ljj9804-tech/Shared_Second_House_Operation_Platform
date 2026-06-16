'use client';

import { useEffect, useState } from 'react';
import AccommodationCard from './components/AccommodationCard';
import PriceCalculator from '../components/PriceCalculator';
import styles from './page.module.css';

// 숙소 가격 구간 타입
export interface StayAccommodationPriceDto {
  id: number;
  minMonths: number;
  maxMonths: number | null;
  discountRate: number;
}

// 숙소 타입
export interface StayAccommodationDto {
  id: number;
  name: string;
  address: string;
  description: string;
  imageUrl: string;
  amenities: string;
  monthlyPrice: number;
  roomCount: number;
  bathroomCount: number;
  floorCount: number;
  parkingCount: number;
  landArea: number;
  buildingArea: number;
  latitude: number;
  longitude: number;
  status: 'AVAILABLE' | 'MAINTENANCE';
  prices: StayAccommodationPriceDto[];
}

export default function AccommodationsPage() {
  const [accommodations, setAccommodations] = useState<StayAccommodationDto[]>(
    []
  );
  const [loading, setLoading] = useState(true);

  // 계산기 상태 (목록 페이지 헤더에서 전체 공유)
  const [teams, setTeams] = useState(1);
  const [months, setMonths] = useState(1);

  useEffect(() => {
    // GET /api/stay/accommodations
    fetch(`${process.env.NEXT_PUBLIC_SERVER_URL}/api/stay/accommodations`)
      .then((res) => res.json())
      .then((data) => {
        setAccommodations(data);
        setLoading(false);
      })
      .catch((err) => {
        console.log('숙소 목록 조회 실패:', err);
        setLoading(false);
      });
  }, []);

  if (loading) return <div className={styles.loading}>불러오는 중...</div>;

  return (
    <div className={styles.container}>
      {/* 상단 계산기 */}
      <div className={styles.calculatorWrap}>
        <PriceCalculator
          teams={teams}
          months={months}
          onTeamsChange={setTeams}
          onMonthsChange={setMonths}
        />
      </div>

      {/* 숙소 목록 */}
      <div className={styles.grid}>
        {accommodations.map((accommodation) => (
          <AccommodationCard
            key={accommodation.id}
            accommodation={accommodation}
            teams={teams}
            months={months}
          />
        ))}
      </div>
    </div>
  );
}
