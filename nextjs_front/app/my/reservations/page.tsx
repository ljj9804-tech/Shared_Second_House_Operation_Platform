/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : app/my/reservations/page.tsx
 * 역할  : 내 예약 목록 페이지 (예약 상태 확인 + 예약 취소)
 * 사용처 : /my/reservations 진입 시 렌더링, Navbar "내 예약" 클릭 시 이동
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - app/lib/auth.ts : api 인스턴스, TEMP_USER_ID
 * - Spring: StayReservationController.java
 *     GET   /api/stay/reservations?userId=       : 내 예약 목록 조회
 *     PATCH /api/stay/reservations/{id}/cancel   : 예약 취소
 * ----------------------------------------------------------------------------------
 * [기능 목록]
 * - 내 예약 목록 조회 및 표시
 * - 예약 상태 표시: 예약 확정 / 취소됨 / 지난 예약 (종료일 기준 자동 판별)
 * - 미래 확정 예약만 취소 버튼 표시 (startDate > 오늘)
 * - 취소 시 리스트 상태 즉시 업데이트 (전체 재조회 없이 setReservations)
 * ----------------------------------------------------------------------------------
 * [파일 흐름과 순서]
 * 진입 → GET /api/stay/reservations?userId=TEMP_USER_ID → setReservations
 * → 취소 버튼 클릭 → confirm → PATCH /api/stay/reservations/{id}/cancel
 * → 성공 시 해당 예약 status: 'CANCELLED'로 즉시 업데이트
 * ----------------------------------------------------------------------------------
 * [주의사항 / 참고]
 * ⚠️ [TODO] 로그인 연동 후: TEMP_USER_ID → 로그인 유저 ID로 교체
 * ==================================================================================
 */

'use client';

import { useEffect, useState } from 'react';
import styles from './page.module.css';
import api, { TEMP_USER_ID } from '@/app/lib/auth';
import ReservationRouteMap from './components/ReservationRouteMap';

interface ReservationDto {
  id: number;
  accommodationId: number;
  accommodationName: string;
  accommodationAddress: string;
  startDate: string;
  endDate: string;
  status: 'CONFIRMED' | 'CANCELLED';
}

export default function MyReservationsPage() {
  const [reservations, setReservations] = useState<ReservationDto[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api
      .get(`/api/stay/reservations?userId=${TEMP_USER_ID}`)
      .then((r) => r.data)
      .then((data) => {
        console.log('내 예약 목록:', data);
        setReservations(Array.isArray(data) ? data : []);
      })
      .catch((err) => console.log('예약 목록 조회 실패:', err))
      .finally(() => setLoading(false));
  }, []);

  // 예약 취소
  const handleCancel = (id: number) => {
    if (!confirm('예약을 취소할까요?')) return;

    api
      .patch(`/api/stay/reservations/${id}/cancel?userId=${TEMP_USER_ID}`)
      .then(() => {
        console.log('예약 취소 완료:', id);
        // 취소된 예약 상태 업데이트
        setReservations((prev) =>
          prev.map((r) => (r.id === id ? { ...r, status: 'CANCELLED' } : r))
        );
      })
      .catch((err) => {
        console.log('예약 취소 실패:', err);
        alert('예약 취소에 실패했어요. 다시 시도해주세요.');
      });
  };

  if (loading) return <div className={styles.loading}>불러오는 중...</div>;

  return (
    <div className={styles.container}>
      <h1 className={styles.title}>내 예약 목록</h1>

      {reservations.length === 0 ? (
        <div className={styles.empty}>예약 내역이 없어요.</div>
      ) : (
        <div className={styles.list}>
          {reservations?.map((reservation) => (
            <div key={reservation.id} className={styles.card}>
              {/* 숙소 정보 */}
              <div className={styles.cardHeader}>
                <h2 className={styles.accommodationName}>
                  {reservation.accommodationName}
                </h2>
                <span
                  className={
                    reservation.status === 'CANCELLED'
                      ? styles.statusCancelled
                      : new Date(reservation.endDate) < new Date()
                        ? styles.statusExpired
                        : styles.statusConfirmed
                  }
                >
                  {reservation.status === 'CANCELLED'
                    ? '취소됨'
                    : new Date(reservation.endDate) < new Date()
                      ? '지난 예약'
                      : '예약 확정'}
                </span>
              </div>

              <p className={styles.address}>
                {reservation.accommodationAddress}
              </p>

              {/* 예약 날짜 */}
              <div className={styles.dates}>
                <div className={styles.dateItem}>
                  <span className={styles.dateLabel}>시작일</span>
                  <span className={styles.dateValue}>
                    {reservation.startDate}
                  </span>
                </div>
                <span className={styles.dateDivider}>→</span>
                <div className={styles.dateItem}>
                  <span className={styles.dateLabel}>종료일</span>
                  <span className={styles.dateValue}>
                    {reservation.endDate}
                  </span>
                </div>
              </div>

              {/* 취소 버튼 - 미래 예약만 표시 */}
              {reservation.status === 'CONFIRMED' &&
                new Date(reservation.startDate) > new Date() && (
                  <button
                    className="btn-danger"
                    onClick={() => handleCancel(reservation.id)}
                  >
                    예약 취소
                  </button>
                )}

              {/* 이 예약 기간에 기록된 이동경로 지도 (경로 없으면 자동 숨김) */}
              <ReservationRouteMap
                accommodationId={reservation.accommodationId}
                startDate={reservation.startDate}
                endDate={reservation.endDate}
              />
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
