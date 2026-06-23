/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : app/lib/auth.ts
 * 역할  : axios 인스턴스 및 임시 userId 설정 (API 공통 기반)
 * 사용처 : 모든 페이지/컴포넌트에서 API 호출 시 import
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - .env : NEXT_PUBLIC_SERVER_URL, NEXT_PUBLIC_TEMP_USER_ID 환경변수
 * ----------------------------------------------------------------------------------
 * [기능 목록]
 * - TEMP_USER_ID : .env의 NEXT_PUBLIC_TEMP_USER_ID 값 (기본값 1)
 * - api          : baseURL이 설정된 axios 인스턴스 (전체 API 요청에 사용)
 * ----------------------------------------------------------------------------------
 * [파일 흐름과 순서]
 * import api, { TEMP_USER_ID } from '@/app/lib/auth'
 * → api.get/post/patch 호출 시 baseURL(NEXT_PUBLIC_SERVER_URL) 자동 적용
 * ----------------------------------------------------------------------------------
 * [주의사항 / 참고]
 * ⚠️ [TODO] 로그인 연동 시: interceptors.request 주석 해제 → Bearer 토큰 자동 첨부
 *    TEMP_USER_ID 사용 제거 → userDetails에서 userId 추출로 교체
 * ==================================================================================
 */

import axios from 'axios';

// ===== 로그인 연동 시 이 파일만 수정하면 됩니다 =====
// .env 파일에 NEXT_PUBLIC_TEMP_USER_ID=본인_userId 추가하면 됩니다.
export const TEMP_USER_ID = Number(process.env.NEXT_PUBLIC_TEMP_USER_ID) || 1;

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_SERVER_URL,
});

// [로그인 연동 시 아래 주석 해제]
// api.interceptors.request.use((config) => {
//   const token = localStorage.getItem('accessToken');
//   if (token) config.headers.Authorization = `Bearer ${token}`;
//   return config;
// });

export default api;
