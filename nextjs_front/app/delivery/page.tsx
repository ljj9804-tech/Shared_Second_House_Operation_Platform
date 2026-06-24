'use client';

import { useEffect, useState, useCallback } from 'react';
import { ShDeliveryData } from '@/domain/models/delivery';
import { deliveryService } from '@/domain/services/deliveryService';

export default function DeliveryAdminPage() {
  const [orders, setOrders] = useState<ShDeliveryData[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // 💡 해결 1: useCallback을 통해 메모이제이션하여 useEffect 내 무한 루프(cascading renders) 완벽 해결
  const loadOrders = useCallback(async () => {
    try {
      setIsLoading(true);
      setError(null);
      
      const data = await deliveryService.getAdminOrders();
      setOrders(data);
    } catch (err: any) {
      console.error(err);
      setError(err.message || '백엔드 서버 상태 및 CORS 설정을 확인하세요.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    loadOrders();
  }, [loadOrders]);

  // 배달 진행 상황별 Tailwind CSS 배지 스타일 매핑 함수
  const getStatusStyle = (status: string) => {
    switch (status) {
      case '주문대기':
        return 'bg-amber-100 text-amber-800 border-amber-200';
      case '배송중':
        return 'bg-blue-100 text-blue-800 border-blue-200';
      case '배송완료':
        return 'bg-emerald-100 text-emerald-800 border-emerald-200';
      default:
        return 'bg-slate-100 text-slate-800 border-slate-200';
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 p-8">
      <div className="max-w-6xl mx-auto">
        
        {/* 상단 통합 제어 컨트롤러 */}
        <div className="flex justify-between items-center mb-8 bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
          <div>
            <h1 className="text-2xl font-extrabold text-slate-800 tracking-tight flex items-center gap-2">
              🚚 관제 센터: 배달 실시간 제어판
            </h1>
            <p className="text-sm text-slate-500 mt-1">
              플러터 모바일에서 접수된 주문 테이블(sh_order) 내역을 모니터링합니다.
            </p>
          </div>
          <button
            onClick={loadOrders}
            className="bg-orange-500 hover:bg-orange-600 text-white font-bold px-5 py-2.5 rounded-xl transition-all active:scale-95 shadow-sm"
          >
            🔄 배달 현황 새로고침
          </button>
        </div>

        {/* 네트워크 상태 안내창 */}
        {error && (
          <div className="mb-6 p-4 bg-rose-50 border border-rose-200 text-rose-700 rounded-xl text-sm font-medium">
            🚨 <strong>연결 지연 경고:</strong> {error}
          </div>
        )}

        {/* 💡 해결 2: 중단 및 누락되었던 모든 div, table, tbody, tr, td 태그들의 쌍을 정상화 */}
        <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
          {isLoading ? (
            <div className="py-20 text-center text-slate-500 font-medium">
              <div className="animate-spin inline-block w-8 h-8 border-[3px] border-current border-t-transparent text-orange-500 rounded-full mb-4"></div>
              <p>실시간 배달 정보를 수신하는 중입니다...</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="bg-slate-50 border-b border-slate-200 text-slate-600 text-sm font-semibold">
                    <th className="p-4 pl-6">주문 고유 ID</th>
                    <th className="p-4">고객 번호</th>
                    <th className="p-4">배달 목적지 주소</th>
                    <th className="p-4">결제 총액</th>
                    <th className="p-4 text-center">현재 배달 상태</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 text-slate-700">
                  {orders.length === 0 ? (
                    <tr>
                      <td colSpan={5} className="p-16 text-center text-slate-400 font-medium">
                        현재 플랫폼 내에 접수된 원격 배달 요청 건이 없습니다.
                      </td>
                    </tr>
                  ) : (
                    orders.map((order) => (
                      <tr key={order.orderId} className="hover:bg-slate-50/40 transition-colors">
                        <td className="p-4 pl-6 font-bold text-slate-900">#{order.orderId}</td>
                        <td className="p-4 text-sm text-slate-500">{order.userId}번 회원</td>
                        <td className="p-4 text-sm font-medium text-slate-800">{order.deliveryAddress}</td>
                        <td className="p-4 text-sm font-bold text-slate-900">
                          {order.totalAmount.toLocaleString()}원
                        </td>
                        <td className="p-4 text-center">
                          <span className={`px-3 py-1 text-xs font-bold rounded-full border ${getStatusStyle(order.status)}`}>
                            {order.status}
                          </span>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>

      </div>
    </div>
  );
}