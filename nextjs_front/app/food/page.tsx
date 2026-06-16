'use client';

import Script from 'next/script';
import { useCallback, useEffect, useRef, useState } from 'react';
import styles from './page.module.css';

/// 🍽️ 주변 맛집 1건 (GET /api/places/restaurants 의 PlaceDTO 규격)
interface PlaceDto {
  id: string;
  name: string;
  address: string;
  latitude: number;
  longitude: number;
  rating?: number | null;
  userRatingCount?: number | null;
  priceLevel?: string | null;
  googleMapsUri?: string;
  primaryTypeName?: string | null;
  businessStatus?: string | null;
}

// 🏠 숙소 좌표 고정 (테스트용 - 부산 해운대 인근)
const HOUSE_LAT = 35.1587;
const HOUSE_LNG = 129.1604;

// eslint-disable-next-line @typescript-eslint/no-explicit-any
type GMap = any;

export default function GuestRestaurantMapPage() {
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // 맵/마커는 React 상태가 아닌 ref로 관리 (리렌더 불필요)
  const mapRef = useRef<GMap>(null);
  const loadedRef = useRef(false); // 한 번이라도 받아왔는지 (캐시 — Places 호출당 과금 방지)

  /// 마커 말풍선에 띄울 평점/주소 요약
  const buildSnippet = (p: PlaceDto) => {
    const rating =
      p.rating != null ? `⭐ ${p.rating} (${p.userRatingCount ?? 0})` : '평점 없음';
    return `${rating} · ${p.address}`;
  };

  /// 🍽️ 맛집 목록 로드 → 마커 표시. 이미 받아왔으면 캐시 재사용.
  const loadRestaurants = useCallback(async (google: GMap, map: GMap) => {
    if (loadedRef.current) return;

    setIsLoading(true);
    setError(null);

    try {
      const res = await fetch(
        `${process.env.NEXT_PUBLIC_SERVER_URL}/api/places/restaurants?lat=${HOUSE_LAT}&lng=${HOUSE_LNG}&radius=1000&limit=10`
      );
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const places = (await res.json()) as PlaceDto[];

      const infowindow = new google.maps.InfoWindow();

      for (const p of places) {
        const marker = new google.maps.Marker({
          map,
          position: { lat: p.latitude, lng: p.longitude },
          title: p.name,
        });

        // 마커 클릭 → 말풍선(이름·평점·주소 + 구글지도 링크) 표시
        google.maps.event.addListener(marker, 'click', () => {
          const link = p.googleMapsUri
            ? `<a href="${p.googleMapsUri}" target="_blank" rel="noopener noreferrer" style="color:#23399d;font-weight:600;">구글지도에서 보기 ↗</a>`
            : '<span style="color:#888;">상세 정보 주소가 없어요 😢</span>';
          infowindow.setContent(
            `<div style="padding:4px 6px;font-size:13px;max-width:220px;line-height:1.5;">` +
              `<strong>${p.name}</strong><br/>` +
              `<span style="color:#555;">${buildSnippet(p)}</span><br/>${link}` +
              `</div>`
          );
          infowindow.open(map, marker);
        });
      }

      loadedRef.current = true;
    } catch (err) {
      console.log('🔴 [맛집] 로드 실패:', err);
      setError('맛집 정보를 가져오지 못했어요. 😢');
    } finally {
      setIsLoading(false);
    }
  }, []);

  /// 구글맵 SDK 로드 완료 → 지도 + 숙소 마커 생성 후 맛집 로드
  const initMap = useCallback(() => {
    const google = (window as unknown as { google: GMap }).google;
    if (!google || !google.maps) return;

    const container = document.getElementById('restaurant-map');
    if (!container || mapRef.current) return;

    const houseLatLng = { lat: HOUSE_LAT, lng: HOUSE_LNG };
    const map = new google.maps.Map(container, {
      center: houseLatLng,
      zoom: 15,
      mapTypeControl: false,
      streetViewControl: false,
      fullscreenControl: false,
    });
    mapRef.current = map;

    // 🏠 숙소 마커 (파란색으로 강조)
    new google.maps.Marker({
      map,
      position: houseLatLng,
      title: '🏠 숙소 (세컨하우스)',
      icon: 'https://maps.google.com/mapfiles/ms/icons/blue-dot.png',
    });

    loadRestaurants(google, map);
  }, [loadRestaurants]);

  // 스크립트가 이미 로드된 상태(클라이언트 이동·Fast Refresh)에선 Script의 콜백이
  // 다시 안 터질 수 있어, 마운트 시 google이 있으면 직접 초기화한다.
  useEffect(() => {
    if ((window as unknown as { google?: GMap }).google?.maps) initMap();
  }, [initMap]);

  return (
    <div className={styles.screen}>
      <header className={styles.appBar}>주변 맛집</header>

      <div className={styles.mapWrap}>
        <Script
          src={`https://maps.googleapis.com/maps/api/js?key=${process.env.NEXT_PUBLIC_GOOGLE_MAP_KEY}`}
          strategy="afterInteractive"
          onReady={initMap}
        />
        <div id="restaurant-map" className={styles.map} />

        {isLoading && (
          <div className={styles.overlay}>
            <span className={styles.spinner} />
          </div>
        )}
        {error && !isLoading && <div className={styles.overlay}>{error}</div>}
      </div>
    </div>
  );
}
