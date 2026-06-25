import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'cart_screen.dart'; // 장바구니 화면 경로에 맞게 임포트하세요

class ProductListScreen extends StatefulWidget {
  const ProductListScreen({Key? key}) : super(key: key);

  @override
  State<ProductListScreen> createState() => _ProductListScreenState();
}

class _ProductListScreenState extends State<ProductListScreen> {
  // 백엔드 데이터와 동기화된 플러터 엄선 dummyProducts 리스트
  final List<Map<String, dynamic>> dummyProducts = [
    {'id': 101, 'name': '프리미엄 바비큐 세트', 'price': 45000, 'desc': '세컨하우스에서 즐기는 최고의 바비큐'},
    {'id': 102, 'name': '지역 특산물 밀키트', 'price': 18000, 'desc': '신선한 현지 재료로 만든 간편 밀키트'},
    {'id': 103, 'name': '유기농 조식 바구니', 'price': 25000, 'desc': '아침을 깨우는 건강한 유기농 식단'},
    {'id': 104, 'name': '감성 불멍 장작 세트', 'price': 15000, 'desc': '따뜻한 캠핑 감성을 위한 오로라 장작'},
  ];

  // 실시간 장바구니 담긴 수량 관리 변수
  int cartItemCount = 0;

  void _addCartItem(Map<String, dynamic> product) {
    setState(() {
      cartItemCount++;
    });
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('${product['name']}이(가) 장바구니에 추가되었습니다.'),
        duration: const Duration(seconds: 1),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          '푸드 & 서비스 스토어',
          style: TextStyle(fontWeight: FontWeight.bold, color: Colors.white),
        ),
        centerTitle: true,
        backgroundColor: const Color(0xFFF97316), // 시그니처 오렌지 컬러 테마 적용
        elevation: 0,
        actions: [
          // 우측 상단 장바구니 아이콘 + 카운트 배지 스택 레이아웃
          Stack(
            alignment: Alignment.center,
            children: [
              IconButton(
                icon: const Icon(Icons.shopping_cart_outlined, color: Colors.white),
                tooltip: '장바구니 이동',
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) => const CartScreen()), // 장바구니 전용 스크린으로 이동
                  );
                },
              ),
              if (cartItemCount > 0)
                Positioned(
                  top: 8,
                  right: 8,
                  child: Container(
                    padding: const EdgeInsets.all(2),
                    decoration: BoxDecoration(
                      color: Colors.red,
                      borderRadius: BorderRadius.circular(10),
                    ),
                    constraints: const BoxConstraints(
                      minWidth: 16,
                      minHeight: 16,
                    ),
                    // 🔴 기존의 문법 에러(TextAlign 소문자 오류 및 타입 오류) 해결 완료!
                    child: Text(
                      '$cartItemCount',
                      style: const TextStyle(
                        color: Colors.white,
                        fontSize: 10,
                        fontWeight: FontWeight.bold,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ),
                ),
            ],
          ),
          const SizedBox(width: 8),
        ],
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(12),
        itemCount: dummyProducts.length,
        itemBuilder: (ctx, i) {
          final prod = dummyProducts[i];
          return Card(
            elevation: 2,
            margin: const EdgeInsets.symmetric(vertical: 6),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(12),
            ),
            child: ListTile(
              contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              leading: Container(
                width: 50,
                height: 50,
                decoration: BoxDecoration(
                  color: Colors.grey[200],
                  borderRadius: BorderRadius.circular(8),
                ),
                child: const Icon(Icons.fastfood, color: Color(0xFFF97316)),
              ),
              title: Text(
                prod['name'],
                style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
              ),
              subtitle: Padding(
                padding: const EdgeInsets.only(top: 4.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      prod['desc'],
                      style: TextStyle(color: Colors.grey[600], fontSize: 13),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                    const SizedBox(height: 4),
                    Text(
                      '${prod['price'].toString().replaceAllMapped(RegExp(r'(\d{1,3})(?=(\d{3})+(?!\d))'), (Match m) => '${m[1]},')}원',
                      style: const TextStyle(
                        color: Colors.orange,
                        fontWeight: FontWeight.bold,
                        fontSize: 15,
                      ),
                    ),
                  ],
                ),
              ),
              trailing: ElevatedButton(
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF1E293B),
                  foregroundColor: Colors.white,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                ),
                onPressed: () => _addCartItem(prod),
                child: const Text('담기', style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold)),
              ),
            ),
          );
        },
      ),
    );
  }
}