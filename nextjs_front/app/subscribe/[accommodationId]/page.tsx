/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : app/subscribe/[accommodationId]/page.tsx
 * 역할  : 구독 신청 페이지 (대표자 + 팀원 입력 → 구독 신청 요청)
 * 사용처 : 숙소 상세 페이지에서 "구독 신청하기" 버튼 클릭 시 이동
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - app/accommodations/[id]/page.tsx         : calcTeamPrice import 출처
 * - lib/api.ts                               : fetch 기반 API 클라이언트 (Bearer 토큰 자동 첨부)
 * - types/auth.ts                            : UserResp 타입 (userId 획득용)
 * - Spring: StayAccommodationController.java : GET /api/stay/accommodations/{id}  (permitAll)
 * - Spring: SubscriptionsController.java     : POST /api/waiting/apply/{leaderId} (인증 필요)
 * ----------------------------------------------------------------------------------
 * [기능 목록]
 * - 숙소 정보 조회 및 표시 (이름, 주소, 월세)
 * - 로그인 유저가 대표자로 자동 설정 (/api/users 로 userId 획득)
 * - 팀원 추가 / 삭제 (0명부터 자유롭게)
 * - 계약 개월수 선택 (1~12개월)
 * - 팀당 월세 실시간 계산 (대표자 포함 총 인원 기준)
 * - 구독 신청 요청 → 완료 시 상세 페이지로 이동
 * ----------------------------------------------------------------------------------
 * [파일 흐름과 순서]
 * 진입 → GET /api/stay/accommodations/{id} → 숙소 정보 표시
 *       → 팀원 추가/삭제 → totalMembers 변경 → 팀당 월세 재계산
 *       → 구독 신청 버튼 → POST /api/waiting/apply/{leaderId}
 *       → 완료 → /accommodations/{id} 이동
 * ==================================================================================
 */

'use client';

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import styles from './page.module.css';
import {
  StayAccommodationDto,
  StayAccommodationPriceDto,
} from '../../accommodations/page';
import { calcTeamPrice } from '../../accommodations/[id]/page';
import { api } from '@/lib/api';
import { UserResp } from '@/types/auth';

export default function SubscribePage() {
  const params = useParams();
  const router = useRouter();
  const accommodationId = params.accommodationId as string;

  const [accommodation, setAccommodation] =
    useState<StayAccommodationDto | null>(null);
  const [prices, setPrices] = useState<StayAccommodationPriceDto[]>([]);
  const [loading, setLoading] = useState(true);

  // 폼 상태
  const [memberIds, setMemberIds] = useState<string[]>([]);
  const [durationMonths, setDurationMonths] = useState(1);

  const [leaderId, setLeaderId] = useState<number | null>(null);

  // 팀 인원 (대표자 + 실제 입력된 팀원)
  const totalMembers = memberIds.filter((id) => id.trim() !== '').length + 1;

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
    // userId 획득 + 숙소 정보 병렬 조회
    Promise.all([
      api.get<UserResp>('/api/users'),
      api.get<StayAccommodationDto>(`/api/stay/accommodations/${accommodationId}`),
    ])
      .then(([userData, accommodationData]) => {
        console.log('유저 데이터:', userData);
        console.log('숙소 데이터:', accommodationData);
        setLeaderId(userData.userId);
        setAccommodation(accommodationData);
        setPrices(accommodationData.prices ?? []);
      })
      .catch((err) => console.log('데이터 조회 실패:', err))
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

    api.post<unknown>(`/api/waiting/apply/${leaderId}`, body)
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
          <input className={styles.input} value={leaderId ?? ''} disabled />
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
              <button
                className="btn-danger"
                onClick={() => removeMember(index)}
              >
                삭제
              </button>
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
