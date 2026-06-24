"use client";

import { useState, useEffect, useRef, useCallback } from "react";
import axios from "axios";
import styles from "../layout.module.css";

/* ==========================================
 * [1] 상수 및 타입 정의 (Static Data & Types)
 * ========================================== */
const TOUR_CATEGORIES = [
  { label: "서울", code: "11" },
  { label: "부산", code: "26" },
  { label: "대구", code: "27" },
  { label: "인천", code: "28" },
  { label: "광주", code: "29" },
  { label: "대전", code: "30" },
  { label: "울산", code: "31" },
  { label: "세종", code: "36" },
  { label: "경기", code: "41" },
  { label: "강원", code: "51" },
  { label: "충북", code: "43" },
  { label: "충남", code: "44" },
  { label: "전북", code: "52" },
  { label: "전남", code: "46" },
  { label: "경북", code: "47" },
  { label: "경남", code: "48" },
  { label: "제주", code: "50" },
];

interface TourItem {
  title: string;
  firstimage: string;
  addr1: string;
  contentid: string;
}

// 1. 서버 응답용 인터페이스 정의 추가
interface TourServerResponse {
  tours: TourItem[];
  isLast: boolean;
}

export default function TourListPage() {
  /* ==========================================
   * [2] 상태 관리 변수 (Component States)
   * ========================================== */
  const [selectedRegion, setSelectedRegion] = useState<string>("11");
  const [tourList, setTourList] = useState<TourItem[]>([]);
  const [pageNo, setPageNo] = useState<number>(0); // 💡 1에서 0으로 변경
  const [hasMore, setHasMore] = useState<boolean>(true);
  const [isLoading, setIsLoading] = useState<boolean>(false);

  /* ==========================================
   * [3] 참조 변수 (Refs - 렌더링 무관 변수 및 DOM 접근)
   * ========================================== */
  const observerTarget = useRef<HTMLDivElement | null>(null);
  const isLoadingRef = useRef<boolean>(false);

  /* ==========================================
   * [4] 데이터 비동기 통신 (API Fetch Functions)
   * ========================================== */
  const fetchTourData = useCallback(
    async (region: string, page: number, isNewCategory = false) => {
      // 더이상 가져올 데이터가 없거나, 이미 로딩 중이면 요청 중단
      if (!isNewCategory && !hasMore) return;
      // api 요청 중복 방지: isLoadingRef를 통해 상태를 확인하고, 동시에 여러 요청이 발생하지 않도록 제어
      if (isLoadingRef.current) return;

      isLoadingRef.current = true;
      setIsLoading(true);

      try {
        const response = await axios.get<TourServerResponse>(
          `http://localhost:8080/api/tours?lDongRegnCd=${region}&pageNo=${page}`,
        );
        const { tours, isLast } = response.data;

        if (isNewCategory) {
          setTourList(tours);
        } else {
          setTourList((prev) => [...prev, ...tours]);
        }

        // 💡 [수정] 서버가 알려준 확실한 지표인 isLast의 반대값을 세팅
        setHasMore(!isLast);
      } catch (error) {
        console.error("관광지 데이터를 불러오는 중 오류 발생:", error);
      } finally {
        isLoadingRef.current = false;
        setIsLoading(false);
      }
    },
    [hasMore], // 의존성 배열에 hasMore 추가: hasMore 상태가 변경될 때마다 fetchTourData 함수가 최신 상태를 반영하도록 함
  );

  /* ==========================================
   * [5] 이벤트 핸들러 (User Event Handlers)
   * ========================================== */
  const handleCategoryChange = (regionCode: string) => {
    if (selectedRegion === regionCode) return;

    setSelectedRegion(regionCode);
    setPageNo(0); // 💡 페이지 번호 초기화 (1에서 0으로 변경)
    setHasMore(true);

    // 상태 변경 배치 처리를 기다리지 않고 즉시 1페이지 데이터 요청 (Effect 우회 최적화)
    fetchTourData(regionCode, 0, true);
  };

  /* ==========================================
   * [6] 생명주기 및 사이드 이펙트 (Effects)
   * ========================================== */
  // 무한 스크롤 감지 (Intersection Observer) 설정
  useEffect(() => {
    // 더 가져올 데이터가 없거나 로딩 중이면 관찰을 시작하지 않음
    if (!observerTarget.current || !hasMore || isLoading) return;

    const observer = new IntersectionObserver(
      (entries) => {
        const { isIntersecting } = entries[0];

        if (isIntersecting && !isLoadingRef.current && hasMore) {
          const nextPage = pageNo + 1;

          setPageNo(nextPage);
          // 최초 진입(nextPage가 1일 때)에는 기존 데이터를 덮어쓰도록(true) 설정
          fetchTourData(selectedRegion, nextPage, nextPage === 1);
        }
      },
      { threshold: 0.1 },
    );

    observer.observe(observerTarget.current);

    return () => {
      if (observerTarget.current) observer.unobserve(observerTarget.current);
    };
  }, [hasMore, pageNo, selectedRegion, fetchTourData, isLoading]);

  /* ==========================================
   * [7] UI 렌더링 구역 (JSX)
   * ========================================== */
  return (
    <div
      className={styles.main}
      style={{
        maxWidth: "var(--max-width)",
        margin: "0 auto",
        padding: "24px",
      }}
    >
      {/* 🧭 상단 지역 카테고리 탭 버튼 바 */}
      <div
        style={{
          display: "flex",
          gap: "8px",
          flexWrap: "wrap",
          marginBottom: "24px",
          paddingBottom: "16px",
          borderBottom: "1px solid var(--color-border)",
        }}
      >
        {TOUR_CATEGORIES.map((region) => {
          const isSelected = selectedRegion === region.code;
          return (
            <button
              key={region.code}
              onClick={() => handleCategoryChange(region.code)}
              className={isSelected ? "btn-sm" : "btn-outline"}
              style={{
                width: "auto",
                padding: "8px 16px",
                borderRadius: "var(--radius-sm)",
                whiteSpace: "nowrap",
              }}
            >
              {region.label}
            </button>
          );
        })}
      </div>

      {/* 📋 관광지 카드 그리드 리스트 크롤러 메인 */}
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fill, minmax(280px, 1fr))",
          gap: "24px",
          marginBottom: "20px",
        }}
      >
        {tourList.map((tour) => (
          <div
            key={tour.contentid}
            style={{
              backgroundColor: "var(--color-card-bg)",
              borderRadius: "var(--radius-md)",
              overflow: "hidden",
              boxShadow: "var(--shadow-card)",
              display: "flex",
              flexDirection: "column",
            }}
          >
            {/* 상단 썸네일 구역 */}
            <div
              style={{
                width: "100%",
                height: "200px",
                backgroundColor: "#eee",
                position: "relative",
              }}
            >
              {tour.firstimage ? (
                <img
                  src={tour.firstimage}
                  alt={tour.title}
                  style={{ width: "100%", height: "100%", objectFit: "cover" }}
                />
              ) : (
                <div
                  style={{
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    height: "100%",
                    color: "var(--color-text-muted)",
                    fontSize: "var(--font-size-sm)",
                  }}
                >
                  이미지 준비중
                </div>
              )}
            </div>

            {/* 하단 텍스트 정보 구역 */}
            <div
              style={{
                padding: "16px",
                flex: 1,
                display: "flex",
                flexDirection: "column",
                justifyContent: "center",
              }}
            >
              <h3
                style={{
                  fontSize: "var(--font-size-base)",
                  fontWeight: "700",
                  marginBottom: "8px",
                  lineHeight: "1.4",
                }}
              >
                {tour.title}
              </h3>
              <p
                style={{
                  fontSize: "var(--font-size-sm)",
                  color: "var(--color-text-muted)",
                  marginBottom: "4px",
                }}
              >
                {tour.addr1 || "주소 정보 없음"}
              </p>
            </div>
          </div>
        ))}
      </div>

      {/* 🔄 무한 스크롤 하단 상태 표시기 및 바닥 감지 타겟 */}
      <div
        ref={observerTarget}
        style={{
          height: "40px",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          margin: "20px 0",
        }}
      >
        {isLoading && (
          <span
            style={{
              color: "var(--color-primary)",
              fontWeight: "600",
              fontSize: "var(--font-size-base)",
            }}
          >
            데이터를 추가로 불러오는 중입니다...
          </span>
        )}
        {!hasMore && tourList.length > 0 && (
          <span
            style={{
              color: "var(--color-text-muted)",
              fontSize: "var(--font-size-sm)",
            }}
          >
            모든 관광지 정보를 가져왔습니다.
          </span>
        )}
      </div>
    </div>
  );
}
