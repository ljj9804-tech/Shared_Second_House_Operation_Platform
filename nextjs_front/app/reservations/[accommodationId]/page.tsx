'use client';

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import DatePicker from 'react-datepicker';
import { ko } from 'date-fns/locale';
import 'react-datepicker/dist/react-datepicker.css';
import styles from './page.module.css';

interface ReservationDto {
  id: number;
  startDate: string;
  endDate: string;
  status: string;
}

interface SubscriptionDto {
  startDate: string;
  endDate: string;
  status: string;
}

export default function ReservationPage() {
  const params = useParams();
  const router = useRouter();
  const accommodationId = params.accommodationId as string;

  const [startDate, setStartDate] = useState<Date | null>(null);
  const [endDate, setEndDate] = useState<Date | null>(null);
  const [reservedDates, setReservedDates] = useState<Date[]>([]);
  const [subscription, setSubscription] = useState<SubscriptionDto | null>(
    null
  );
  const [loading, setLoading] = useState(true);

  // TODO [인증]: userId 하드코딩 → JWT 토큰에서 실제 userId 추출로 교체
  const userId = 1;

  useEffect(() => {
    Promise.all([
      fetch(
        `${process.env.NEXT_PUBLIC_SERVER_URL}/api/subscriptions/my/${userId}`
      ).then((r) => r.json()),
      fetch(`${process.env.NEXT_PUBLIC_SERVER_URL}/api/stay/reservations`).then(
        (r) => r.json()
      ),
    ])
      .then(([subscriptionData, reservationsData]) => {
        console.log('[ReservationPage] 구독 데이터:', subscriptionData);
        console.log('[ReservationPage] 예약 데이터:', reservationsData);

        // 배열로 오는 구독 데이터에서 첫 번째 ACTIVE 구독 찾기
        const activeSubscription = Array.isArray(subscriptionData)
          ? subscriptionData.find((s) => s.status === 'ACTIVE')
          : null;

        setSubscription(activeSubscription ?? null);

        // 이미 예약된 날짜 배열로 변환
        const dates: Date[] = [];
        reservationsData
          .filter((r: ReservationDto) => r.status === 'CONFIRMED')
          .forEach((r: ReservationDto) => {
            const start = new Date(r.startDate);
            const end = new Date(r.endDate);
            const current = new Date(start);
            while (current <= end) {
              dates.push(new Date(current));
              current.setDate(current.getDate() + 1);
            }
          });
        setReservedDates(dates);
      })
      .catch((err) => {
        console.log('[ReservationPage] 데이터 조회 실패:', err);
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  // 구독 기간 범위
  const minDate = subscription ? new Date(subscription.startDate) : new Date();
  const maxDate = subscription ? new Date(subscription.endDate) : new Date();

  // 예약 생성
  const handleReserve = () => {
    if (!startDate || !endDate) {
      alert('날짜를 선택해주세요.');
      return;
    }

    const body = {
      accommodationId: Number(accommodationId),
      userId: userId,
      startDate: startDate.toISOString().split('T')[0],
      endDate: endDate.toISOString().split('T')[0],
    };

    console.log('[ReservationPage] 예약 생성 요청:', body);

    fetch(`${process.env.NEXT_PUBLIC_SERVER_URL}/api/stay/reservations`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    })
      .then((r) => {
        if (!r.ok) throw new Error('예약 실패');
        return r.json();
      })
      .then((data) => {
        console.log('[ReservationPage] 예약 생성 완료:', data);
        alert('예약이 완료됐어요!');
        router.push('/my/reservations');
      })
      .catch((err) => {
        console.log('[ReservationPage] 예약 생성 실패:', err);
        alert('예약에 실패했어요. 다시 시도해주세요.');
      });
  };

  if (loading) return <div className={styles.loading}>불러오는 중...</div>;

  return (
    <div className={styles.container}>
      <h1 className={styles.title}>예약하기</h1>

      <div className={styles.calendarWrap}>
        <DatePicker
          selected={startDate}
          onChange={(dates) => {
            const [start, end] = dates as [Date | null, Date | null];
            setStartDate(start);
            setEndDate(end);
          }}
          startDate={startDate}
          endDate={endDate}
          selectsRange
          inline
          minDate={minDate}
          maxDate={maxDate}
          excludeDates={reservedDates}
          locale={ko}
          monthsShown={2}
          disabledKeyboardNavigation
        />
      </div>

      {/* 선택된 날짜 표시 */}
      <div className={styles.selectedDates}>
        <div className={styles.dateItem}>
          <span className={styles.dateLabel}>시작일</span>
          <span className={styles.dateValue}>
            {startDate ? startDate.toLocaleDateString('ko-KR') : '선택해주세요'}
          </span>
        </div>
        <div className={styles.dateDivider}>→</div>
        <div className={styles.dateItem}>
          <span className={styles.dateLabel}>종료일</span>
          <span className={styles.dateValue}>
            {endDate ? endDate.toLocaleDateString('ko-KR') : '선택해주세요'}
          </span>
        </div>
        {(startDate || endDate) && (
          <button
            className="btn-reset"
            onClick={() => {
              setStartDate(null);
              setEndDate(null);
            }}
          >
            초기화
          </button>
        )}
      </div>

      {/* 예약 버튼 */}
      <button
        className="btn-primary"
        onClick={handleReserve}
        disabled={!startDate || !endDate}
      >
        예약 확정
      </button>
    </div>
  );
}
