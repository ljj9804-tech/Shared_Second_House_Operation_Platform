'use client';

'use client';

import { useState } from 'react'; // 🟢 사용하지 않는 useEffect는 제거해도 됩니다!
import { useRouter } from 'next/navigation';

// 상품 인터페이스 정의
interface ProductDto {
  id: number;
  name: string;
  price: number;
  description?: string;
  imageUrl?: string;
}

export default function ProductListPage() {
  const router = useRouter();
  const [cartCount, setCartCount] = useState<number>(0);

  // 🟢 [해결 핵심] useState 초기값에 엄선된 상품 데이터를 직접 할당하여 동기식 setState 호출 문제를 원천 차단합니다!
  const [products] = useState<ProductDto[]>([
    { id: 101, name: '프리미엄 바비큐 세트', price: 45000, description: '세컨하우스에서 즐기는 최고의 바비큐' },
    { id: 102, name: '지역 특산물 밀키트', price: 18000, description: '신선한 현지 재료로 만든 간편 밀키트' },
    { id: 103, name: '유기농 조식 바구니', price: 25000, description: '아침을 깨우는 건강한 유기농 식단' },
    { id: 104, name: '감성 불멍 장작 세트', price: 15000, description: '따뜻한 캠핑 감성을 위한 오로라 장작' },
  ]);

  // 장바구니 담기 핸들러
  const handleAddToCart = (productName: string) => {
    alert(`🛒 ${productName} 상품이 장바구니에 담겼습니다!`);
    setCartCount((prev) => prev + 1);
  };

  // 기존의 isLoading 체크 로직과 useEffect는 이제 필요 없으므로 제거되어 코드가 훨씬 가벼워집니다!
  return (
    <div style={{ maxWidth: '1000px', margin: '0 auto', padding: '20px', fontFamily: 'sans-serif' }}>
      
      {/* 💳 상단 헤더 영역 */}
      <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', paddingBottom: '20px', borderBottom: '1px solid #e2e8f0', marginBottom: '30px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <button 
            onClick={() => router.push('/')} 
            style={{ background: 'none', border: 'none', fontSize: '20px', cursor: 'pointer', color: '#475569' }}
          >
            ⬅️
          </button>
          <div>
            <h1 style={{ fontSize: '24px', fontWeight: 'bold', color: '#1e293b', margin: 0 }}>🛒 푸드 & 서비스 스토어</h1>
            <p style={{ color: '#64748b', fontSize: '13px', margin: '4px 0 0 0' }}>세컨하우스 머무시는 동안 필요한 물품을 주문하세요.</p>
          </div>
        </div>

        {/* 🛒 장바구니 배지 영역 */}
        <div 
          onClick={() => router.push('/cart')}
          style={{ position: 'relative', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', width: '45px', height: '45px', borderRadius: '50%', backgroundColor: '#f1f5f9' }}
        >
          <span style={{ fontSize: '22px' }}>🛒</span>
          {cartCount > 0 && (
            <span style={{
              position: 'absolute', top: '-2px', right: '-2px',
              backgroundColor: '#ef4444', color: '#ffffff',
              fontSize: '10px', fontWeight: 'bold',
              minWidth: '18px', height: '18px', borderRadius: '50%',
              display: 'flex', justifyContent: 'center', alignItems: 'center',
              padding: '0 4px'
            }}>
              {cartCount}
            </span>
          )}
        </div>
      </header>

      {/* 📦 상품 그리드 레이아웃 */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '24px' }}>
        {products.map((product) => (
          <div 
            key={product.id}
            style={{ 
              border: '1px solid #e2e8f0', borderRadius: '16px', overflow: 'hidden', 
              backgroundColor: '#ffffff', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)',
              display: 'flex', flexDirection: 'column', justifyContent: 'space-between',
              padding: '16px', minHeight: '180px'
            }}
          >
            <div>
              <h3 style={{ fontSize: '16px', fontWeight: 'bold', color: '#1e293b', margin: '0 0 6px 0' }}>{product.name}</h3>
              <p style={{ fontSize: '13px', color: '#64748b', margin: '0 0 16px 0', lineHeight: '1.4' }}>{product.description}</p>
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 'auto' }}>
              <span style={{ fontSize: '18px', fontWeight: 'bold', color: '#f97316' }}>
                {product.price.toLocaleString()}원
              </span>
              
              <button
                onClick={() => handleAddToCart(product.name)}
                style={{
                  padding: '8px 14px', backgroundColor: '#1e293b', color: '#ffffff',
                  border: 'none', borderRadius: '8px', fontSize: '13px', fontWeight: 'bold',
                  cursor: 'pointer'
                }}
              >
                담기
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}