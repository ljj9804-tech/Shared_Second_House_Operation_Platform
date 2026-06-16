'use client';

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import styles from './page.module.css';
import {
  StayAccommodationDto,
  StayAccommodationPriceDto,
} from '../../accommodations/page';
import { calcTeamPrice } from '../../accommodations/[id]/page';

export default function SubscribePage() {
  const params = useParams();
  const router = useRouter();
  const accommodationId = params.accommodationId as string;

  const [accommodation, setAccommodation] =
    useState<StayAccommodationDto | null>(null);
  const [prices, setPrices] = useState<StayAccommodationPriceDto[]>([]);
  const [loading, setLoading] = useState(true);

  // 폼 상태
  const [memberIds, setMemberIds] = useState<string[]>(['']);
  const [durationMonths, setDurationMonths] = useState(1);

  // TODO [인증]: userId 하드코딩 → JWT 토큰에서 실제 userId 추출로 교체
  const leaderId = 1;

  // 팀 인원 (대표자 + 입력된 팀원)
  const totalMembers = memberIds.length + 1;

  // 팀당 월세 계산
  const teamPrice = accommodation
    ? calcTeamPrice(
        accommodation.monthlyPrice,
        prices,
        durationMonths,
        totalMembers
      )
    : 0;

  useEffect(() => {
    fetch(
      `${process.env.NEXT_PUBLIC_SERVER_URL}/api/stay/accommodations/${accommodationId}`
    )
      .then((r) => r.json())
      .then((data) => {
        console.log('숙소 데이터:', data);
        setAccommodation(data);
        setPrices(data.prices ?? []);
      })
      .catch((err) => console.log('숙소 조회 실패:', err))
      .finally(() => setLoading(false));
  }, [accommodationId]);

  // 팀원 추가
  const addMember = () => {
    setMemberIds((prev) => [...prev, '']);
  };

  // 팀원 삭제
  const removeMember = (index: number) => {
    setMemberIds((prev) => prev.filter((_, i) => i !== index));
  };

  // 팀원 ID 변경
  const updateMember = (index: number, value: string) => {
    setMemberIds((prev) => prev.map((id, i) => (i === index ? value : id)));
  };

  // 구독 신청
  const handleSubmit = () => {
    const body = {
      accommodationId: Number(accommodationId),
      durationMonths,
      memberIdentifiers: memberIds.filter((id) => id.trim() !== ''),
    };

    console.log('구독 신청 요청:', body);

    fetch(`${process.env.NEXT_PUBLIC_SERVER_URL}/api/waiting/apply/${leaderId}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    })
      .then((r) => r.json())
      .then((data) => {
        console.log('구독 신청 완료:', data);
        alert('구독 신청이 완료됐어요! 관리자 승인을 기다려주세요.');
        router.push(`/accommodations/${accommodationId}`);
      })
      .catch((err) => {
        console.log('구독 신청 실패:', err);
        alert('구독 신청에 실패했어요. 다시 시도해주세요.');
      });
  };

  if (loading) return <div className={styles.loading}>불러오는 중...</div>;
  if (!accommodation)
    return <div className={styles.loading}>숙소를 찾을 수 없습니다.</div>;

  return (
    <div className={styles.container}>
      <h1 className={styles.title}>구독 신청</h1>

      {/* 숙소 정보 */}
      <div className={styles.accommodationInfo}>
        <h2 className={styles.accommodationName}>{accommodation.name}</h2>
        <p className={styles.accommodationAddress}>{accommodation.address}</p>
        <p className={styles.accommodationPrice}>
          월 {accommodation.monthlyPrice.toLocaleString()}원
        </p>
      </div>

      {/* 폼 */}
      <div className={styles.form}>
        {/* 대표자 */}
        <div className={styles.formGroup}>
          <label className={styles.label}>대표자 ID</label>
          <input className={styles.input} value={leaderId} disabled />
          <span className={styles.hint}>
            ※ 로그인한 유저가 대표자로 자동 설정됩니다.
          </span>
        </div>

        {/* 함께할 팀원 */}
        <div className={styles.formGroup}>
          <label className={styles.label}>함께할 팀원 아이디 또는 이메일</label>
          {memberIds.map((memberId, index) => (
            <div key={index} className={styles.memberRow}>
              <input
                className={styles.input}
                placeholder={`팀원 ${index + 1} 아이디 또는 이메일 입력`}
                value={memberId}
                onChange={(e) => updateMember(index, e.target.value)}
              />
              {memberIds.length > 1 && (
                <button
                  className="btn-danger"
                  onClick={() => removeMember(index)}
                >
                  삭제
                </button>
              )}
            </div>
          ))}
          <button className="btn-secondary" onClick={addMember}>
            + 팀원 추가
          </button>
        </div>

        {/* 개월수 선택 */}
        <div className={styles.formGroup}>
          <label className={styles.label}>계약 개월수</label>
          <select
            className={styles.select}
            value={durationMonths}
            onChange={(e) => setDurationMonths(Number(e.target.value))}
          >
            {Array.from({ length: 12 }, (_, i) => i + 1).map((n) => (
              <option key={n} value={n}>
                {n} 개월
              </option>
            ))}
          </select>
        </div>

        {/* 구독 요약 */}
        <div className={styles.summary}>
          <div className={styles.summaryRow}>
            <span className={styles.summaryLabel}>원래 월세</span>
            <span className={styles.summaryValue}>
              {accommodation.monthlyPrice.toLocaleString()}원 / 월
            </span>
          </div>
          <div className={styles.summaryRow}>
            <span className={styles.summaryLabel}>팀 인원</span>
            <span className={styles.summaryValue}>
              {totalMembers}명 (대표자 포함)
            </span>
          </div>
          <div className={styles.summaryRow}>
            <span className={styles.summaryLabel}>계약 기간</span>
            <span className={styles.summaryValue}>{durationMonths}개월</span>
          </div>

          <hr className={styles.summaryDivider} />

          <div className={styles.summaryRow}>
            <span className={styles.summaryLabel}>팀당 월세</span>
            <span className={styles.summaryPrice}>
              {teamPrice > 0 ? `${teamPrice.toLocaleString()}원` : '-'} / 월
            </span>
          </div>
        </div>

        {/* 신청 버튼 */}
        <button className="btn-primary" onClick={handleSubmit}>
          구독 신청하기
        </button>
      </div>
    </div>
  );
}
