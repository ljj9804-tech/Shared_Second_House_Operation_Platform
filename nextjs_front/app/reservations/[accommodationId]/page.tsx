'use client';

import { useEffect, useState, useMemo, useCallback } from 'react';
import { useParams, useRouter } from 'next/navigation';
import DatePicker from 'react-datepicker';
import { ko } from 'date-fns/locale';
import 'react-datepicker/dist/react-datepicker.css';
import styles from './page.module.css';
import api, { TEMP_USER_ID } from '@/app/lib/auth';

// Spring LocalDate가 배열([2026,6,25]) 또는 문자열("2026-06-25") 두 형태로 올 수 있음
// 로컬 자정 기준으로 변환해야 한국(UTC+9)에서 하루 밀림 현상 없음
const parseLocalDate = (dateInput: string | number[]): Date => {
  if (Array.isArray(dateInput)) {
    const [year, month, day] = dateInput;
    return new Date(year, month - 1, day);
  }
  const [year, month, day] = dateInput.split('-').map(Number);
  return new Date(year, month - 1, day);
};

// toISOString()은 UTC 기준이라 한국(+9)에서 하루 밀림 → 로컬 기준으로 포맷
const formatLocalDate = (date: Date): string => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

interface ReservationDto {
  id: number;
  startDate: string | number[];
  endDate: string | number[];
  status: string;
}

interface SubscriptionDto {
  accommodationId: number;
  startDate: string | number[];
  endDate: string | number[];
  status: string;
}

