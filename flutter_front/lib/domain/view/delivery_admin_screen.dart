import 'package:flutter/material.dart';
import 'package:flutter_front/domain/view/delivery_admin_detail_screen.dart';

class DeliveryAdminScreen extends StatefulWidget {
  const DeliveryAdminScreen({super.key});

  @override
  State<DeliveryAdminScreen> createState() => _DeliveryAdminScreenState();
}

class _DeliveryAdminScreenState extends State<DeliveryAdminScreen> {
  final List<Map<String, dynamic>> adminOrders = [
    {
      'orderId': 'SH-2026-0001',
      'status': '배송 준비 중',
      'totalAmount': '49,800원',
    },
    {
      'orderId': 'SH-2026-0002',
      'status': '배송 중',
      'totalAmount': '125,000원',
    },
    {
      'orderId': 'SH-2026-0003',
      'status': '배송 완료',
      'totalAmount': '32,000원',
    },
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('관리자 배송/주문 관리'),
        backgroundColor: const Color(0xFF2E6F40),
        foregroundColor: Colors.white,
      ),
      body: adminOrders.isEmpty
          ? const Center(
        child: Text(
          '처리할 주문 내역이 없습니다.',
          style: TextStyle(fontSize: 16, color: Colors.grey),
        ),
      )
          : ListView.builder(
        padding: const EdgeInsets.all(16.0),
        itemCount: adminOrders.length,
        itemBuilder: (context, index) {
          final order = adminOrders[index];
          return Card(
            margin: const EdgeInsets.only(bottom: 12.0),
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
            elevation: 2,
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              key: ValueKey(order['orderId']),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        '주문번호: ${order['orderId']}',
                        style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        '상태: ${order['status']}',
                        style: const TextStyle(fontSize: 14, color: Colors.blueAccent, fontWeight: FontWeight.w500),
                      ),
                    ],
                  ),
                  ElevatedButton(
                    onPressed: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => DeliveryAdminDetailScreen(
                            order: adminOrders[index],
                            onDelete: () {
                              setState(() {
                                adminOrders.removeAt(index);
                              });
                            },
                            // 💡 여기에 이 코드를 한 줄 추가해주면 리스트 상태도 실시간 연동됩니다!
                            onStatusChange: (newStatus) {
                              setState(() {
                                adminOrders[index]['status'] = newStatus;
                              });
                            },
                          ),
                        ),
                      );
                    },
                    child: const Text('상세보기'),
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }
}