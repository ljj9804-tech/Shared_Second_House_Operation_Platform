'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';

// 이미지 속성을 포함한 인터페이스 정의
interface CartItemDto {
  productId: number;
  name: string;
  price: number;
  quantity: number;
  img: string; // 이미지 경로 추가
}

export default function CartPage() {
  const router = useRouter();
  const [cartItems, setCartItems] = useState<CartItemDto[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isProcessing, setIsProcessing] = useState(false);

  useEffect(() => {
    // 5개의 상품 데이터에 이미지 경로를 포함시켰습니다.
    const timer = setTimeout(() => {
      setCartItems([
        { productId: 3, name: "[시그니처] 대나무 바베큐 플래터", price: 45000, quantity: 1, img: "bbq.png" },
        { productId: 5, name: "콜라 / 사이다 500ml 캔", price: 2500, quantity: 3, img: "drink.png" },
      ]);
      setIsLoading(false);
    }, 500);

    return () => clearTimeout(timer);
  }, []);

  const totalAmount = cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0);

  const handleQuantityChange = (productId: number, newQuantity: number) => {
    if (newQuantity < 1) return;
    setCartItems((prev) =>
      prev.map((item) => (item.productId === productId ? { ...item, quantity: newQuantity } : item))
    );
  };

  const handleRemoveItem = (productId: number) => {
    setCartItems((prev) => prev.filter((item) => item.productId !== productId));
  };

  const handleCheckout = async () => {
    if (cartItems.length === 0) return;
    setIsProcessing(true);
    
    // 주문 완료 후 배달 관리 페이지로 이동하는 로직 (경로 확인 필요)
    alert('🎉 주문이 성공적으로 접수되었습니다!');
    setCartItems([]);
    router.push('/delivery'); 
    setIsProcessing(false);
  };

  if (isLoading) return <div style={{ textAlign: 'center', marginTop: '50px' }}>장바구니 정보를 불러오는 중...</div>;

  return (
    <div style={{ maxWidth: '800px', margin: '40px auto', padding: '0 20px', fontFamily: 'sans-serif' }}>
      <h1 style={{ fontSize: '24px', fontWeight: 'bold', marginBottom: '30px' }}>🛒 장바구니</h1>

      {cartItems.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '60px 0', border: '1px dashed #cbd5e1', borderRadius: '12px' }}>
          장바구니가 비어 있습니다.
        </div>
      ) : (
        <div>
          {cartItems.map((item) => (
            <div key={item.productId} style={{ display: 'flex', alignItems: 'center', padding: '16px', borderBottom: '1px solid #eee' }}>
              {/* 이미지 출력 영역 */}
              <img 
                src={`/images/${item.img}`} 
                alt={item.name} 
                style={{ width: '80px', height: '80px', objectFit: 'cover', borderRadius: '8px', marginRight: '16px' }} 
              />
              
              <div style={{ flex: 1 }}>
                <h3 style={{ fontSize: '16px', margin: '0 0 4px 0' }}>{item.name}</h3>
                <p style={{ fontWeight: 'bold', color: '#f97316' }}>{item.price.toLocaleString()}원</p>
              </div>

              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <button onClick={() => handleQuantityChange(item.productId, item.quantity - 1)}>-</button>
                <span>{item.quantity}</span>
                <button onClick={() => handleQuantityChange(item.productId, item.quantity + 1)}>+</button>
                <button onClick={() => handleRemoveItem(item.productId)} style={{ color: 'red', marginLeft: '10px' }}>삭제</button>
              </div>
            </div>
          ))}

          <div style={{ marginTop: '30px', textAlign: 'right' }}>
            <h2>총 결제 금액: {totalAmount.toLocaleString()}원</h2>
            <button 
              onClick={handleCheckout} 
              disabled={isProcessing}
              style={{ padding: '15px 30px', backgroundColor: '#2E6F40', color: 'white', border: 'none', borderRadius: '8px', fontSize: '16px' }}
            >
              {isProcessing ? '주문 처리 중...' : '주문하기'}
            </button>
          </div>
        </div>
      )}
    </div>
  );
}