export default function ReservationPage() {
  const params = useParams();
  const router = useRouter();
  const accommodationId = params.accommodationId as string;

  const [startDate, setStartDate] = useState<Date | null>(null);
  const [endDate, setEndDate] = useState<Date | null>(null);
  const [reservedDates, setReservedDates] = useState<Date[]>([]);
  const [subscription, setSubscription] = useState<SubscriptionDto | null>(null);
  const [loading, setLoading] = useState(true);

  const userId = TEMP_USER_ID;

  // 예약 데이터 fetch (실시간 갱신을 위해 함수로 분리)
  const fetchReservations = useCallback(() => {
    api.get(`/api/stay/reservations/accommodation/${accommodationId}`)
      .then((r) => r.data)
      .then((reservationsData) => {
        console.log('[ReservationPage] 예약 데이터 갱신:', reservationsData);
        const dates: Date[] = [];
        reservationsData
          .filter((r: ReservationDto) => r.status === 'CONFIRMED')
          .forEach((r: ReservationDto) => {
            const start = parseLocalDate(r.startDate);
            const end = parseLocalDate(r.endDate);
            const current = new Date(start);
            while (current <= end) {
              dates.push(new Date(current));
              current.setDate(current.getDate() + 1);
            }
          });
        setReservedDates(dates);
        // 날짜 갱신 시 선택 초기화
        setStartDate(null);
        setEndDate(null);
      })
      .catch((err) => console.log('[ReservationPage] 예약 데이터 갱신 실패:', err));
  }, [accommodationId]);

  // 초기 로딩 (구독 + 예약 데이터)
  useEffect(() => {
    Promise.all([
      api.get(`/api/subscriptions/my/${userId}`).then((r) => r.data),
      api.get(`/api/stay/reservations/accommodation/${accommodationId}`).then((r) => r.data),
    ])
      .then(([subscriptionData, reservationsData]) => {
        console.log('[ReservationPage] 구독 데이터:', subscriptionData);
        console.log('[ReservationPage] 예약 데이터:', reservationsData);

        const activeSubscription = Array.isArray(subscriptionData)
          ? subscriptionData.find(
              (s) => s.status === 'ACTIVE' && s.accommodationId === Number(accommodationId)
            )
          : null;
        setSubscription(activeSubscription ?? null);

        const dates: Date[] = [];
        reservationsData
          .filter((r: ReservationDto) => r.status === 'CONFIRMED')
          .forEach((r: ReservationDto) => {
            const start = parseLocalDate(r.startDate);
            const end = parseLocalDate(r.endDate);
            const current = new Date(start);
            while (current <= end) {
              dates.push(new Date(current));
              current.setDate(current.getDate() + 1);
            }
          });
        setReservedDates(dates);
      })
      .catch((err) => console.log('[ReservationPage] 데이터 조회 실패:', err))
      .finally(() => setLoading(false));
  }, []);

  // 탭 전환 후 돌아왔을 때 예약 데이터 실시간 갱신
  useEffect(() => {
    const handleFocus = () => fetchReservations();
    window.addEventListener('focus', handleFocus);
    return () => window.removeEventListener('focus', handleFocus);
  }, [fetchReservations]);

  const today = useMemo(() => {
    const d = new Date();
    d.setHours(0, 0, 0, 0);
    return d;
  }, []);

  const baseMinDate = useMemo(() => {
    if (!subscription) return today;
    const subStart = parseLocalDate(subscription.startDate);
    return subStart > today ? subStart : today;
  }, [subscription, today]);

  const baseMaxDate = useMemo(() => {
    return subscription ? parseLocalDate(subscription.endDate) : today;
  }, [subscription, today]);

  // 시작일 선택 후: 다음 예약 날짜 직전까지만 종료일 선택 가능
  const dynamicMaxDate = useMemo(() => {
    if (!startDate) return baseMaxDate;

    const nextBlocked = reservedDates
      .filter((d) => d.getTime() > startDate.getTime())
      .sort((a, b) => a.getTime() - b.getTime())[0];

    if (!nextBlocked) return baseMaxDate;

    const dayBefore = new Date(nextBlocked);
    dayBefore.setDate(dayBefore.getDate() - 1);

    return dayBefore.getTime() < baseMaxDate.getTime() ? dayBefore : baseMaxDate;
  }, [startDate, reservedDates, baseMaxDate]);

  // 시작일 선택 후 연박 불가 여부
  const noEndDateAvailable =
    startDate !== null && dynamicMaxDate.getTime() <= startDate.getTime();

  // 예약 생성
  const handleReserve = () => {
    if (!startDate || !endDate) {
      alert('시작일과 종료일을 모두 선택해주세요.');
      return;
    }

    const body = {
      accommodationId: Number(accommodationId),
      userId: userId,
      startDate: formatLocalDate(startDate),
      endDate: formatLocalDate(endDate),
    };

    console.log('[ReservationPage] 예약 생성 요청:', body);

    api.post(`/api/stay/reservations`, body)
      .then((r) => r.data)
      .then((data) => {
        console.log('[ReservationPage] 예약 생성 완료:', data);
        alert('예약이 완료됐어요!');
        router.push('/my/reservations');
      })
      .catch((err) => {
        const msg = err.response?.data?.message ?? '예약에 실패했어요. 다시 시도해주세요.';
        console.log('[ReservationPage] 예약 생성 실패:', err);
        alert(msg);
        // 실패 시 달력 즉시 갱신 (다른 사용자가 먼저 예약한 경우 반영)
        fetchReservations();
      });
  };

  if (loading) return <div className={styles.loading}>불러오는 중...</div>;

  return (
    <div className={styles.container}>
      <h1 className={styles.title}>예약하기</h1>

      {/* 기본 안내 */}
      {!startDate && (
        <p className={styles.calendarHint}>
          시작일을 먼저 선택해주세요. 회색 날짜는 이미 예약된 날짜입니다.
        </p>
      )}

      {/* 연박 불가 안내 */}
      {noEndDateAvailable && (
        <p className={styles.calendarWarning}>
          선택하신 날짜는 연박 예약이 어렵습니다. 다른 시작일을 선택해주세요.
        </p>
      )}

      <div className={styles.calendarWrap}>
        <DatePicker
          selected={startDate}
          onChange={(dates) => {
            const [start, end] = dates as [Date | null, Date | null];
            // 최소 1박 2일: 종료일이 시작일과 같거나 이전이면 종료일 초기화
            if (start && end && end.getTime() <= start.getTime()) {
              setStartDate(start);
              setEndDate(null);
              return;
            }
            setStartDate(start);
            setEndDate(end);
          }}
          startDate={startDate}
          endDate={endDate}
          selectsRange
          inline
          minDate={baseMinDate}
          maxDate={dynamicMaxDate}
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
