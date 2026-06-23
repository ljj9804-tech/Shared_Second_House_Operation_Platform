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

// Spring Page 응답 타입
interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number;
}

/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : app/accommodations/page.tsx
 * 역할  : 숙소 목록 페이지 (검색 + 슬라이딩 윈도우 페이지네이션)
 * 사용처 : 앱 내 숙소 목록 진입점, app/page.tsx 에서 "더 보러가기" 클릭 시 이동
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - AccommodationCard.tsx                    : 숙소 카드 컴포넌트
 * - PriceCalculator.tsx                      : 상단 가격 계산기 컴포넌트
 * - page.module.css                          : 스타일
 * - Spring: StayAccommodationController.java : GET /api/stay/accommodations
 * ----------------------------------------------------------------------------------
 * [기능 목록]
 * - 숙소 목록 조회 (검색 + 페이지네이션, 6개씩)
 * - 이름 검색 (Enter 키 또는 검색 버튼)
 * - X 버튼으로 검색어 초기화 및 전체 목록 복원
 * - 슬라이딩 윈도우 페이지네이션 (최대 5개 버튼)
 * ----------------------------------------------------------------------------------
 * [파일 흐름과 순서]
 * 진입 → useEffect(page, keyword) → GET ?page=0&size=6
 *       → data.content → setAccommodations / data.totalPages → setTotalPages
 *       → 검색 입력 → handleSearch() → setKeyword → useEffect 재실행
 *       → 페이지 버튼 클릭 → setCurrentPage → useEffect 재실행
 * ==================================================================================
 */

const PAGE_SIZE = 6;

export default function AccommodationsPage() {
  const [accommodations, setAccommodations] = useState<StayAccommodationDto[]>(
    []
  );
  const [totalPages, setTotalPages] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [keyword, setKeyword] = useState('');
  const [inputValue, setInputValue] = useState('');
  const [loading, setLoading] = useState(true);

  // 계산기 상태
  const [teams, setTeams] = useState(1);
  const [months, setMonths] = useState(1);

  useEffect(() => {
    setLoading(true);
    const params = new URLSearchParams({
      page: String(currentPage),
      size: String(PAGE_SIZE),
    });
    if (keyword) params.set('keyword', keyword);

    fetch(
      `${process.env.NEXT_PUBLIC_SERVER_URL}/api/stay/accommodations?${params}`
    )
      .then((res) => res.json())
      .then((data: PageResponse<StayAccommodationDto>) => {
        setAccommodations(data.content);
        setTotalPages(data.totalPages);
      })
      .catch((err) => console.log('숙소 목록 조회 실패:', err))
      .finally(() => setLoading(false));
  }, [currentPage, keyword]);

  const handleSearch = () => {
    setCurrentPage(0);
    setKeyword(inputValue.trim());
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') handleSearch();
  };

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

      {/* 검색창 */}
      <div className={styles.searchWrap}>
        <div className={styles.searchInputWrap}>
          <input
            className={styles.searchInput}
            placeholder="숙소 이름으로 검색"
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            onKeyDown={handleKeyDown}
          />
          {inputValue && (
            <button
              className={styles.clearBtn}
              onClick={() => {
                setInputValue('');
                setCurrentPage(0);
                setKeyword('');
              }}
            >
              ✕
            </button>
          )}
        </div>
        <button
          className={`btn-primary ${styles.searchBtn}`}
          onClick={handleSearch}
        >
          검색
        </button>
      </div>

      {/* 숙소 목록 */}
      {loading ? (
        <div className={styles.loading}>불러오는 중...</div>
      ) : accommodations.length === 0 ? (
        <div className={styles.empty}>검색 결과가 없습니다.</div>
      ) : (
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
      )}

      {/* 페이지네이션 - 슬라이딩 윈도우 (최대 5개) */}
      {totalPages > 1 &&
        (() => {
          const start = Math.max(0, Math.min(currentPage - 2, totalPages - 5));
          const end = Math.min(totalPages - 1, Math.max(currentPage + 2, 4));
          const pages = Array.from(
            { length: end - start + 1 },
            (_, i) => start + i
          );
          return (
            <div className={styles.pagination}>
              <button
                className={styles.pageBtn}
                onClick={() => setCurrentPage((p) => p - 1)}
                disabled={currentPage === 0}
              >
                이전
              </button>
              {pages.map((i) => (
                <button
                  key={i}
                  className={`${styles.pageBtn} ${i === currentPage ? styles.pageBtnActive : ''}`}
                  onClick={() => setCurrentPage(i)}
                >
                  {i + 1}
                </button>
              ))}
              <button
                className={styles.pageBtn}
                onClick={() => setCurrentPage((p) => p + 1)}
                disabled={currentPage === totalPages - 1}
              >
                다음
              </button>
            </div>
          );
        })()}
    </div>
  );
}
