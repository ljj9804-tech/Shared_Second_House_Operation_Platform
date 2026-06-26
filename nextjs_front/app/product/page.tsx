'use client'; // 클라이언트 컴포넌트로 설정

import React from 'react';
import { useRouter } from 'next/navigation';

export default function ProductListPage() {
  const router = useRouter();

  // 1. 상품 데이터 정의
  const products = [
    { id: 1, name: "[추천] 오션뷰 세컨하우스 1박 이용권", price: 150000, image: "ocean.png" },
    { id: 2, name: "[조식] 수제 샌드위치 & 커피 세트", price: 12000, image: "sandwich.png" },
    { id: 3, name: "[시그니처] 대나무 바베큐 플래터", price: 45000, image: "bbq.png" },
    { id: 4, name: "[가정간편식] 얼큰 차돌된장찌개", price: 18000, image: "stew.png" },
    { id: 5, name: "콜라 / 사이다 500ml 캔", price: 2500, image: "drink.png" },
  ];

  const cartCount = 0; // 예시 상태값

  return (
    <div style={{ padding: '24px' }}>
      {/* 장바구니 아이콘 영역 */}
      <div 
        onClick={() => router.push('/cart')}
        style={{ position: 'relative', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', width: '45px', height: '45px', borderRadius: '50%', backgroundColor: '#f0f0f0', marginBottom: '20px' }}
      >
        <span style={{ fontSize: '22px' }}>🛒</span>
        {cartCount > 0 && (
          <span style={{ position: 'absolute', top: '-2px', right: '-2px', backgroundColor: '#ff4444', color: '#ffffff', fontSize: '10px', fontWeight: 'bold', minWidth: '18px', height: '18px', borderRadius: '50%', display: 'flex', justifyContent: 'center', alignItems: 'center', padding: '0 4px' }}>
            {cartCount}
          </span>
        )}
      </div>

      {/* 상품 그리드 레이아웃 */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '24px' }}>
        {products.map((product) => (
          <div 
            key={product.id}
            style={{ border: '1px solid #e2e8f0', borderRadius: '16px', overflow: 'hidden', backgroundColor: '#ffffff', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.1)' }}
          >
            {/* 이미지 출력 부분 */}
            <img 
              src={`/images/${product.image}`} 
              alt={product.name} 
              style={{ width: '100%', height: '180px', objectFit: 'cover' }} 
            />
            
            <div style={{ padding: '16px' }}>
              <h3 style={{ fontSize: '16px', marginBottom: '8px' }}>{product.name}</h3>
              <p style={{ color: '#666', marginBottom: '16px' }}>{product.price.toLocaleString()}원</p>
              
              <button 
                style={{ width: '100%', padding: '10px', backgroundColor: '#2E6F40', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer' }}
                onClick={() => console.log(`${product.name} 담기 클릭`)}
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