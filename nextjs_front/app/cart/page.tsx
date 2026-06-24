'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import styles from './cart.module.css'; // 필요 시 스타일 파일 생성 또는 인라인 스타일 활용

// 플러터의 DTO 구조를 기반으로 정의한 장바구니 아이템 인터페이스
interface CartItemDto {
  productId: number;
  name: string;
  price: number;
  quantity: number;
}

export default function CartPage() {
  const router = useRouter();
  const [cartItems, setCartItems] = useState<CartItemDto[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isProcessing, setIsProcessing] = useState(false);

  // 1. 초기 더미 데이터 로드 (추후 백엔드 API 연동 시 fetch로 변경 가능)
  useEffect(() => {
    // 성규님의 플러터 dummyProducts 및 주문 테스트 구조를 반영한 샘플 데이터
    const timer = setTimeout(() => {
      setCartItems([
        { productId: 101, name: '프리미엄 바비큐 세트', price: 45000, quantity: 2 },
        { productId: 102, name: '지역 특산물 밀키트', price: 18000, quantity: 1 },
      ]);
      setIsLoading(false);
    }, 500);

    return () => clearTimeout(timer);
  }, []);

  // 2. 총 금액 계산 로직
  const totalAmount = cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0);

  // 3. 수량 변경 핸들러
  const handleQuantityChange = (productId: number, newQuantity: number) => {
    if (newQuantity < 1) return;
    setCartItems((prev) =>
      prev.map((item) => (item.productId === productId ? { ...item, quantity: newQuantity } : item))
    );
  };

  // 4. 아이템 삭제 핸들러
  const handleRemoveItem = (productId: number) => {
    setCartItems((prev) => prev.filter((item) => item.productId !== productId));
  };

  // 5. 플러터의 _handleCheckout 기능 이식: 주문서 서버 전송
  const handleCheckout = async () => {
    if (cartItems.length === 0) return;

    setIsProcessing(true);
    try {
      // 스프링 백엔드 order-controller 스펙과 일치하는 JSON 페이로드 구성
      const orderPayload = {
        userId: 9007199254740991, // 샘플 고유 ID
        deliveryAddress: "부산광역시 세컨하우스 지정 숙소", // 배송지 기본값
        totalAmount: totalAmount,
        items: cartItems.map((item) => ({
          productId: item.productId,
          quantity: item.quantity,
          price: item.price,
        })),
      };

      const response = await fetch(`${process.env.NEXT_PUBLIC_SERVER_URL}/api/orders`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(orderPayload),
      });

      if (response.ok) {
        alert('🎉 주문이 성공적으로 접수되었습니다!');
        setCartItems([]); // 장바구니 비우기
        router.push('/delivery'); // 주문 완료 후 배달 관리 콘솔로 이동
      } else {
        alert('주문 처리 중 서버 에러가 발생했습니다.');
      }
    } catch (error) {
      console.error('❌ [Checkout Error]:', error);
      alert('네트워크 오류로 주문에 실패했습니다.');
    } finally {
      setIsProcessing(false);
    }
  };

  if (isLoading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh', fontSize: '16px', fontWeight: 'bold', color: '#64748b' }}>
        장바구니 정보를 불러오는 중...
      </div>
    );
  }

  return (
    <div style={{ maxWidth: '800px', margin: '40px auto', padding: '0 20px', fontFamily: 'sans-serif' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '10px' }}>
        <button 
          onClick={() => router.push('/')} 
          style={{ background: 'none', border: 'none', fontSize: '20px', cursor: 'pointer', color: '#334155' }}
        >
          ⬅️
        </button>
        <h1 style={{ fontSize: '24px', fontWeight: 'bold', color: '#1e293b', margin: 0 }}>🛒 장바구니 주문 센터</h1>
      </div>
      <p style={{ color: '#64748b', fontSize: '14px', marginBottom: '30px' }}>선택하신 상품 목록을 확인하고 주문을 진행해 주세요.</p>

      {cartItems.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '60px 0', border: '1px dashed #cbd5e1', borderRadius: '12px', color: '#94a3b8' }}>
          <div style={{ fontSize: '40px', marginBottom: '16px' }}>🛒</div>
          <p style={{ fontSize: '16px', fontWeight: '500', margin: 0 }}>장바구니에 담긴 상품이 없습니다.</p>
          <button 
            onClick={() => router.push('/')}
            style={{ marginTop: '20px', padding: '10px 20px', backgroundColor: '#f1f5f9', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold', color: '#475569' }}
          >
            쇼핑하러 가기
          </button>
        </div>
      ) : (
        <div>
          {/* 장바구니 아이템 리스트 (플러터의 Card & ListTile 매핑 구조) */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', marginBottom: '24px' }}>
            {cartItems.map((item) => (
              <div 
                key={item.productId} 
                style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '16px', border: '1px solid #e2e8f0', borderRadius: '12px', backgroundColor: '#ffffff', boxShadow: '0 1px 3px rgba(0,0,0,0.02)' }}
              >
                <div>
                  <h3 style={{ fontSize: '16px', fontWeight: 'bold', color: '#1e293b', margin: '0 0 6px 0' }}>{item.name}</h3>
                  <p style={{ fontSize: '14px', color: '#f97316', fontWeight: 'bold', margin: 0 }}>
                    {item.price.toLocaleString()}원 <span style={{ color: '#94a3b8', fontWeight: 'normal', fontSize: '12px' }}>x {item.quantity}</span>
                  </p>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
                  {/* 수량 조절 버튼 */}
                  <div style={{ display: 'flex', alignItems: 'center', border: '1px solid #cbd5e1', borderRadius: '6px', overflow: 'hidden' }}>
                    <button 
                      onClick={() => handleQuantityChange(item.productId, item.quantity - 1)}
                      style={{ padding: '6px 12px', border: 'none', backgroundColor: '#f8fafc', cursor: 'pointer' }}
                    >
                      -
                    </button>
                    <span style={{ padding: '0 12px', fontSize: '14px', fontWeight: 'bold' }}>{item.quantity}</span>
                    <button 
                      onClick={() => handleQuantityChange(item.productId, item.quantity + 1)}
                      style={{ padding: '6px 12px', border: 'none', backgroundColor: '#f8fafc', cursor: 'pointer' }}
                    >
                      +
                    </button>
                  </div>

                  {/* 단품 삭제 버튼 */}
                  <button 
                    onClick={() => handleRemoveItem(item.productId)}
                    style={{ background: 'none', border: 'none', color: '#ef4444', fontSize: '13px', cursor: 'pointer', fontWeight: '500' }}
                  >
                    삭제
                  </button>
                </div>
              </div>
            ))}
          </div>

          {/* 결제 요약 카드 및 결제 버튼 처리 */}
          <div style={{ padding: '20px', border: '1px solid #cbd5e1', borderRadius: '12px', backgroundColor: '#f8fafc' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <span style={{ fontSize: '16px', fontWeight: 'bold', color: '#475569' }}>총 결제 금액</span>
              <span style={{ fontSize: '20px', fontWeight: 'bold', color: '#1e293b' }}>{totalAmount.toLocaleString()}원</span>
            </div>

            {isProcessing ? (
              <button 
                disabled 
                style={{ width: '100%', padding: '14px', backgroundColor: '#94a3b8', color: '#ffffff', border: 'none', borderRadius: '8px', fontWeight: 'bold', fontSize: '16px', cursor: 'not-allowed', display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '8px' }}
              >
                🔄 주문 처리 중...
              </button>
            ) : (
              <button 
                onClick={handleCheckout}
                style={{ width: '100%', padding: '14px', backgroundColor: '#f97316', color: '#ffffff', border: 'none', borderRadius: '8px', fontWeight: 'bold', fontSize: '16px', cursor: 'pointer', boxShadow: '0 2px 4px rgba(249,115,22,0.2)', transition: 'background-color 0.2s' }}
                onMouseOver={(e) => (e.currentTarget.style.backgroundColor = '#ea580c')}
                onMouseOut={(e) => (e.currentTarget.style.backgroundColor = '#f97316')}
              >
                주문하기
              </button>
            )}
          </div>
        </div>
      )}
    </div>
  );
}