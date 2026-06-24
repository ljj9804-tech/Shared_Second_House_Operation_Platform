'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import styles from './page.module.css';
import { StayAccommodationDto } from './accommodations/page';

export default function Home() {
  const router = useRouter();
  const [accommodations, setAccommodations] = useState<StayAccommodationDto[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch(`${process.env.NEXT_PUBLIC_SERVER_URL}/api/stay/accommodations?page=0&size=3`)
      .then((r) => r.json())
      .then((data) => {
        console.log('[Home] 숙소 목록:', data);
        setAccommodations(data.content ?? []);
      })
      .catch((err) => console.log('[Home] 데이터 조회 실패:', err))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className={styles.container}>
      {/* 숙소 목록 섹션 */}
      <section className={styles.section}>
        <h2 className={styles.sectionTitle}>우리가 머물 집</h2>
        <p className={styles.sectionDesc}>지금부터 하나씩 살펴봐요</p>

        {loading ? (
          <div className={styles.loading}>불러오는 중...</div>
        ) : (
          <div className={styles.grid}>
            {accommodations.map((accommodation) => (
              <div
                key={accommodation.id}
                className={styles.card}
                onClick={() =>
                  router.push(`/accommodations/${accommodation.id}`)
                }
              >
                {/* 이미지 */}
                <div className={styles.imageWrap}>
                  {accommodation.imageUrl ? (
                    <img
                      src={`${process.env.NEXT_PUBLIC_SERVER_URL}${accommodation.imageUrl.split(',')[0].trim()}`}
                      alt={accommodation.name}
                      className={styles.image}
                    />
                  ) : (
                    <div className={styles.imagePlaceholder}>이미지 없음</div>
                  )}
                  {/* 이미지 위 지역명 오버레이 */}
                  <div className={styles.overlay}>
                    <span className={styles.region}>
                      {accommodation.address.split(' ').slice(0, 2).join(' ')}
                    </span>
                  </div>
                </div>

                {/* 숙소 정보 */}
                <div className={styles.info}>
                  <h3 className={styles.name}>{accommodation.name}</h3>
                  {/* amenities 앞 3개 해시태그로 표시 */}
                  <div className={styles.tags}>
                    {accommodation.amenities
                      ?.split(',')
                      .slice(0, 3)
                      .map((tag, i) => (
                        <span key={i} className={styles.tag}>
                          #{tag.trim()}
                        </span>
                      ))}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* 🟢 하단 하이라이트 버튼 섹션 (기존 버튼들에 상품 스토어 버튼을 유기적으로 결합) */}
        <div 
          className={styles.moreWrap} 
          style={{ 
            display: 'flex', 
            gap: '15px', 
            justifyContent: 'center', 
            alignItems: 'center',
            flexWrap: 'wrap',
            marginTop: '30px'
          }}
        >
          {/* 1. 숙소 목록 더보기 버튼 */}
          <button
            className="btn-outline"
            onClick={() => router.push('/accommodations')}
            style={{ margin: 0 }}
          >
            숙소 목록 더 보러가기
          </button>

          {/* 2. 🍔 [신규 추가] 푸드 & 서비스 상품 스토어 이동 버튼 */}
          <div 
            onClick={() => router.push('/product')}
            className="cursor-pointer text-slate-700 hover:text-emerald-500 font-bold transition-colors duration-200"
            style={{ 
              padding: '10px 20px', 
              border: '1px solid #cbd5e1', 
              borderRadius: '8px',
              backgroundColor: '#ffffff',
              boxShadow: '0 1px 2px rgba(0,0,0,0.05)',
              display: 'flex',
              alignItems: 'center',
              gap: '6px'
            }}
          >
            🍔 상품 스토어 구경하기
          </div>

          {/* 3. 🛒 장바구니 목록 이동 버튼 */}
          <div 
            onClick={() => router.push('/cart')}
            className="cursor-pointer text-slate-700 hover:text-blue-500 font-bold transition-colors duration-200"
            style={{ 
              padding: '10px 20px', 
              border: '1px solid #cbd5e1', 
              borderRadius: '8px',
              backgroundColor: '#ffffff',
              boxShadow: '0 1px 2px rgba(0,0,0,0.05)',
              display: 'flex',
              alignItems: 'center',
              gap: '6px'
            }}
          >
            🛒 장바구니 목록 이동
          </div>

          {/* 4. 🚚 배달 관리 콘솔 이동 버튼 */}
          <div 
            onClick={() => router.push('/delivery')}
            className="cursor-pointer text-slate-700 hover:text-orange-500 font-bold transition-colors duration-200"
            style={{ 
              padding: '10px 20px', 
              border: '1px solid #cbd5e1', 
              borderRadius: '8px',
              backgroundColor: '#ffffff',
              boxShadow: '0 1px 2px rgba(0,0,0,0.05)',
              display: 'flex',
              alignItems: 'center',
              gap: '6px'
            }}
          >
            🚚 배달 관리 콘솔 이동
          </div>
        </div>
      </section>
    </div>
  );
}