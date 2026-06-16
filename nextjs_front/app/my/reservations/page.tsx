'use client';

import { useEffect, useState } from 'react';
import styles from './page.module.css';

interface ReservationDto {
  id: number;
  startDate: string;
  endDate: string;
  status: 'CONFIRMED' | 'CANCELLED';
  stayAccommodation: {
    id: number;
    name: string;
    address: string;
  };
}

export default function MyReservationsPage() {
  const [reservations, setReservations] = useState<ReservationDto[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/stay/reservations`)
      .then((r) => r.json())
      .then((data) => {
        console.log('내 예약 목록:', data);
        setReservations(data);
      })
      .catch((err) => console.log('예약 목록 조회 실패:', err))
      .finally(() => setLoading(false));
  }, []);

  // 예약 취소
  const handleCancel = (id: number) => {
    if (!confirm('예약을 취소할까요?')) return;

    fetch(
      `${process.env.NEXT_PUBLIC_API_BASE_URL}/stay/reservations/${id}/cancel`,
      {
        method: 'PATCH',
      }
    )
      .then((r) => {
        if (!r.ok) throw new Error('취소 실패');
        return r.text(); // json() 대신 text()로 변경
      })
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
          {reservations.map((reservation) => (
            <div key={reservation.id} className={styles.card}>
              {/* 숙소 정보 */}
              <div className={styles.cardHeader}>
                <h2 className={styles.accommodationName}>
                  {reservation.stayAccommodation?.name}
                </h2>
                <span
                  className={
                    reservation.status === 'CONFIRMED'
                      ? styles.statusConfirmed
                      : styles.statusCancelled
                  }
                >
                  {reservation.status === 'CONFIRMED' ? '예약 확정' : '취소됨'}
                </span>
              </div>

              <p className={styles.address}>
                {reservation.stayAccommodation?.address}
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

              {/* 취소 버튼 */}
              {reservation.status === 'CONFIRMED' && (
                <button
                  className="btn-danger"
                  onClick={() => handleCancel(reservation.id)}
                >
                  예약 취소
                </button>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
