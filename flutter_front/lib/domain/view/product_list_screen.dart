import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../controller/cart_controller.dart';
import 'cart_screen.dart';

class ProductListScreen extends StatelessWidget {
  const ProductListScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    // 💡 테스트용 고정 상품 데이터 세트 (스프링 부트 DB의 sh_product 더미데이터와 ID 매칭)
    final List<Map<String, dynamic>> dummyProducts = [
      {'id': 1, 'name': '[추천] 오션뷰 세컨하우스 1박 이용권', 'price': 150000},
      {'id': 2, 'name': '[조식] 수제 샌드위치 & 커피 세트', 'price': 12000},
      {'id': 3, 'name': '[시그니처] 대나무 바베큐 플래터', 'price': 45000},
      {'id': 4, 'name': '[가정간편식] 얼큰 차돌된장찌개', 'price': 18000},
      {'id': 5, 'name': '콜라 / 사이다 500ml캔', 'price': 2500},
    ];

    final cart = Provider.of<CartController>(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text('🏡 세컨하우스 푸드·서비스'),
        backgroundColor: Colors.orange,
        actions: [
          // 🛒 우측 상단 장바구니 아이콘 배지 버튼
          Stack(
            alignment: Alignment.center,
            children: [
              IconButton(
                icon: const Icon(Icons.shopping_cart),
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) => const CartScreen()),
                  );
                },
              ),
              if (cart.items.isNotEmpty)
                Positioned(
                  right: 8,
                  top: 8,
                  child: Container(
                    padding: const EdgeInsets.all(2),
                    decoration: BoxDecoration(
                      color: Colors.red,
                      borderRadius: BorderRadius.circular(10),
                    ),
                    constraints: const BoxConstraints(minWidth: 16, minHeight: 16),
                    child: Text(
                      '${cart.items.length}',
                      style: const TextStyle(color: Colors.white, fontSize: 10, fontWeight: FontWeight.bold),
                      textAlign: TextAlign.center,
                    ),
                  ),
                ),
            ],
          ),
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
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
            child: ListTile(
              contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              title: Text(prod['name'], style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
              subtitle: Text('${prod['price'].toString().replaceAllMapped(RegExp(r'(\d{3})(?=\d{3})'), (m) => '${m[1]},')}원',
                  style: const TextStyle(color: Colors.orange, fontWeight: FontWeight.w600)),
              trailing: ElevatedButton.icon(
                onPressed: () {
                  // 💡 성규님의 CartController에 담기 액션 실행
                  cart.addToCart(prod['id'], prod['name'], prod['price']);

                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      content: Text('📥 ${prod['name']}이(가) 장바구니에 추가되었습니다.'),
                      duration: const Duration(seconds: 1),
                    ),
                  );
                },
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.orange,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                ),
                icon: const Icon(Icons.add_shopping_cart, size: 16),
                label: const Text('담기'),
              ),
            ),
          );
        },
      ),
    );
  }
}