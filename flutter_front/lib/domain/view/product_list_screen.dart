import 'package:flutter/material.dart';
import 'cart_screen.dart';

class ProductListScreen extends StatefulWidget {
  const ProductListScreen({Key? key}) : super(key: key);

  @override
  State<ProductListScreen> createState() => _ProductListScreenState();
}

class _ProductListScreenState extends State<ProductListScreen> {
  // 실제 API 연동 시 Controller의 데이터를 사용하세요.
  final List<Map<String, dynamic>> products = [
    {"id": 1, "name": "프리미엄 한우 바비큐", "price": 85000, "img": "beef.jpg"},
    {"id": 2, "name": "산들바람 샐러드 밀키트", "price": 15000, "img": "salad.jpg"},
    {"id": 3, "name": "제주 감귤 주스 세트", "price": 12000, "img": "juice.jpg"},
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('배달 상품 스토어'),
        backgroundColor: const Color(0xFF2E6F40),
        actions: [
          IconButton(
            icon: const Icon(Icons.shopping_basket),
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => const CartScreen()),
              );
            },
          ),
        ],
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: products.length,
        itemBuilder: (context, index) {
          final p = products[index];
          return Card(
            elevation: 2,
            margin: const EdgeInsets.only(bottom: 16),
            child: ListTile(
              contentPadding: const EdgeInsets.all(10),
              leading: Container(width: 60, height: 60, color: Colors.grey[200], child: const Icon(Icons.fastfood)),
              title: Text(p['name'], style: const TextStyle(fontWeight: FontWeight.bold)),
              subtitle: Text("${p['price']}원"),
              trailing: ElevatedButton(
                onPressed: () {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text('${p['name']}이 장바구니에 담겼습니다.')),
                  );
                },
                style: ElevatedButton.styleFrom(backgroundColor: Colors.orange),
                child: const Text('담기'),
              ),
            ),
          );
        },
      ),
    );
  }
}