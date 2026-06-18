"use client";

import styles from "./LocationMap.module.css";
import { useEffect, useState } from "react";
import {
  AdvancedMarker,
  APIProvider,
  ControlPosition,
  InfoWindow,
  Map,
  MapControl,
  Pin,
  useMap,
} from "@vis.gl/react-google-maps";

interface PlaceDto {
  id: string;
  name: string;
  address: string;
  latitude: number;
  longitude: number;
  rating?: number | null;
  userRatingCount?: number | null;
  googleMapsUri?: string;
}

interface LocationMapProps {
  latitude: number;
  longitude: number;
}

export default function LocationMap({ latitude, longitude }: LocationMapProps) {
  const [places, setPlaces] = useState<PlaceDto[]>([]);
  const [selectedPlace, setSelectedPlace] = useState<PlaceDto | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function fetchRestaurants() {
      try {
        const res = await fetch(
          `${process.env.NEXT_PUBLIC_SERVER_URL}/api/places/restaurants?lat=${latitude}&lng=${longitude}&radius=1000&limit=10`,
        );
        if (!res.ok) throw new Error();
        const data = await res.json();
        setPlaces(data);
      } catch {
        setError("맛집 정보를 가져오지 못했어요. 😢");
      } finally {
        setIsLoading(false);
      }
    }
    fetchRestaurants();
  }, [latitude, longitude]);

  return (
    <div className={styles.screen}>
      <header className={styles.appBar}>주변 맛집</header>

      <div className={styles.mapWrap}>
        {/* 1. API 키 설정 */}
        <APIProvider apiKey={process.env.NEXT_PUBLIC_GOOGLE_MAP_KEY || ""}>
          {/* 2. 지도 띄우기 */}
          <Map
            defaultCenter={{ lat: latitude, lng: longitude }}
            defaultZoom={15}
            disableDefaultUI={true} // 기존의 구글 기본 버튼들(로드뷰 등) 한방에 숨기기
            className={styles.map}
            mapId={process.env.NEXT_PUBLIC_GOOGLE_MAP_ID || "DEMO_MAP_ID"}
            gestureHandling={"greedy"} //인삿말 안하기
            scrollwheel={false} // 1. 휠로 확대되는 건 끈다! (스크롤 방해 금지)
            zoomControl={true} // 3. 👈 확대/축소 (+, -) 버튼 딱 이거 하나만 우측 하단에 띄운다!
          >
            {/* 🏠 숙소 마커 (파란색으로 강조) */}
            <AdvancedMarker
              position={{ lat: latitude, lng: longitude }}
              title="🏠 숙소"
            >
              <Pin
                background="#4285F4"
                borderColor="#1a73e8"
                glyphColor="#ffffff"
              />
            </AdvancedMarker>

            {/* 🍽️ 맛집 마커들 반복문 돌리기 */}
            {places.map((p) => (
              <AdvancedMarker
                key={p.id}
                position={{ lat: p.latitude, lng: p.longitude }}
                title={p.name}
                onClick={() => setSelectedPlace(p)} // 클릭하면 이 맛집을 선택!
              />
            ))}

            {/* 💬 마커 클릭 시 나타날 말풍선 (InfoWindow) */}
            {selectedPlace && (
              <InfoWindow
                position={{
                  lat: selectedPlace.latitude,
                  lng: selectedPlace.longitude,
                }}
                onCloseClick={() => setSelectedPlace(null)} // 닫기 버튼 누르면 초기화
              >
                <div
                  style={{
                    padding: "4px",
                    fontSize: "13px",
                    maxWidth: "220px",
                    color: "#000",
                  }}
                >
                  <strong>{selectedPlace.name}</strong>
                  <br />
                  <span style={{ color: "#555" }}>
                    ⭐ {selectedPlace.rating ?? "0"} (
                    {selectedPlace.userRatingCount ?? 0}) ·{" "}
                    {selectedPlace.address}
                  </span>
                  <br />
                  {selectedPlace.googleMapsUri ? (
                    <a
                      href={selectedPlace.googleMapsUri}
                      target="_blank"
                      rel="noopener noreferrer"
                      style={{ color: "#23399d", fontWeight: 600 }}
                    >
                      구글지도에서 보기 ↗
                    </a>
                  ) : (
                    <span style={{ color: "#888" }}>상세 정보 없음 😢</span>
                  )}
                </div>
              </InfoWindow>
            )}
            {/* 🎯 숙소 위치로 돌아오는 버튼 (구글맵 컨트롤 레이어 우상단에 얹는다) */}
            <MapControl position={ControlPosition.RIGHT_TOP}>
              <RecenterButton latitude={latitude} longitude={longitude} />
            </MapControl>
          </Map>
        </APIProvider>

        {/* 로딩 및 에러 처리 */}
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
/// 🎯 클릭하면 숙소 위치로 부드럽게 이동하는 버튼 컴포넌트
function RecenterButton({ latitude, longitude }: LocationMapProps) {
  const map = useMap(); // 👈 현재 켜져있는 구글 맵 객체를 가져오는 치트키야!

  const handleRecenter = () => {
    if (!map) return;

    // 지도의 중심을 숙소 좌표로 부드럽게(panTo) 이동시켜줘!
    map.panTo({ lat: latitude, lng: longitude });
    // 원한다면 줌 크기도 원래대로(15) 리셋 가능!
    map.setZoom(15);
  };

  return (
    <button
      type="button"
      onClick={handleRecenter}
      style={{
        margin: "10px",
        padding: "10px 14px",
        backgroundColor: "#ffffff",
        border: "none",
        borderRadius: "8px",
        boxShadow: "0 2px 6px rgba(0,0,0,0.3)",
        cursor: "pointer",
        fontSize: "14px",
        fontWeight: "bold",
        color: "#333",
        display: "flex",
        alignItems: "center",
        gap: "4px",
      }}
    >
      🏠 숙소 위치로
    </button>
  );
}
