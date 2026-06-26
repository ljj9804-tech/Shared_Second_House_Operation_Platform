import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class DeliveryAdminScreen extends StatefulWidget {
  const DeliveryAdminScreen({Key? key}) : super(key: key);

  @override
  State<DeliveryAdminScreen> createState() => _DeliveryAdminScreenState();
}

class _DeliveryAdminScreenState extends State<DeliveryAdminScreen> {
  List<dynamic> orders = [];
  bool isLoading = true;

  @override
  void initState() {
    super.initState();
    fetchOrders();
  }

  Future<void> fetchOrders() async {
    try {
      // 안드로이드 에뮬레이터 로컬 서버 접근 IP
      final response = await http.get(Uri.parse('http://10.0.2.2:8080/api/orders/admin'));
      if (response.statusCode == 200) {
        setState(() {
          orders = json.decode(utf8.decode(response.bodyBytes)); // 한글 깨짐 방지
          isLoading = false;
        });
      }
    } catch (e) {
      debugPrint("에러 발생: $e");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('관리자 배송/주문 관리')),
      body: isLoading
          ? const Center(child: CircularProgressIndicator())
          : ListView.builder(
        itemCount: orders.length,
        itemBuilder: (context, index) {
          final order = orders[index];
          return Card(
            child: ListTile(
              title: Text('주문번호: SH-2026-${order['order_id']}'),
              subtitle: Text('상태: ${order['status']}'),
            ),
          );
        },
      ),
    );
  }
}