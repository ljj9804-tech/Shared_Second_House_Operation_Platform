'use client';

import Script from 'next/script';
import styles from './LocationMap.module.css';

interface LocationMapProps {
  address: string;
  latitude: number;
  longitude: number;
}

export default function LocationMap({ address, latitude, longitude }: LocationMapProps) {

  const initMap = () => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const kakao = (window as any).kakao;
    if (!kakao || !kakao.maps) return;

    kakao.maps.load(() => {
      const container = document.getElementById('kakao-map');
      if (!container) return;

      const options = {
        center: new kakao.maps.LatLng(latitude, longitude),
        level: 3,
      };

      const map = new kakao.maps.Map(container, options);
      const markerPosition = new kakao.maps.LatLng(latitude, longitude);
      const marker = new kakao.maps.Marker({ position: markerPosition });
      marker.setMap(map);

      const infowindow = new kakao.maps.InfoWindow({
        content: `<div style="padding:6px 10px; font-size:13px;">${address}</div>`,
      });
      infowindow.open(map, marker);

      console.log('카카오맵 초기화 완료:', latitude, longitude);
    });
  };

  return (
    <section className={styles.section}>
      <h2 className={styles.sectionTitle}>위치 및 주변 시설</h2>
      <p className={styles.address}>{address}</p>

      <Script
        src={`//dapi.kakao.com/v2/maps/sdk.js?appkey=${process.env.NEXT_PUBLIC_KAKAO_MAP_KEY}&libraries=services&autoload=false`}
        strategy="afterInteractive"
        onLoad={initMap}
      />

      <div id="kakao-map" className={styles.map} />
    </section>
  );
}